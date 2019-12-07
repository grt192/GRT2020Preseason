package frc.sequence;

import frc.config.Config;
import frc.robot.Robot;
import frc.mechs.OuttakeMech;

public class OuttakeSequence extends Sequence {
    private boolean in;
    private int time;
    private double speed;

    private OuttakeMech outtake;

    /** @param in Whether this outtake sequence is for moving the mech
     * out or in. */
    public OuttakeSequence(boolean in) {
        outtake = Robot.OUTTAKE;
        this.in = in;
        if (in) {
            time = Config.getInt("outtake_time_in");
            speed = outtake.getOuttakeSpeed();
        } else {
            time = Config.getInt("outtake_time_out");
            speed = -outtake.getOuttakeSpeed();
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