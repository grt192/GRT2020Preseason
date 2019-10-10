/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sequence;

import frc.robot.*;

/**
 * Add your docs here.
 */
public class IntakeHatch extends Sequence {

    @Override
    public void runSequence() {
        Robot.HATCHES.setMiddle(false);
    }

}
