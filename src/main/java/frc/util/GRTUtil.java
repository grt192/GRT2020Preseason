package frc.util;

public class GRTUtil {

	// GRTUtil because there are too many other Util classes
	private GRTUtil() {
	}

	public static final double TWO_PI = 2 * Math.PI;

	public static double clamp(double min, double x, double max) {
		return Math.min(Math.max(min, x), max);
	}

	public static double positiveMod(double x, double mod) {
		return (((x % mod) + mod) % mod);
	}

	public static boolean inRange(double min, double x, double max) {
		return x >= min && x <= max;
	}

	public static double distanceToAngle(double from, double to) {
		from = positiveMod(from, TWO_PI);
		to = positiveMod(to, TWO_PI);
		double error = to - from;
		if (Math.abs(error) > Math.PI) {
			error -= Math.signum(error) * TWO_PI;
		}
		return error;
	}

}
