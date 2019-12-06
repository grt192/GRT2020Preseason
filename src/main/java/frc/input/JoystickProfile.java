package frc.input;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.util.GRTUtil;
public class JoystickProfile {
	private static final double DEFAULT_DEADBAND = 0.15;

	// number between 0 and 1. closer to 1 = more dramatic joystick value correction
	private static double profileFactor = 1;
	
	private JoystickProfile() {}

	public static double applyProfile(double x) {
		// first apply deadband, then scale back to original range
		x = applyDeadband(x) / (1 - DEFAULT_DEADBAND);
		// apply the polynominal
		return applyPolynominal(x);
	}

	private static double applyPolynominal(double x) {
		double posX = Math.abs(x);
		
		return Math.signum(x) * (profileFactor * Math.pow(posX, 5) + (1 - profileFactor) * Math.pow(posX, 2));
	}

	public static void updateProfileFactor() {
		double val = SmartDashboard.getNumber("DB/Slider 0", 4);
		profileFactor = val/5;
		SmartDashboard.putBoolean("DB/LED 0", !SmartDashboard.getBoolean("DB/LED 0", false));
	}

	/** Returns how much the joystick value correction is */
	public static double getProfileFactor() {
		return profileFactor;
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