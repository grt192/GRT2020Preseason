package frc.input;
import java.util.ArrayList;

import frc.util.GRTUtil;
public class JoystickProfile {
	private static final double DEFAULT_DEADBAND = 0.1;

	// number between 0 and 1. closer to 1 = more dramatic joystick value correction
	private static double profileFactor = .8;

	private JoystickProfile() {}

	public static double applyProfile(double x) {
		// first apply deadband, then scale back to original range
		x = applyDeadband(x) / (1 - DEFAULT_DEADBAND);
		// apply the polynominal
		return applyPolynominal(x);
	}

	private static double applyPolynominal(double x) {
		// possible a*X^3 + (1-a)*X
		return profileFactor * x * x * x + (1 - profileFactor) * x;
	}

	/** Sets how "dramatic" the joystick value correction should be.
	 * @param factor a number from 0 to 1
	 */
	public static boolean setProfileFactor(double factor) {
		// maybe check network tables here
		if (GRTUtil.inRange(0, factor, 1)) {
			profileFactor = factor;
			return true;
		}
		return false;
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