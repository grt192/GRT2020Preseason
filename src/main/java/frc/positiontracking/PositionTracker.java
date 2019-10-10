/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.positiontracking;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public abstract class PositionTracker {

    private NetworkTableEntry xPos, yPos;
    private NetworkTableEntry set;

    public PositionTracker() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("PositionTracking");
        xPos = table.getEntry("x");
        yPos = table.getEntry("y");
        set = table.getEntry("set");
        set.addListener((event) -> {
            String data = event.value.getString();
            String[] split = data.split(" ");
            set(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    public abstract void set(double x, double y);

    public abstract double getX();

    public abstract double getY();

    public abstract void update();

    public final void sendToNetwork() {
        xPos.setDouble(getX());
        yPos.setDouble(getY());
    }
}
