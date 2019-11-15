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

/**
 * Add your docs here.
 */
class DriverControl extends Mode {

    @Override
    public boolean loop() {
        driveSwerve();
        return true;
    }

    private void driveSwerve() {
        double x = JoystickProfile.applyProfile(Input.SWERVE_XBOX.getY(Hand.kLeft));
        double y = JoystickProfile.applyProfile(-Input.SWERVE_XBOX.getX(Hand.kLeft));

        double lTrigger = Input.SWERVE_XBOX.getTriggerAxis(Hand.kLeft);
        double rTrigger = Input.SWERVE_XBOX.getTriggerAxis(Hand.kRight);
        double rotate = 0;
        if (lTrigger + rTrigger > 0.05) {
            rotate = -(rTrigger * rTrigger - lTrigger * lTrigger);
        }
        Robot.SWERVE.drive(x, y, rotate);
    }

}
