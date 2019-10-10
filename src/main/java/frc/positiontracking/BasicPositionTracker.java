/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.positiontracking;

import frc.swerve.SwerveData;
import frc.robot.*;

/**
 * Add your docs here.
 */
public class BasicPositionTracker extends PositionTracker {

    private volatile double x;
    private volatile double y;
    private long lastUpdate;

    public BasicPositionTracker() {
        set(0, 0);
    }

    @Override
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void update() {
        SwerveData data = Robot.SWERVE.getSwerveData();
        long temp = System.currentTimeMillis();
        double dt = (temp - lastUpdate) / 1000.0;
        x += data.encoderVX * dt;
        y += data.encoderVY * dt;
        lastUpdate = temp;
    }
}
