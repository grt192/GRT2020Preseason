package frc.mechs;

import frc.config.Config;
import frc.input.Input;
import frc.input.JoystickProfile;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;

public class OuttakeMech {
    private TalonSRX motor;

    // variables for timed outtake movement
    /** speed of the timed movement from -1 to 1 */
    private double timedSpeed;
    /** The time at which the timed action started. Used to calculate when to stop */
    private long startTimeMS;
    /** The number of ms to run the timed action for */
    private int runTimeMS;
    /** Whether we are in timed mode */
    private boolean inTimeMode;

    /** The amount of time in ms to run the motor for moving the outtake mech in */
    private int outtakeTimeIn;
    /** The amt of time in ms to run the motor for moving the outtake mech out */
    private int outtakeTimeOut;
    /** The speed to move the motor during the out movement, from -1.0 to 1.0. 
     * The negative of this speed is used to move the mech back in. */
    private double outtakeSpeed;

    /** The controller for this mech */
    private static final XboxController controller = Input.MECH_CONTROL;

    public OuttakeMech() {
        // config motors
        motor = new TalonSRX(Config.getInt("outtake"));
        Config.defaultConfigTalon(motor);
        // config speeds
        outtakeTimeIn = Config.getInt("outtake_time_in");
        outtakeTimeOut = Config.getInt("outtake_time_out");
        outtakeSpeed = Math.min(1.0, Math.max(-1.0, Config.getDouble("outtake_speed")));
    }

    /** Pressing (a) moves the outtake mech out, pressing (b) moves it back in.
     * Pressing (x) stops everything.
     * Pressing the triggers controls manual movement.
    */
    public void loop() {
        // pressing x means stop everything
        if (controller.getXButtonReleased()) {
            stopEverything();
            return;
        }

        double speedToSet;

        // check if the buttons for timed control were pressed
        if (controller.getAButtonReleased()) { // move mech out
            startTimeMode(outtakeSpeed, outtakeTimeOut);
        } else if (controller.getBButtonReleased()) { // move mech in
            startTimeMode(-outtakeSpeed, outtakeTimeIn);
        }

        // check if the trigger was pressed. if so, stop timed control and enter manual control
        double triggerVal = JoystickProfile.applyProfile(
            Math.abs(controller.getTriggerAxis(Hand.kLeft)) - Math.abs(controller.getTriggerAxis(Hand.kRight)));
        if (triggerVal != 0) {
            inTimeMode = false;
            speedToSet = triggerVal;
        } else {
            speedToSet = 0;
        }
        
        if (inTimeMode) {
            if (System.currentTimeMillis() > startTimeMS + runTimeMS) {
                inTimeMode = false;
            } else {
                speedToSet = timedSpeed;
            }
        }
        motor.set(ControlMode.PercentOutput, speedToSet);
    }
    
    /** Stops timed mode, stops the motor */
    public void stopEverything() {
        inTimeMode = false;
        motor.set(ControlMode.PercentOutput, 0);
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