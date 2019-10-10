/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.fieldmap;

import java.util.HashSet;
import java.util.Set;

import frc.fieldmap.geometry.Circle;
import frc.fieldmap.geometry.Polygon;
import frc.fieldmap.geometry.Vector;
import frc.robot.Robot;
import frc.util.GRTUtil;

/**
 * Add your docs here.
 */
public class FieldMap {

	public double FIELD_WIDTH, FIELD_HEIGHT;
	private Vector bounds;
	private Polygon wall;
	private Polygon[] obstacles;
	public VisionTarget[] visionTargets;
	private double reflectionLineX, reflectionLineY;

	public FieldMap() {
		buildMap();
		// testMap();
		// testMapShop();
		bounds = new Vector(FIELD_HEIGHT, FIELD_WIDTH);
		wall = new Polygon(new Vector(0, 0), new Vector(FIELD_HEIGHT, 0), new Vector(FIELD_HEIGHT, FIELD_WIDTH),
				new Vector(0, FIELD_WIDTH));
	}

	public boolean lineOfSight(Vector v1, Vector v2) {
		Vector dif = v2.subtract(v1);
		double d = v1.distanceTo(v2);
		if (d == 0.0)
			return true;
		Vector norm = dif.multiply(Robot.ROBOT_RADIUS / d).normal();
		Polygon rect = new Polygon(v1.add(norm), v2.add(norm), v2.subtract(norm), v1.subtract(norm));
		Circle startCircle = new Circle(v1, Robot.ROBOT_RADIUS);
		Circle endCircle = new Circle(v2, Robot.ROBOT_RADIUS);
		if (shapeIntersects(rect))
			return false;
		if (shapeIntersects(startCircle))
			return false;
		if (shapeIntersects(endCircle))
			return false;
		return true;
	}

	public boolean shapeIntersects(Polygon p) {
		if (p.outsideBounds(bounds))
			return true;
		for (Polygon poly : obstacles) {
			if (p.intersects(poly))
				return true;
		}
		return false;
	}

	public boolean shapeIntersects(Circle c) {
		if (c.outsideBounds(bounds))
			return true;
		for (Polygon poly : obstacles) {
			if (c.intersects(poly))
				return true;
		}
		return false;
	}

	public VisionTarget getNearestTarget(Vector robotPos, Vector image) {
		double min = Double.POSITIVE_INFINITY;
		VisionTarget best = null;
		for (VisionTarget vt : visionTargets) {
			double dist = vt.pos.pos.add(image.rotate(vt.pos.angle)).distanceSquaredTo(robotPos);
			if (dist < min) {
				min = dist;
				best = vt;
			}
		}
		return best;
	}

	public VisionTarget getNearestTarget(Vector estimate, double angleEstimate) {
		double min = 60 * 60;
		VisionTarget best = null;
		int j = -1;
		double angleError = Math.toRadians(20);
		for (int i = 0; i < visionTargets.length; ++i) {
			VisionTarget vt = visionTargets[i];
			if (Math.abs(GRTUtil.distanceToAngle(vt.pos.angle, angleEstimate)) < angleError) {
				double dist = vt.pos.pos.distanceSquaredTo(estimate);
				if (dist < min) {
					min = dist;
					best = vt;
					j = i;
				}
			}
		}
		// System.out.println(j);
		return best;
	}

	public Vector closestWallPoint(Vector p) {
		return wall.closestPoint(p);
	}

	public Polygon[] getObstacles() {
		return obstacles;
	}

	public VisionTarget[] getVisionTargets() {
		return visionTargets;
	}

	public Set<Vector> generateNodes() {
		double radius = Robot.ROBOT_RADIUS + 1.0;
		double bigRadius = radius + 0.5;
		Set<Vector> nodeSet = new HashSet<>();
		for (Polygon p : obstacles) {
			Vector[] nodes = p.getPossibleNodes(bigRadius);
			for (Vector v : nodes) {
				Circle c = new Circle(v, radius);
				if (!shapeIntersects(c))
					nodeSet.add(v);
			}
		}
		return nodeSet;
	}

