package frc.input;
import java.util.ArrayList;
public class JoystickProfile {
	private static final double DEFAULT_DEADBAND = 0.1;
	// shouldn't be able to change the size of thiss array
	private static final int[] lookup = new int[1000];
	// use network tables to get the size of a lookup table?
	// maybe have to use locks otherwise will get problems with multithreading

	private JoystickProfile() {}

	/** Takes an arraylist of JoystickProfilePoints, which should be presorted
	 * by their raw values, in ascending order. all raw/profiled values should be between 0 and 1.0
	 */
	public static void setProfile(ArrayList<JoystickProfilePoint> pts) {
		// do lock
		int startRaw = 0;
		int startProfiled = 0;
		int endRaw = 0;
		int endProfiled = 0;
		for (int i = 0; i < pts.size(); i++) {
			startRaw = endRaw;
			startProfiled = endProfiled;
			endRaw = (int) (i == pts.size() - 1 ? lookup.length : pts.get(i+1).raw * lookup.length);
			endProfiled = (int) (i == pts.size() - 1 ? lookup.length : pts.get(i+1).profiled * lookup.length);
			// between indices startRaw/endRaw, do a transition between startProfiled and endProfiled
			for (int j = startRaw; j <= endRaw; j++) {
				lookup[j] = (int) (startRaw + (j - startRaw) * (endProfiled - startProfiled) / (endRaw - startRaw));
			}
		}
	}

	public static double applyDeadband(double x, double deadband) {
		return (Math.abs(x) > deadband ? x : 0);
	}

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

class JoystickProfilePoint {
	public final double raw;
	public final double profiled;
	public JoystickProfilePoint(double raw, double profiled) {
		this.raw = raw;
		this.profiled = profiled;
	}

}