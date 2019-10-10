/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.fieldmap;

import frc.fieldmap.geometry.Vector;
import frc.positiontracking.Position;

/**
 * Add your docs here.
 */
public class VisionTarget {

    private static final double HIGH_HEIGHT = 39.125;
    private static final double LOW_HEIGHT = 31.5;

    public final Position pos;
    public final boolean high;
    public final double height;

    public VisionTarget(Vector pos, double angle, boolean high) {
        this.pos = new Position(pos, angle);
        this.high = high;
        if (high) {
            height = HIGH_HEIGHT;
        } else {
            height = LOW_HEIGHT;
        }
    }

    public VisionTarget flipVisionTargetX(double line) {
        double angle = Math.PI - pos.angle;
        return new VisionTarget(new Vector(2 * line - pos.pos.x, pos.pos.y), angle, high);
    }

    public VisionTarget flipVisionTargetY(double line) {
        double angle = -pos.angle;
        return new VisionTarget(new Vector(pos.pos.x, 2 * line - pos.pos.y), angle, high);
    }
}