	private void buildMap() {
		FIELD_WIDTH = 27 * 12;
		FIELD_HEIGHT = 54 * 12;
		visionTargets = new VisionTarget[32];
		reflectionLineX = FIELD_HEIGHT / 2;
		reflectionLineY = FIELD_WIDTH / 2;
		VisionTarget leftIntake = new VisionTarget(new Vector(0, 25.715), 0, false);
		VisionTarget rightIntake = leftIntake.flipVisionTargetY(reflectionLineY);

		VisionTarget leftLeftSideCargoBay = new VisionTarget(new Vector(304.25, 133.094), -Math.PI / 2, false);
		VisionTarget centerLeftSideCargoBay = new VisionTarget(new Vector(282.5, 133.094), -Math.PI / 2, false);
		VisionTarget rightLeftSideCargoBay = new VisionTarget(new Vector(260.75, 133.094), -Math.PI / 2, false);

		VisionTarget leftRightSideCargoBay = leftLeftSideCargoBay.flipVisionTargetY(reflectionLineY);
		VisionTarget centerRightSideCargoBay = centerLeftSideCargoBay.flipVisionTargetY(reflectionLineY);
		VisionTarget rightRightSideCargoBay = rightLeftSideCargoBay.flipVisionTargetY(reflectionLineY);

		VisionTarget leftFrontCargoBay = new VisionTarget(new Vector(220.219, 150.125), Math.PI, false);
		VisionTarget rightFrontCargoBay = leftFrontCargoBay.flipVisionTargetY(reflectionLineY);

		VisionTarget leftSideLeftRocket = new VisionTarget(new Vector(214.491, 18.116), 2.64, false);
		VisionTarget centerLeftRocket = new VisionTarget(new Vector(229.125, 27.442), Math.PI / 2, true);
		VisionTarget rightSideLeftRocket = new VisionTarget(new Vector(243.759, 18.116), .5, false);

		VisionTarget rightSideRightRocket = leftSideLeftRocket.flipVisionTargetY(reflectionLineY);
		VisionTarget centerRightRocket = centerLeftRocket.flipVisionTargetY(reflectionLineY);
		VisionTarget leftSideRightRocket = rightSideLeftRocket.flipVisionTargetY(reflectionLineY);
		visionTargets[0] = leftIntake;
		visionTargets[1] = rightIntake;
		visionTargets[2] = leftSideLeftRocket;
		visionTargets[3] = centerLeftRocket;
		visionTargets[4] = rightSideLeftRocket;
		visionTargets[5] = leftSideRightRocket;
		visionTargets[6] = centerRightRocket;
		visionTargets[7] = rightSideRightRocket;
		visionTargets[8] = leftFrontCargoBay;
		visionTargets[9] = rightFrontCargoBay;
		visionTargets[10] = leftRightSideCargoBay;
		visionTargets[11] = rightRightSideCargoBay;
		visionTargets[12] = centerRightSideCargoBay;
		visionTargets[13] = leftLeftSideCargoBay;
		visionTargets[14] = rightLeftSideCargoBay;
		visionTargets[15] = centerLeftSideCargoBay;
		for (int i = 0; i < visionTargets.length / 2; i++) {
			visionTargets[i + visionTargets.length / 2] = visionTargets[i].flipVisionTargetX(reflectionLineX);
		}

		obstacles = new Polygon[10];
		Polygon habZoneClose = new Polygon(new Vector(47.75, 71.375), new Vector(0, 71.375), new Vector(0, 250.625),
				new Vector(47.75, 250.625));

		Polygon levelsClose = new Polygon(new Vector(47.75, 236.276), new Vector(95.41, 236.276),
				new Vector(95.41, 85.724), new Vector(47.75, 85.724));

		Polygon leftRocketClose = new Polygon(new Vector(211.433, 0), new Vector(211.433, 7.75),
				new Vector(219.801, 27.474), new Vector(238.584, 27.474), new Vector(249.5, 7.75),
				new Vector(249.5, 0));

		Polygon cargoBayClose = new Polygon(new Vector(219.625, 138.175), new Vector(249.554, 132.5),
				new Vector(324, 132.5), new Vector(324, 189.5), new Vector(249.554, 189.5),
				new Vector(219.625, 183.625));

		Polygon rightRocketClose = leftRocketClose.flipPolygonY(reflectionLineY);

		obstacles[0] = habZoneClose;
		obstacles[1] = leftRocketClose;
		obstacles[2] = cargoBayClose;
		obstacles[3] = rightRocketClose;
		obstacles[4] = levelsClose;
		for (int i = 0; i < obstacles.length / 2; i++) {
			obstacles[i + obstacles.length / 2] = obstacles[i].flipPolygonX(reflectionLineX);
		}
	}

