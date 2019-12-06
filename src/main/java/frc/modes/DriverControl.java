/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.modes;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.input.Input;
import frc.input.JoystickProfile;
import frc.robot.Robot;
import frc.util.GRTUtil;

class DriverControl extends Mode {
    // number of drive control methods
    public static final int DRIVES = 3;
    // current drive control method, [0, DRIVES-1]
    private int driveMethod = 0;

    public DriverControl() {
        putDriveMethod();
    }

    @Override
    public boolean loop() {
        drive();
        Robot.ELEVATOR.loop();
        return true;
    }

    public void drive() {

        
        // get input from the dpad to switch drive modes
        if (Input.CONTROLLER.getBumper(Hand.kLeft)) {
            driveMethod = (driveMethod + 1) % DRIVES;
            putDriveMethod();
        } else if (Input.CONTROLLER.getBumper(Hand.kRight)) {
            driveMethod = (driveMethod - 1 + DRIVES) % DRIVES;
            putDriveMethod();
        }

        switch (driveMethod) {
            case 0: tankDrive(); break;
            case 1: arcadeDrive(); break;
            case 2: arcade2StickDrive(); break;
            default: tankDrive(); break;
        }
    }

    /** Left stick controls velocity of left wheels, right stick controls velocity of right wheels */
    private void tankDrive() {
        // You may be wondering, why x is .getY() and y is .getX()? Don't question it //
        double leftVel = JoystickProfile.applyDeadband(Input.CONTROLLER.getY(Hand.kLeft));
        double rightVel = JoystickProfile.applyDeadband(Input.CONTROLLER.getY(Hand.kRight));
        // square inputs to decrease sensitivity at low speeds
        leftVel = GRTUtil.signedSquare(leftVel);
        rightVel = GRTUtil.signedSquare(rightVel);


        Robot.TANK.setRaw(leftVel, rightVel);
    }

    /** Left stick controls forward/backward and left/right motion */
    private void arcadeDrive() {
        double forwardVel = JoystickProfile.applyDeadband(Input.CONTROLLER.getY(Hand.kLeft));
        double rotateAmt = JoystickProfile.applyDeadband(Input.CONTROLLER.getX(Hand.kLeft));
        // square inputs to decrease sensitivity at low speeds
        forwardVel = GRTUtil.signedSquare(forwardVel);
        rotateAmt = GRTUtil.signedSquare(rotateAmt);
        Robot.TANK.setPolarRaw(forwardVel * Robot.TANK.MAX_SPEED, rotateAmt * Robot.TANK.MAX_ANGULAR_SPEED);
    }

    /** 2 stick arcade drive - left stick controls forward/backward, right 
     * stick controls left/right */
     private void arcade2StickDrive() {
        double forwardVel = JoystickProfile.applyDeadband(Input.CONTROLLER.getY(Hand.kLeft));
        double rotateAmt = JoystickProfile.applyDeadband(Input.CONTROLLER.getX(Hand.kRight));
        // square inputs to decrease sensitivity at low speeds
        forwardVel = GRTUtil.signedSquare(forwardVel);
        rotateAmt = GRTUtil.signedSquare(rotateAmt);
        Robot.TANK.setPolarRaw(forwardVel * Robot.TANK.MAX_SPEED, rotateAmt * Robot.TANK.MAX_ANGULAR_SPEED);
     }

     /** Puts the current drive method onto the SmartDashboard */
     private void putDriveMethod() {
         String strDrive;
         switch (driveMethod) {
             case 0: strDrive = "Tank"; break;
             case 1: strDrive = "Arcade, 1 stick"; break;
             case 2: strDrive = "Arcade, 2 stick"; break;
             default: strDrive = "Default (tank)"; break;
         }
         SmartDashboard.putString("DB/String 0", "drive: " + driveMethod + ", " + strDrive);
     }
}
