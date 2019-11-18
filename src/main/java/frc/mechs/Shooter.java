package frc.mechs;

import frc.config.Config;
import frc.robot.Mech;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Shooter extends Mech {

    private static final double POWER_HOPPER = 0.35;
    private static final double POWER_FLYWHEEL = 0.35;

    private int directionHopper = 1;
    private int directionFlywheel = 1;

    private TalonSRX motorHopper;
    private TalonSRX motorFlywheel;
    private XboxController controller = Input.MECH_XBOX;

    private boolean shooterOn = False;
    
    public Shooter() {
        this.motorHopper = new TalonSRX(Config.getInt("motor_hopper"));
        this.motorFlywheel = new TalonSRX(Config.getInt("motor_flywheel"));
        // TODO: add config
    }

    public void loop () {
        // if x button is pressed, start shooter
        // if y button is pressed, stop shooter

        boolean xButtonVal = controller.getXButtonReleased();
        double yButtonVal = controller.getYButtonReleased();

        if (xButtonVal && !shooterOn) {
            motorHopper.set(ControlMode.PercentOutput, directionHopper * POWER_HOPPER);
            motorFlywheel.set(ControlMode.PercentOutput, directionFlywheel * POWER_FLYWHEEL);
            shooterOn = true;
        } else if (yButtonVal && shooterOn) {
            motorHopper.set(ControlMode.PercentOutput, 0);
            motorFlywheel.set(ControlMode.PercentOutput, 0);
            shooterOn = false;
        }
    }
}