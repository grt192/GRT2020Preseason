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
    
    public Shooter() {
        this.motorHopper = new TalonSRX(Config.getInt("motor_hopper"));
        this.motorFlywheel = new TalonSRX(Config.getInt("motor_flywheel"));
        // TODO: add config
    }

    public void start() {
        motorHopper.set(ControlMode.PercentOutput, POWER_HOPPER * directionHopper);
        motorFlywheel.set(ControlMode.PercentOutput, POWER_FLYWHEEL * directionFlywheel);
    }

    public void stop() {
        motorHopper.set(ControlMode.PercentOutput, 0);
        motorFlywheel.set(ControlMode.PercentOutput, 0);
    }
}