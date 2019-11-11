package frc.mechs;

import frc.config.Config;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class ElevatorMech extends Mech {
    private TalonSRX mainMotor;
    private TalonSRX followMotor;

    /** Total length of the lead screw */
    private final double SCREW_LEN;
    /** Travel distance per revolution of the lead screw, inches */
    private final double DIST_PER_REV;
    /** Encoder ticks in one elevator motor revolution */
    private final int TICKS_PER_REV;

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
    }

    /** Zeroes the elevator */
    public void zero() {
        // TODO idk if this works. it is meant to set the current encoder
        // position as 0
        mainMotor.setSelectedSensorPosition(0);
        followMotor.setSelectedSensorPosition(0);
    }

    public void loop() {
        // TODO maybe we only need one controller for everything
        // if left trigger is pressed, move elevator down
        // if right trigger is pressed, move elevator up
    }

    private void configTalon(TalonSRX talon) {
        talon.configFactoryDefault();
        // TODO check if this actually works
        talon.setSelectedSensorPosition(0);
        talon.configForwardSoftLimitThreshold((int) ((SCREW_LEN / DIST_PER_REV) * TICKS_PER_REV), 0);
        talon.configReverseSoftLimitThreshold(0, 0);
        talon.configForwardSoftLimitEnable(true, 0);
        talon.configReverseSoftLimitEnable(true, 0);
    }
}