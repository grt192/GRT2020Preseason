package frc.sequence;

import frc.config.Config;
import frc.robot.Robot;
import frc.util.GRTUtil;
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

    public OuttakeSequence(double speed, int time) {
        setSpeedTime(speed, time);
    }
    
    @Override
    public void runSequence() {
        outtake.stopEverything();
        outtake.setSpeed(speed);
        sleep(time);
        outtake.stopEverything();
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