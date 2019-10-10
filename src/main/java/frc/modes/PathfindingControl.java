/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.modes;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.fieldmap.geometry.Circle;
import frc.fieldmap.geometry.Vector;
import frc.pathfinding.Pathfinding;
import frc.pathfinding.PotentialFieldPathfinding;
import frc.robot.Robot;

/**
 * Add your docs here.
 */
public class PathfindingControl extends Mode {

    private Vector target;
    private volatile Vector tempTarget;
    private volatile boolean newTarget;
    private Pathfinding pathfinding;
    private PotentialFieldPathfinding pfpf;
    private NetworkTableEntry targetEntry;

    public static final double SPEED = 0.5;

    public PathfindingControl() {
        pathfinding = new Pathfinding();
        pfpf = new PotentialFieldPathfinding();
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Pathfinding");
        targetEntry = table.getEntry("target");
        targetEntry.setString("0.0 0.0");
        targetEntry.addListener(event -> {
            String data = event.value.getString();
            String[] split = data.split(" ");
            tempTarget = new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
            if (Robot.FIELD_MAP.shapeIntersects(new Circle(tempTarget, Robot.ROBOT_RADIUS)))
                tempTarget = null;
            newTarget = true;
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kLocal);
    }

    @Override
    public boolean loop() {
        Robot.SWERVE.setRobotCentric(false);
        if (newTarget) {
            target = tempTarget;
            if (target != null)
                setTarget(target.x, target.y);
            newTarget = false;
        }
        if (target == null)
            return false;
        double xPos = Robot.POS_TRACKER.getX();
        double yPos = Robot.POS_TRACKER.getY();
        Vector pos = new Vector(xPos, yPos);
        Vector velocity;
        Vector endPos = pathfinding.search(xPos, yPos);
        if (endPos == null) {
            velocity = pfpf.search(xPos, yPos);
        } else {
            double d = pos.distanceTo(endPos);
            velocity = endPos.subtract(pos).multiply(1 / d);
        }
        double distance = pos.distanceTo(target);
        double speed = SPEED * Math.min(distance / 36.0, 1);
        velocity = velocity.multiply(speed);
        if (distance < 4) {
            Robot.SWERVE.drive(0, 0, 0);
            return false;
        }
        Robot.SWERVE.drive(velocity.x, velocity.y, 0);
        return true;

    }

    public void setTarget(double x, double y) {
        target = new Vector(x, y);
        pathfinding.setTargetNode(x, y);
        pfpf.setTarget(x, y);
    }

}
