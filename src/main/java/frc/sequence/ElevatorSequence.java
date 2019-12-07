package frc.sequence;

import frc.config.Config;
import frc.robot.Robot;
import frc.util.GRTUtil;
import frc.mechs.ElevatorMech;

public class ElevatorSequence extends Sequence {
    private boolean up;
    private int time;
    private double speed;

    private ElevatorMech elevator;

    /** @param in Whether this elevator sequence is for moving the 
     * elevator up or down */
    public ElevatorSequence(boolean up) {
        elevator = Robot.ELEVATOR;
        this.up = up;
        if (up) {
            time = Config.getInt("ball_dump_time");
            speed = -elevator.getBallDumpSpeed();
        } else {
            time = Config.getInt("ball_dump_time");
            speed = elevator.getBallDumpSpeed();
        }
    }

    public ElevatorSequence(double speed, int time) {
        up = true;
        this.speed = speed;
        this.time = time;
    }

    @Override
    public void runSequence() {
        elevator.stopEverything();
        long initalTime = System.currentTimeMillis();
        while (initalTime + time > System.currentTimeMillis()) {
            elevator.setSpeed(speed);
        }
        elevator.stopEverything();
    }

    /** sets the speed to run the mech at, from -1.0 to 1.0 */
    public void setSpeed(double speed) {
        this.speed = GRTUtil.clamp(-1.0, speed, 1.0);
    }

    /** sets the time to run the mech for, in milliseconds */
    public void setTime(int time) {
        this.time = Math.abs(time);
    }

    /** sets the speed (-1.0 to 1.0) and the time to run the mech (ms) */
    public void setSpeedTime(double speed, int time) {
        setSpeed(speed);
        setTime(time);
    }
}