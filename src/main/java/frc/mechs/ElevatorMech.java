package frc.mechs;

import frc.config.Config;
import frc.input.Input;
import frc.input.JoystickProfile;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
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


    /** The controller for this elevator */
    private static final XboxController controller = Input.TANK_CONTROL;

    public ElevatorMech() {
        // config motors
        mainMotor = new TalonSRX(Config.getInt("elevator_master"));
        Config.defaultConfigTalon(mainMotor);
        followMotor = new TalonSRX(Config.getInt("elevator_follower"));
        Config.defaultConfigTalon(followMotor);
        followMotor.follow(mainMotor);
        // config speeds
        ballDumpSpeed = Config.getDouble("ball_dump_speed");
        ballDumpTime = Config.getInt("ball_dump_time");
        climbSpeed = Config.getDouble("climb_speed");
        climbTime = Config.getInt("climb_time");
    }

    /** pressing b/a moves elevator up/down for ball dump */
    public void loop() {
        // stop everything if x button is pressed
        if (controller.getXButtonPressed()) {
            stopEverything();
            return;
        }

        double speedToSet = 0;

        // check if the buttons for timed control were released 
        if (controller.getAButtonReleased()) { // down for ball dump
            startTimeMode(-ballDumpSpeed, ballDumpTime);
        } else if (controller.getBButtonReleased()) { // up for ball dump
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
        mainMotor.set(ControlMode.PercentOutput, speedToSet);

    }

    /** stops all the motors and stops timed mode */
    public void stopEverything() {
        inTimeMode = false;
        mainMotor.set(ControlMode.PercentOutput, 0);
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