	private void testMap() {
		FIELD_WIDTH = 14 * 12;
		FIELD_HEIGHT = 14 * 12;
		obstacles = new Polygon[2];
		reflectionLineX = FIELD_HEIGHT / 2;
		Polygon table = new Polygon(new Vector(48, 72), new Vector(48, 96), new Vector(120, 96), new Vector(120, 72));
		Polygon cargoShip = new Polygon(new Vector(84, 168), new Vector(84, 151), new Vector(108, 151),
				new Vector(108, 168));
		obstacles[1] = table;
		obstacles[0] = cargoShip;
		// obstacles[0] = new Polygon(new Vector(0, 0), new Vector(FIELD_HEIGHT, 0), new
		// Vector(FIELD_HEIGHT, FIELD_WIDTH),
		// new Vector(0, FIELD_WIDTH));

		visionTargets = new VisionTarget[1];
		visionTargets[0] = new VisionTarget(new Vector(96, 151), -Math.PI / 2, false);
	}

	private void testMapShop() {
		FIELD_WIDTH = 14 * 12 + 10;
		FIELD_HEIGHT = 16 * 12;
		obstacles = new Polygon[4];
		Polygon rocket = new Polygon(new Vector(130, FIELD_WIDTH), new Vector(130, FIELD_WIDTH - 3.5),
				new Vector(130 + 9, FIELD_WIDTH - 22), new Vector(130 + 18.5 + 9, FIELD_WIDTH - 22),
				new Vector(130 + 9 + 9 + 18.5, FIELD_WIDTH - 3.5), new Vector(130 + 9 + 9 + 18.5, FIELD_WIDTH));
		Polygon habzone = new Polygon(new Vector(0, 65.5), new Vector(67, 65.5), new Vector(67, 116),
				new Vector(0, 116));
		VisionTarget intake = new VisionTarget(new Vector(0, 29.75), 0, false);
		VisionTarget rightSideRocket = new VisionTarget(
				new Vector((2 * (105 + 18.5 + 9) + 9) / 2 + 25, (2 * FIELD_WIDTH - 3.5 - 22) / 2),
				-2.0944 + Math.PI / 2, false);
		VisionTarget centerSideRocket = new VisionTarget(new Vector(123.25 + 25, 156), -Math.PI / 2, true);
		VisionTarget leftSideRocket = new VisionTarget(new Vector(109.5 + 25, 165.25), -1.0472 - Math.PI / 2, false);
		Polygon level2 = new Polygon(new Vector(0, 116), new Vector(22, 116), new Vector(22, FIELD_WIDTH - 20.5),
				new Vector(0, FIELD_WIDTH - 20.5));
		Polygon cargoShip = new Polygon(new Vector(FIELD_HEIGHT - 23.5, 0), new Vector(FIELD_HEIGHT, 0),
				new Vector(FIELD_HEIGHT, 28), new Vector(FIELD_HEIGHT - 23.5, 28));
		obstacles[0] = rocket;
		obstacles[1] = habzone;
		obstacles[2] = level2;
		obstacles[3] = cargoShip;

		visionTargets = new VisionTarget[4];
		visionTargets[0] = intake;
		visionTargets[1] = leftSideRocket;
		// visionTargets[2] = rightSideRocket;
		visionTargets[2] = centerSideRocket;
		visionTargets[3] = new VisionTarget(new Vector(FIELD_HEIGHT - 11, 28), Math.PI / 2, false);
		FIELD_HEIGHT += 12;
	}
}