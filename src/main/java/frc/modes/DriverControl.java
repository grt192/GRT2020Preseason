/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.modes;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.input.Input;
import frc.input.JoystickProfile;
import frc.robot.Robot;

class DriverControl extends Mode {
    // consider the front of the robot to be the side with the hopper
    private boolean reverse = false;
    // number of drive control methods
    public static final int DRIVES = 3;
    // current drive control method, [0, DRIVES-1]
    private int driveMethod = 0;


    @Override
    public boolean loop() {
        // if the right bumper is being pressed, then we are in reverse mode
        if (Input.TANK_XBOX.getBumperPressed(Hand.kRight)) {
            reverse = true;
        }
        if (Input.TANK_XBOX.getBumperReleased(Hand.kRight)) {
            reverse = false;
        }

        // get input from the dpad to switch drive modes
        if (Input.TANK_XBOX.getPOV() == 90) {
            driveMethod = (driveMethod + 1) % DRIVES;
        } else if (Input.TANK_XBOX.getPOV() == 270) {
            driveMethod = (driveMethod - 1) % DRIVES;
        }

        switch (driveMethod) {
            case 0: tankDrive(); break;
            case 1: arcadeDrive(); break;
            case 2: arcade2StickDrive(); break;
        }
        return true;
    }

    /** Left stick controls velocity of left wheels, right stick controls velocity of right wheels */
    private void tankDrive() {
        // You may be wondering, why x is .getY() and y is .getX()? Don't question it //
        double leftVel = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kLeft));
        double rightVel = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kRight));
        if (reverse) {
            double tmp = leftVel;
            leftVel = -rightVel;
            rightVel = -tmp;
        }
        Robot.TANK.setRaw(leftVel, rightVel);
    }

    /** Left stick controls forward/backward and left/right motion */
    private void arcadeDrive() {
        double forwardVel = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kLeft)) * (reverse?-1:1);
        double rotateAmt = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kLeft)) * (reverse?-1:1);
        Robot.TANK.setPolar(forwardVel * Robot.TANK.MAX_SPEED, rotateAmt * Robot.TANK.MAX_ANGULAR_SPEED);
    }

    /** 2 stick arcade drive - left stick controls forward/backward, right 
     * stick controls left/right */
     private void arcade2StickDrive() {
        double forwardVel = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kLeft)) * (reverse?-1:1);
        double rotateAmt = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kRight)) * (reverse?-1:1);
        Robot.TANK.setPolar(forwardVel * Robot.TANK.MAX_SPEED, rotateAmt * Robot.TANK.MAX_ANGULAR_SPEED);
     }

}
