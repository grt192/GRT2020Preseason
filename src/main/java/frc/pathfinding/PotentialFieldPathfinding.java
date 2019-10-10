package frc.pathfinding;

import frc.fieldmap.geometry.Polygon;
import frc.fieldmap.geometry.Vector;
import frc.robot.Robot;

public class PotentialFieldPathfinding {

	private Vector target;

	public PotentialFieldPathfinding() {
		target = new Vector(0, 0);
	}

	public Vector search(double x, double y) {
		double radius = Robot.ROBOT_RADIUS + 4.0;
		double smallRadius = Robot.ROBOT_RADIUS + 2.0;
		double r2 = radius * radius;
		double sr2 = smallRadius * smallRadius;
		Vector pos = new Vector(x, y);
		Vector velocity = target.subtract(pos).multiply(1 / target.distanceTo(pos));
		Vector emergency = new Vector(0, 0);
		boolean isEmergency = false;
		for (Polygon p : Robot.FIELD_MAP.getObstacles()) {
			Vector close = p.closestPoint(pos);
			double d2 = close.distanceSquaredTo(pos);
			if (d2 < sr2) {
				emergency = emergency.add(pos.subtract(close).multiply(1 / Math.sqrt(d2)));
				isEmergency = true;
			} else if (d2 < r2) {
				Vector displacement = pos.subtract(close);
				double project = (displacement.dot(velocity) / d2);
				if (project < 0) {
					Vector force = displacement.multiply(-project);
					velocity = velocity.add(force);
				}
			}
		}
		Vector close = Robot.FIELD_MAP.closestWallPoint(pos);
		double d2 = close.distanceSquaredTo(pos);
		if (d2 < sr2) {
			emergency = emergency.add(pos.subtract(close).multiply(1 / Math.sqrt(d2)));
			isEmergency = true;
		} else if (d2 < radius * radius) {
			Vector displacement = pos.subtract(close);
			double project = (displacement.dot(velocity) / d2);
			if (project < 0) {
				Vector force = displacement.multiply(-project);
				velocity = velocity.add(force);
			}
		}
		if (!isEmergency) {
			velocity = velocity.multiply(1 / velocity.magnitude());
		} else {
			velocity = emergency.multiply(1 / emergency.magnitude());
		}
		return velocity;
	}

	public Vector oldsearch(double x, double y) {
		double radius = Robot.ROBOT_RADIUS + 4.0;
		double r2 = radius * radius;
		Vector pos = new Vector(x, y);
		Vector velocity = target.subtract(pos).multiply(1 / target.distanceTo(pos));
		for (Polygon p : Robot.FIELD_MAP.getObstacles()) {
			Vector close = p.closestPoint(pos);
			double d2 = close.distanceSquaredTo(pos);
			if (d2 < r2) {
				Vector displacement = pos.subtract(close);
				double project = (displacement.dot(velocity) / d2);
				if (project < 0) {
					Vector force = displacement.multiply(-project);
					velocity = velocity.add(force);
				}
			}
		}
		Vector close = Robot.FIELD_MAP.closestWallPoint(pos);
		double d2 = close.distanceSquaredTo(pos);
		if (d2 < r2) {
			Vector displacement = pos.subtract(close);
			double project = (displacement.dot(velocity) / d2);
			if (project < 0) {
				Vector force = displacement.multiply(-project);
				velocity = velocity.add(force);
			}
		}
		velocity = velocity.multiply(1 / velocity.magnitude());
		return velocity;
	}

	public void setTarget(double x, double y) {
		target = new Vector(x, y);
	}

}
