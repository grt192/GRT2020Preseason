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
            time = Config.getInt("ball_dump_speed");
            speed = -elevator.getOuttakeSpeed();
        } else {
            time = Config.getInt("outtake_time_out");
            speed = outtake.getOuttakeSpeed();
        }
    }

    @Override
    public void runSequence() {
        outtake.stopEverything();
        outtake.setSpeed(speed);
        sleep(time);
        outtake.stopEverything();
    }

}