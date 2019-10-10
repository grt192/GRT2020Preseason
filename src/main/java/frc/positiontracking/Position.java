/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.positiontracking;

import frc.fieldmap.geometry.*;

public class Position {

    public final Vector pos;
    public final double angle;

    public Position(Vector pos, double angle) {
        this.pos = pos;
        this.angle = angle;
    }
}
