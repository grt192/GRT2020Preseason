package frc.sequence;

import frc.config.Config;
import frc.robot.Robot;
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

    @Override
    public void runSequence() {
        elevator.stopEverything();
        long initalTime = System.currentTimeMillis();
        while (initalTime + time > System.currentTimeMillis()) {
            elevator.setSpeed(speed);
        }
        elevator.stopEverything();
    }

}