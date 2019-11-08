package frc.tank;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI.Port;
import frc.config.Config;

/**
 * See http://www.cs.columbia.edu/~allen/F17/NOTES/icckinematics.pdf
 */
public class Tank {
    /** Meters per encoder ticks */
    private final double METERS_PER_TICK;
    /** Width of the robot (distance between opposite wheels) */
    private final double WIDTH;
    /** The max speed of the motors. From -1.0 to 1.0. */
    public final double MAX_SPEED;
    /** The max angular speed of the wheel. How fast the robot can turn */
    public final double MAX_ANGULAR_SPEED;

    /** controls the left wheel */
    private TalonSRX leftMotor;
    /** controls the right wheel */
    private TalonSRX rightMotor;
    private AHRS gyro;

    public Tank() {
        METERS_PER_TICK = Config.getDouble("ticks_to_meters");
        WIDTH = Config.getDouble("dt_width");
        MAX_SPEED = Config.getDouble("max_speed");
        // maximum angular velocity = v/r
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

    /** Sets the speed of the motor. lSpeed/rSpeed should be between -1.0 and 1.0,
     * with 0.0 as stopped, and represent the percent output of the motor. */
    private void setRaw(double lSpeed, double rSpeed) {
        leftMotor.set(ControlMode.PercentOutput, lSpeed);
        rightMotor.set(ControlMode.PercentOutput, rSpeed);
    }

    /** Set the speed of the left and right motors in meters/second
     * (velocity along the ground)
     * @param lSpeed speed of the left motor in meters/second on the ground
     * @param rSpeed speed of the right motor in meters/second on the ground
     */
    public void set(double lSpeed, double rSpeed) {
        // make lSpeed and rSpeed smaller than MAX_SPEED
        double scale = Math.min(1, MAX_SPEED / Math.max(Math.abs(lSpeed), Math.abs(rSpeed)));
        lSpeed *= scale;
        rSpeed *= scale;

        // meters/second * ticks/meter / 10 = meters/100ms
        leftMotor.set(ControlMode.Velocity, lSpeed / (METERS_PER_TICK * 10));
        rightMotor.set(ControlMode.Velocity, rSpeed / (METERS_PER_TICK * 10));
    }
    /** Sets the tangential velocity and angular velocity of the robot
     * with respect to the point of rotation.
     * <p> left motor meters/second = angVel * ( (radius of rotation) + (robot width / 2) ) </p>
     * <p> right motor meters/second = angVel * ( (radius of rotation) - (robot width / 2) ) </p>
     */
    public void setPolar(double speed, double angVel) {
        double lSpeed = speed + angVel * WIDTH / 2;
        double rSpeed = speed - angVel * WIDTH / 2;
        set(lSpeed, rSpeed);
    }

    private void configPID(TalonSRX talon) {
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        double kF = 1023 * 10 * METERS_PER_TICK / MAX_SPEED;
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
        double leftSpeed = leftMotor.getSelectedSensorVelocity(0) * METERS_PER_TICK * 10;
        double rightSpeed = rightMotor.getSelectedSensorVelocity(0) * METERS_PER_TICK * 10;
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