package frc.input;

public class JoystickProfile {

	private static final double DEFAULT_DEADBAND = 0.1;

	private JoystickProfile() {
	}

	/**
	 * If the the x value passed is greater than the deadband value, the joystick
	 * movement is significant enough, so return the movement. Otherwise, return 0.
	 */
	public static double applyDeadband(double x, double deadband) {
		return (Math.abs(x) > deadband ? x : 0);
	}

	/**
	 * Applies the default deadband to the input value.
	 */
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
