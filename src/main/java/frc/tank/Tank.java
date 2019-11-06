package frc.tank;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI.Port;
import frc.config.Config;

public class Tank {

    private final double TICKS_TO_METERS;
    private final double WIDTH;
    public final double MAX_SPEED;
    public final double MAX_ANGULAR_SPEED;

    private TalonSRX leftMotor;
    private TalonSRX rightMotor;
    private AHRS gyro;

    public Tank() {
        TICKS_TO_METERS = Config.getDouble("ticks_to_meters");
        WIDTH = Config.getDouble("dt_width");
        MAX_SPEED = Config.getDouble("max_speed");
        MAX_ANGULAR_SPEED = MAX_SPEED / (WIDTH / 2);

        gyro = new AHRS(Port.kMXP);

        leftMotor = new TalonSRX(Config.getInt("left_master"));
        boolean leftInverted = Config.getBoolean("left_inverted");
        Config.defaultConfigTalon(leftMotor);
        leftMotor.setInverted(leftInverted);
        TalonSRX leftFollower = new TalonSRX(Config.getInt("left_follower"));
        Config.defaultConfigTalon(leftFollower);
        leftFollower.setInverted(leftInverted);
        leftFollower.follow(leftMotor);
        configPID(leftMotor);

        rightMotor = new TalonSRX(Config.getInt("right_master"));
        boolean rightInverted = Config.getBoolean("right_inverted");
        Config.defaultConfigTalon(rightMotor);
        rightMotor.setInverted(rightInverted);
        TalonSRX rightFollower = new TalonSRX(Config.getInt("right_follower"));
        Config.defaultConfigTalon(rightFollower);
        rightFollower.setInverted(rightInverted);
        rightFollower.follow(rightMotor);
        configPID(rightMotor);
    }

    public void setRaw(double lSpeed, double rSpeed) {
        leftMotor.set(ControlMode.PercentOutput, lSpeed);
        rightMotor.set(ControlMode.PercentOutput, rSpeed);
    }

    public void set(double lSpeed, double rSpeed) {
        double scale = Math.min(1, MAX_SPEED / Math.max(Math.abs(lSpeed), Math.abs(rSpeed)));
        lSpeed *= scale;
        rSpeed *= scale;
        leftMotor.set(ControlMode.Velocity, lSpeed / (TICKS_TO_METERS * 10));
        rightMotor.set(ControlMode.Velocity, rSpeed / (TICKS_TO_METERS * 10));
    }

    public void setPolar(double speed, double angVel) {
        double lSpeed = speed + angVel * WIDTH / 2;
        double rSpeed = speed - angVel * WIDTH / 2;
        set(lSpeed, rSpeed);
    }

    private void configPID(TalonSRX talon) {
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        double kF = 1023 * 10 * TICKS_TO_METERS / MAX_SPEED;
        talon.config_kP(0, kF * 2 * 0, 0);
        talon.config_kI(0, 0, 0);
        talon.config_kD(0, 0, 0);
        talon.config_kF(0, kF, 0);

        talon.config_kP(1, 0, 0);
        talon.config_kI(1, 0, 0);
        talon.config_kD(1, 0, 0);
        talon.config_kF(1, 0, 0);
    }

    public TankData getTankData() {
        TankData td = new TankData();
        double leftSpeed = leftMotor.getSelectedSensorVelocity(0) * TICKS_TO_METERS * 10;
        double rightSpeed = rightMotor.getSelectedSensorVelocity(0) * TICKS_TO_METERS * 10;
        td.leftSpeed = leftSpeed;
        td.rightSpeed = rightSpeed;
        td.avgSpeed = (leftSpeed + rightSpeed) / 2;
        td.encoderW = (leftSpeed - rightSpeed) / WIDTH;
        td.gyroAngle = Math.toRadians(gyro.getAngle());
        td.gyroW = Math.toRadians(gyro.getRate());
        return td;
    }

    public void printError() {
        System.out.println("Error: " + leftMotor.getClosedLoopError(0) + ", " + rightMotor.getClosedLoopError(0));
        System.out.println(leftMotor.getClosedLoopTarget(0));
        System.out.println(leftMotor.getMotorOutputPercent());
    }

}