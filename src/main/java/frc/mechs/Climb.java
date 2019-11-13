package frc.mechs;

import frc.config.Config;
import frc.robot.Mech;

import edu.wpi.first.wpilibj.Solenoid;

public class Climb extends Mech {
    private Solenoid solLeft;
    private Solenoid solRight;

    public Climb() {
        solLeft = new Solenoid(Config.getInt("climb_sol_l"));
        solRight = new Solenoid(Config.getInt("climb_sol_r"));
        // TODO: change config
    }

    public void extend() {
        solLeft.set(true);
        solRight.set(true);
    }

    public void retract() {
        solLeft.set(false);
        solRight.set(true);
    }
}