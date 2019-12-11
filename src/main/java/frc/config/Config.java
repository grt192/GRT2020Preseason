package frc.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Filesystem;

public class Config {
	private static Map<String, String> map;

	private static String fileName;
	
	/** Get the int config value corresponding to the key passed in.
	 * @return The corresponding integer value, or -1 if the key was not found/invalid
	 */
	public static int getInt(String key) {
		try {
			return Integer.parseInt(map.get(key));
		} catch (Exception e) {
			return -1;
		}
	}

	/** Get the boolean config value corresponding to the key passed in.
	 * @return The corresponding boolean value, or false if the key was invalid
	 */
	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(map.get(key));
	}
	/** Get the string config value corresponding to the key passed in.
	 * @return The corresponding string value, or the empty string if the key was invalid
	 */
	public static String getString(String key) {
		String result = map.get(key);
		if (result == null) {
			return "";
		}
		return result;
	}

	/** Get the double config value corresponding to the key passed in.
	 * @return The corresponding double value, or 0.0 if the key was invalid
	 */
	public static double getDouble(String key) {
		try {
			return Double.parseDouble(map.get(key));
		} catch (Exception e) {
			return 0.0;
		}
	}

	public static void start() {
		map = new HashMap<>();
		try {
			// load config file
			Scanner nameScanner = new Scanner(new File("/home/lvuser/name.192"));
			String name = nameScanner.nextLine();
			nameScanner.close();
			fileName = name + ".txt";
			System.out.println("reading from file " + fileName);
			File f = new File(Filesystem.getDeployDirectory(), fileName);
			Scanner scanner = new Scanner(f);

			// add configs to map
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();

				if (line.length() > 0 && line.charAt(0) != '#') {
					String[] splitted = line.trim().split("=");
					if (splitted.length == 2)
						map.put(splitted[0], splitted[1]);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (String s : map.keySet()) {
			System.out.println(s + ": " + getString(s));
		}

	}

	public static void defaultConfigTalon(TalonSRX talon) {
		talon.configFactoryDefault();
		talon.configForwardSoftLimitEnable(false, 0);
		talon.configReverseSoftLimitEnable(false, 0);
		talon.setNeutralMode(NeutralMode.Brake);
		talon.configOpenloopRamp(0, 0);
	}

	/** Returns the name of the file used for config (eg "preseason2020.txt") */
	public static String getFileName() {
		return fileName;
	}
}
