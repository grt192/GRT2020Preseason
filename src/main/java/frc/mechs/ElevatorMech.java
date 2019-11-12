package frc.mechs;

import frc.config.Config;
import frc.input.Input;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

public class ElevatorMech extends Mech {
    private TalonSRX mainMotor;
    private TalonSRX followMotor;

    /** Total length of the lead screw */
    private final double SCREW_LEN;
    /** Travel distance per revolution of the lead screw, inches */
    private final double DIST_PER_REV;
    /** Encoder ticks in one elevator motor revolution */
    private final int TICKS_PER_REV;
    /** Encoder ticks in moving from one end to the other end of the screw */
    private final int TICKS_PER_SCREW;
    //TODO we may need to add a motor rev. to screw rev. ratio

    /** The controller for this elevator */
    private static final XboxController controller = Input.TANK_XBOX;

    public ElevatorMech() {
        // config motors
        mainMotor = new TalonSRX(Config.getInt("elevator_master"));
        configTalon(mainMotor);
        followMotor = new TalonSRX(Config.getInt("elevator_follower"));
        configTalon(followMotor);
        followMotor.follow(mainMotor);

        SCREW_LEN = Config.getDouble("screw_len");
        DIST_PER_REV = Config.getDouble("screw_dist_per_rev");
        TICKS_PER_REV = Config.getInt("elevator_ticks_per_rev");
        TICKS_PER_SCREW = (int) ((SCREW_LEN / DIST_PER_REV) * TICKS_PER_REV);
    }

    /** Zeroes the elevator */
    public void zero() {
        // TODO idk if this works. it is meant to set the current encoder position as 0
        mainMotor.setSelectedSensorPosition(0);
        followMotor.setSelectedSensorPosition(0);
    }

    public void loop() {
        // TODO maybe we only need one controller for everything
        // if left trigger is pressed, move elevator down
        // if right trigger is pressed, move elevator up
        // if neither are pressed, keep elevator at same level (run the motor to keep it there?)
        double triggerVal = controller.getTriggerAxis(Hand.kLeft) + controller.getTriggerAxis(Hand.kRight);

        // make controller vibrate if we are at elevator top or bottom
        if (triggerVal > 0 && mainMotor.getSelectedSensorPosition() >= TICKS_PER_SCREW - 1) {
            controller.setRumble(RumbleType.kRightRumble, 1);
        } else if (triggerVal < 0 && mainMotor.getSelectedSensorPosition() <= 2) {
            controller.setRumble(RumbleType.kLeftRumble, 1);
        }
    }

    private void configTalon(TalonSRX talon) {
        talon.configFactoryDefault();
        // TODO check if this actually works
        talon.setSelectedSensorPosition(0);
        talon.configForwardSoftLimitThreshold(TICKS_PER_SCREW, 0);
        talon.configReverseSoftLimitThreshold(0, 0);
        talon.configForwardSoftLimitEnable(true, 0);
        talon.configReverseSoftLimitEnable(true, 0);
    }

}