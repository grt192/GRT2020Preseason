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

    @Override
    public boolean loop() {
        driveTank();
        return true;
    }

    private void driveTank() {
        // You may be wondering, why x is .getY() and y is .getX()? Don't question it
        double x = JoystickProfile.applyDeadband(-Input.TANK_XBOX.getY(Hand.kLeft));
        double y = JoystickProfile.applyDeadband(Input.TANK_XBOX.getX(Hand.kLeft));

        Robot.TANK.setPolar(x, y);
    }

}
