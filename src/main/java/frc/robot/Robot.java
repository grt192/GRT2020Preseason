/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.config.Config;
import frc.fieldmap.FieldMap;
import frc.input.Input;
import frc.input.JoystickProfile;
import frc.modes.Mode;
import frc.positiontracking.KalmanFilterPositionTracker;
import frc.positiontracking.PositionTracker;
import frc.swerve.NavXGyro;
import frc.swerve.Swerve;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

    private NetworkTableEntry mode;
    private Autonomous autonomous;

    public static Swerve SWERVE;
    public static NavXGyro GYRO;
    public static PositionTracker POS_TRACKER;
    public static FieldMap FIELD_MAP;
    public static double ROBOT_WIDTH;
    public static double ROBOT_HEIGHT;
    public static double ROBOT_RADIUS;

    private boolean overridden;

    @Override
    public void robotInit() {
        Config.start();
        ROBOT_WIDTH = Config.getDouble("robot_width");
        ROBOT_HEIGHT = Config.getDouble("robot_height");
        ROBOT_RADIUS = Math.sqrt(ROBOT_WIDTH * ROBOT_WIDTH + ROBOT_HEIGHT * ROBOT_HEIGHT) / 2;
        FIELD_MAP = new FieldMap();
        autonomous = new Autonomous(this);
        GYRO = new NavXGyro();
        POS_TRACKER = new KalmanFilterPositionTracker();
        // POS_TRACKER = new BasicPositionTracker();
        POS_TRACKER.set(66 + ROBOT_HEIGHT / 2, 14.75 + ROBOT_WIDTH / 2);
        SWERVE = new Swerve();
        Mode.initModes();
        mode = NetworkTableInstance.getDefault().getTable("Robot").getEntry("mode");
        mode.setNumber(0);
    }

    private void loop() {
        // long start = System.nanoTime();
        // handle mode switching
        autonomous.loop();
        // System.out.println("auton: " + (System.nanoTime() - start));
        int i = mode.getNumber(0).intValue();
        if (manualOverride()) {
            autonomous.kill();
            mode.setNumber(0);
            i = 0;
        }
        // System.out.println("override: " + (System.nanoTime() - start));
        if (!Mode.getMode(i).loop()) {
            autonomous.modeFinished();
            mode.setNumber(0);
        }
        // System.out.println("done: " + (System.nanoTime() - start));
    }

    public void setMode(int i) {
        mode.setNumber(i);
    }

    private boolean manualOverride() {
        double x = JoystickProfile.applyDeadband(-Input.SWERVE_XBOX.getY(Hand.kLeft));
        double y = JoystickProfile.applyDeadband(Input.SWERVE_XBOX.getX(Hand.kLeft));
        boolean temp = !(x == 0 && y == 0);
        if (temp && !overridden) {
            overridden = temp;
            return true;
        }
        overridden = temp;
        return false;
    }

    @Override
    public void autonomousInit() {
        autonomous.init("2hatchesfrontl.txt");
    }

    @Override
    public void autonomousPeriodic() {
        loop();
    }

    @Override
    public void teleopPeriodic() {
        loop();
    }

}
