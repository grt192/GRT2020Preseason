package frc.mechs;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Solenoid;
import frc.config.Config;

public class Hatches {

    private Solenoid top;
    private Solenoid middle;
    private Solenoid bottom;

    private PWM hookServo;

    public Hatches() {
        top = new Solenoid(Config.getInt("hatch_top"));
        middle = new Solenoid(Config.getInt("hatch_middle"));
        bottom = new Solenoid(Config.getInt("hatch_bottom"));
        hookServo = new PWM(Config.getInt("hatch_servo"));
        hookServo.setBounds(2, 1.5, 1.5, 1.5, 1);
        hookServo.setSpeed(0);
    }

    public void setTop(boolean on) {
        top.set(on);
    }

    public void setMiddle(boolean on) {
        middle.set(on);
    }

    public void toggleMiddle() {
        middle.set(!middle.get());
    }

    public void setBottom(boolean on) {
        bottom.set(on);
    }

    public void setHookSpeed(double speed) {
        hookServo.setSpeed(speed);
    }
}