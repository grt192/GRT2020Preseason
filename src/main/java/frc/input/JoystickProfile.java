package frc.input;

public class JoystickProfile {

	private static final double DEFAULT_DEADBAND = 0.1;

	private JoystickProfile() {
	}

	/** applies the requested deadband to x. */
	public static double applyDeadband(double x, double deadband) {
		return (Math.abs(x) > deadband ? x : 0);
	}

	/** applies the default deadband to x. */
	public static double applyDeadband(double x) {
		return applyDeadband(x, DEFAULT_DEADBAND);
	}

	/** squares x while keeping the original sign. */
	public static double signedSquare(double x) {
		return Math.copySign(x * x, x);
	}

	/** applies the deadband to x and returns the signed square of the result */
	public static double clipAndSquare(double x) {
		return signedSquare(applyDeadband(x));
	}

}
