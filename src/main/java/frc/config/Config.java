package frc.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

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
					String[] splitted = line.split("=");
					if (splitted.length == 2)
						map.put(splitted[0].trim(), splitted[1].trim());
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

	/** Puts an entry into the map of config values. If the map 
	 * previously contained a mapping for the key, the old value is replaced 
	 * by the specified value. DOES NOT change the actual config file, call
	 * updateConfigFile() to change values in file */
	public static void put(String key, String value) {
		key = key.trim();
		value = value.trim();
		if (map.put(key, value) == null) {
			System.out.println("added new mapping to config map: " + key + "=" + value);
		} else {
			System.out.println("edited existing mapping in config map: " + key + "=" + value);
		}
	}

	/** Removes the mapping for a key from the map of config values if it is present
	 * @param key the key whose mapping is to be removed
	 * @return the previous value associated with the key */
	public static String remove(String key) {
		System.out.println("removed mapping from config map: " + key + "=" + map.get(key));
		return map.remove(key);
	}


	/** Writes the current mappings to the config file */
	public static void updateConfigFile() {
		File f = new File(Filesystem.getDeployDirectory(), fileName);
		// read config file, store the formatting, and identify new keys to add
		Queue<String> commands = new LinkedList<String>();
		Set<String> existingKeys = new HashSet<String>();
		try {
			Scanner scanner = new Scanner(f);
			String input;
			while (scanner.hasNext()) {
				input = scanner.nextLine().trim();
				commands.add(input);
				if (!input.isEmpty() && input.charAt(0) != '#') {
					existingKeys.add(input.split("=")[0].trim().toLowerCase());
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("recovering...");
			// recover by writing the current key/value pairs to a new config file
			writeRawToConfigFile(f);
			return;
		}

		// put new config file at "configtemp.txt", then atomically rename it to replace old config file
		File tempFile = new File(Filesystem.getDeployDirectory(), "configtemp.txt");
		FileWriter writer;
		try {
			writer = new FileWriter(tempFile);
			String cmd, key;
			while (!commands.isEmpty()) {
				cmd = commands.remove();
				if (cmd.isEmpty()) {
					writer.write("\n");
				} else if (cmd.charAt(0) != '#') {
					key = cmd.split("=")[0].trim();
					if (map.containsKey(key)) {
						writer.write(key + "=" + map.get(key) + "\n");
					}
				} else if (cmd.charAt(0) == '#') {
					writer.write(cmd + "\n");
				}
			}
			// write new key/value pairs to bottom of file
			writer.write("\n");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (!existingKeys.contains(entry.getKey().toLowerCase())) {
					writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
				}
			}
			writer.close();
			// rename file to replace old file
			Files.move(tempFile.toPath(), f.toPath(), StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			System.out.println("could not update config file");
			e.printStackTrace();
			return;
		}
	}

	/** Writes the current key/value pairs to the file in an unordered way */
	private static void writeRawToConfigFile(File f)  {
		System.out.println("Writing raw key/value pairs to config file: " + f.getName());
		FileWriter writer;
		try {
			writer = new FileWriter(f);
			for (Map.Entry<String, String> entry : map.entrySet()) {
				writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("could not write to config file");
			e.printStackTrace();
		}
	}
}
