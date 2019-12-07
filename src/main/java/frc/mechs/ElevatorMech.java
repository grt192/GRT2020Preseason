package frc.mechs;

import frc.config.Config;
import frc.input.Input;
import frc.input.JoystickProfile;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.XboxController;

public class ElevatorMech {
    private TalonSRX mainMotor;
    private TalonSRX followMotor;

    // variables for timed elevator movement
    /** speed in percent output -1 to 1 */
    private double timedSpeed;
    /** The time at which the timed action started. Used to calculate when to stop */
    private long startTimeMS;
    /** The number of ms to run the timed action for */
    private int runTimeMS;
    /** Whether we are in timed mode */
    private boolean inTimeMode;

    /** The speed to dump the balls, in motor percent output (0-1.0) */
    private double ballDumpSpeed;
    /** The amt of time in ms to run the motors for ball dump */
    private int ballDumpTime;
    /** The speed for climbing in motor percent output (0-1.0) */
    private double climbSpeed;
    /** The amt of time in ms to run the motors for climb */
    private int climbTime;

    // limit switch variables
    DigitalInput topLimitSwitch;
    DigitalInput bottomLimitSwitch;

    private final boolean UP_IS_POSITIVE;

    /** The controller for this elevator */
    private static final XboxController controller = Input.TANK_CONTROL;

    public ElevatorMech() {
        // config motors
        mainMotor = new TalonSRX(Config.getInt("elevator_master"));
        Config.defaultConfigTalon(mainMotor);
        followMotor = new TalonSRX(Config.getInt("elevator_follower"));
        Config.defaultConfigTalon(followMotor);
        followMotor.follow(mainMotor);
        // config inverted
        UP_IS_POSITIVE = Config.getBoolean("elevator_up_is_positive");
        // config limit switches
        //TODO use config stuff for this
        topLimitSwitch = new DigitalInput(0);
        bottomLimitSwitch = new DigitalInput(1);
        // config speeds
        ballDumpSpeed = Config.getDouble("ball_dump_speed");
        ballDumpTime = Config.getInt("ball_dump_time");
        climbSpeed = Config.getDouble("climb_speed");
        climbTime = Config.getInt("climb_time");
    }

    /** pressing a/b moves elevator up/down for ball dump */
    public void loop() {
        System.out.println("top: " + topLimitSwitch.get());
        System.out.println("bottom: " + bottomLimitSwitch.get());
        // stop everything if x button is pressed
        if (controller.getXButtonPressed()) {
            stopEverything();
            return;
        }

        double speedToSet = 0;

        // check if the buttons for timed control were released 
        if (controller.getBButtonReleased()) { // down for ball dump
            startTimeMode(-ballDumpSpeed, ballDumpTime);
        } else if (controller.getAButtonReleased()) { // up for ball dump
            startTimeMode(ballDumpSpeed, ballDumpTime);
        }

        // check if the trigger was pressed. if so, stop timed control and enter manual control
        double triggerVal = JoystickProfile.applyProfile(
            Math.abs(controller.getTriggerAxis(Hand.kLeft)) - Math.abs(controller.getTriggerAxis(Hand.kRight)));

        if (triggerVal != 0) {
            inTimeMode = false;
            speedToSet = triggerVal;
        }
        
        if (inTimeMode) {
            if (System.currentTimeMillis() > startTimeMS + runTimeMS) {
                inTimeMode = false;
            } else {
                speedToSet = timedSpeed;
            }
        }
        setSpeed(speedToSet);
    }

    /** stops all the motors and stops timed mode */
    public void stopEverything() {
        System.out.println("stopping everything");
        inTimeMode = false;
        mainMotor.set(ControlMode.PercentOutput, 0);
    }

    /** sets the speed of the elevator from -1.0 to 1.0 with
     * consideration to the values of the limit switches */
    public boolean setSpeed(double speedToSet) {
        boolean stoppedSomething = false;
        // check limit switches and constrain speeds
        if ((UP_IS_POSITIVE && topLimitSwitch.get()) 
            || (!UP_IS_POSITIVE && bottomLimitSwitch.get())) {
            // stop timed movements
            stopEverything();
            if (speedToSet > 0) {
                stoppedSomething = true;
            }
            // speed must be negative or zero, otherwise will break elevator
            speedToSet = Math.min(0, speedToSet);
        } else if ((UP_IS_POSITIVE && bottomLimitSwitch.get())
            || (!UP_IS_POSITIVE && topLimitSwitch.get())) {
            // stop timed movements
            stopEverything();
            if (speedToSet < 0) {
                stoppedSomething = true;
            }
            // speed must be positive or zero
            speedToSet = Math.max(0, speedToSet);
        }
        System.out.println("speed: " + speedToSet + ", stopped: " + stoppedSomething);
        mainMotor.set(ControlMode.PercentOutput, speedToSet);
        return stoppedSomething;
    }

    public double getBallDumpSpeed() {
        return ballDumpSpeed;
    }

    /** Sets timedSpeed to speed, timeMS to time, 
     * startMS to the current time in ms, and inTimeMode to true
     * @param speed speed in percent output of the motor
     * @param time time in ms
     */
    private void startTimeMode(double speed, int time) {
        timedSpeed = speed;
        runTimeMS = time;
        startTimeMS = System.currentTimeMillis();
        inTimeMode = true;
    }

}