/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sequence;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import frc.input.Input;

/**
 * Add your docs here.
 */
public class RumbleSeguence extends Sequence {

    @Override
    public void runSequence() {
        Input.SWERVE_XBOX.setRumble(RumbleType.kLeftRumble, 1.0);
        Input.SWERVE_XBOX.setRumble(RumbleType.kRightRumble, 1.0);
        sleep(500);
        Input.SWERVE_XBOX.setRumble(RumbleType.kLeftRumble, 0.0);
        Input.SWERVE_XBOX.setRumble(RumbleType.kRightRumble, 0.0);
    }
}
