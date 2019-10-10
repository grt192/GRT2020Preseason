/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.vision;

import frc.config.Config;
import frc.fieldmap.VisionTarget;
import frc.fieldmap.geometry.Vector;
import frc.positiontracking.Position;
import frc.robot.Robot;
import frc.util.GRTUtil;

public class Camera {
    private Position relativePosition;
    private JeVois jeVois;

    public Camera(String name) {
        double x = Config.getDouble(name + "_x");
        double y = Config.getDouble(name + "_y");
        double angle = Config.getDouble(name + "_angle");
        relativePosition = new Position(new Vector(x, y), angle);
        jeVois = new JeVois();
        jeVois.start();
    }

    public Position getPositionEstimate(long maxAge) {
        if (System.currentTimeMillis() - jeVois.getLastReceivedTimestamp() > maxAge)
            return null;
        JeVoisMessage message = jeVois.getLastMessage();
        if (message == null)
            return null;
        double gyroAngle = Math.toRadians(Robot.GYRO.getAngle());
        Vector imageDisplacement = new Vector(message.translateZ, message.translateX);
        System.out.println("image: " + imageDisplacement);
        Vector robotPos = new Vector(Robot.POS_TRACKER.getX(), Robot.POS_TRACKER.getY());
        Vector estimate = relativePosition.pos.add(imageDisplacement.rotate(relativePosition.angle)).rotate(gyroAngle)
                .add(robotPos);
        double targetAngleEstimate = gyroAngle + message.rotateY + relativePosition.angle + Math.PI;
        VisionTarget target = Robot.FIELD_MAP.getNearestTarget(estimate, targetAngleEstimate);
        if (target == null)
            return null;
        double angleEstimate = -message.rotateY + Math.PI + target.pos.angle - relativePosition.angle;
        Vector betterEstimate = relativePosition.pos.add(imageDisplacement.rotate(relativePosition.angle))
                .rotate(gyroAngle).add(robotPos);
        Vector posEstimate = robotPos.add(target.pos.pos.subtract(estimate));
        // System.out.println(GRTUtil.positiveMod(Math.toDegrees(angleEstimate),
        // 360.0));
        System.out.println("pos: " + posEstimate);
        Position pos = new Position(posEstimate, angleEstimate);
        return pos;
    }

}
