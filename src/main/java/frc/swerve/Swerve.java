package frc.swerve;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Queue;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Filesystem;

import frc.config.Config;
import frc.robot.Robot;
import frc.util.GRTUtil;

public class Swerve implements Runnable {

	private final double SWERVE_WIDTH;
	private final double SWERVE_HEIGHT;
	private final double RADIUS;
	private final double WHEEL_ANGLE;
	private final double ROTATE_SCALE;
	private final double kP;
	private final double kD;

	private Notifier notifier;

	private NetworkTableEntry gyroAngle;
	private NavXGyro gyro;
	/** wheels[0]=fr, wheels[1]=br, wheels[2]=bl, wheels[3]=fl */
	private Wheel[] wheels;

	private volatile double userVX, userVY, userW, angle;
	private volatile boolean robotCentric;
	private volatile boolean withPID;
	private volatile SwerveData swerveData;

	public Swerve() {
		gyroAngle = NetworkTableInstance.getDefault().getTable("PositionTracking").getEntry("angle");
		this.gyro = Robot.GYRO;
		gyro.reset();
		angle = 0.0;
		robotCentric = false;
		withPID = false;
		wheels = new Wheel[4];
		wheels[0] = new Wheel("fr");
		wheels[1] = new Wheel("br");
		wheels[2] = new Wheel("bl");
		wheels[3] = new Wheel("fl");

		SWERVE_WIDTH = Config.getDouble("swerve_width");
		SWERVE_HEIGHT = Config.getDouble("swerve_height");
		kP = Config.getDouble("swerve_kp");
		kD = Config.getDouble("swerve_kd");
		RADIUS = Math.sqrt(SWERVE_WIDTH * SWERVE_WIDTH + SWERVE_HEIGHT * SWERVE_HEIGHT) / 2;
		WHEEL_ANGLE = Math.atan2(SWERVE_WIDTH, SWERVE_HEIGHT);
		ROTATE_SCALE = 1 / RADIUS;
		calcSwerveData();
		notifier = new Notifier(this);
		notifier.startPeriodic(0.02);
		setAngle(0.0);
	}

	public void run() {
		double w = userW;
		if (withPID) {
			w = calcPID();
		}
		changeMotors(userVX, userVY, w);
		calcSwerveData();
		SmartDashboard.putNumber("Angle", gyro.getAngle());
		gyroAngle.setDouble(Math.toRadians(gyro.getAngle()));
	}

	private double calcPID() {
		double error = GRTUtil.distanceToAngle(Math.toRadians(gyro.getAngle()), angle);
		double w = error * kP - Math.toRadians(gyro.getRate()) * kD;
		return w;
	}

	public void drive(double vx, double vy, double w) {
		userVX = vx;
		userVY = vy;
		userW = w;
		if (w != 0) {
			withPID = false;
		}
	}

	public void setAngle(double angle) {
		withPID = true;
		this.angle = angle;
	}

	public void setRobotCentric(boolean mode) {
		robotCentric = mode;
	}

	public void changeMotors(double vx, double vy, double w) {
		w *= ROTATE_SCALE;
		double gyroAngle = (robotCentric ? 0 : Math.toRadians(gyro.getAngle()));
		for (int i = 0; i < 4; i++) {
			double wheelAngle = getRelativeWheelAngle(i) + gyroAngle;
			double dx = RADIUS * Math.cos(wheelAngle);
			double dy = RADIUS * Math.sin(wheelAngle);
			double wheelVX = vx - dy * w;
			double wheelVY = vy + dx * w;
			double wheelPos = Math.atan2(wheelVY, wheelVX) - gyroAngle;
			double power = Math.sqrt(wheelVX * wheelVX + wheelVY * wheelVY);
			wheels[i].set(wheelPos, power);
		}
	}

	public SwerveData getSwerveData() {
		return swerveData;
	}

	private void calcSwerveData() {
		double gyroAngle = Math.toRadians(gyro.getAngle());
		double gyroRate = Math.toRadians(gyro.getRate());
		double vx = 0;
		double vy = 0;
		double w = 0;
		for (int i = 0; i < 4; i++) {
			double wheelAngle = getRelativeWheelAngle(i);
			double wheelPos = wheels[i].getCurrentPosition();
			double speed = wheels[i].getDriveSpeed();
			w += Math.sin(wheelPos - wheelAngle) * speed / RADIUS;
			wheelPos += gyroAngle;
			vx += Math.cos(wheelPos) * speed;
			vy += Math.sin(wheelPos) * speed;
		}
		w /= 4.0;
		vx /= 4.0;
		vy /= 4.0;
		swerveData = new SwerveData(gyroAngle, gyroRate, vx, vy, w);
	}

	private double getRelativeWheelAngle(int i) {
		double angle = WHEEL_ANGLE;
		if (i == 1 || i == 3) {
			angle *= -1;
		}
		if (i == 1 || i == 2) {
			angle += Math.PI;
		}
		return angle;
	}

	/**
	 * Takes the current position of the wheels and sets them as zero in the
	 * currently running program and adds them to the Basic tab on SmartDashboard
	 */
	public void zeroRotate() {
		for (int i = 0; i < wheels.length; i++) {
			wheels[i].zero();
			SmartDashboard.putString("DB/String " + i, wheels[i].getName() + "_offset: " + wheels[i].getOffset());
		}
	}


}
