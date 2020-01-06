package frc.input;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.config.Config;
import frc.util.GRTUtil;
public class JoystickProfile {
	private static final double DEFAULT_DEADBAND = 0.05;

	/** array of [x, y] points used to define the joystick profile */
	private static double[][] profilingPoints;
	
	private JoystickProfile() {}

	public static void init() {
		profilingPoints = new double[2][2];
		profilingPoints[0][0] = Config.getDouble("joystick_x1");
		profilingPoints[0][1] = Config.getDouble("joystick_y1");
		SmartDashboard.putString("DB/String 5", profilingPoints[0][0] + ", " + profilingPoints[0][1]);
		profilingPoints[1][0] = Config.getDouble("joystick_x2");
		profilingPoints[1][1] = Config.getDouble("joystick_y2");
		SmartDashboard.putString("DB/String 6", profilingPoints[1][0] + ", " + profilingPoints[1][1]);
	}

	public static double applyProfile(double x) {
		// first apply deadband, then scale back to original range
		x = applyDeadband(x) / (1 - DEFAULT_DEADBAND);
		// apply profiling
		if (GRTUtil.inRange(0, x, profilingPoints[0][0])) {
			x = GRTUtil.toRange(0, profilingPoints[0][0], 0, profilingPoints[0][1], x);
		} else if (GRTUtil.inRange(profilingPoints[0][0], x, profilingPoints[1][0])) {
			x = GRTUtil.toRange(profilingPoints[0][0], profilingPoints[1][0], 
								profilingPoints[0][1], profilingPoints[1][1], x);
		} else {
			x = GRTUtil.toRange(profilingPoints[1][0], 1, profilingPoints[1][1], 1, x);
		}
		return x;
	}

	public static double applyDeadband(double x, double deadband) {
		return (Math.abs(x) > deadband ? x : 0);
	}

	/** Applies the default deadband to the value passed in */
	public static double applyDeadband(double x) {
		return applyDeadband(x, DEFAULT_DEADBAND);
	}

	public static double square(double x) {
		return Math.copySign(x * x, x);
	}

	public static double clipAndSquare(double x) {
		return square(applyDeadband(x));
	}

}