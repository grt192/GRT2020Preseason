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

    /** The controller for this elevator */
    private static final XboxController controller = Input.CONTROLLER;

    public ElevatorMech() {
        // config motors
        mainMotor = new TalonSRX(Config.getInt("elevator_master"));
        Config.defaultConfigTalon(mainMotor);
        followMotor = new TalonSRX(Config.getInt("elevator_follower"));
        Config.defaultConfigTalon(followMotor);
        followMotor.follow(mainMotor);
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

}