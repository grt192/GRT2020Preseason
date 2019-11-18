package frc.mechs;

import frc.config.Config;
import frc.input.Input;

import edu.wpi.first.wpilibj.Solenoid;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

public class Climb extends Mech {
    private Solenoid solLeft;
    private Solenoid solRight;
    private XboxController controller = Input.MECH_XBOX;

    private boolean extended = False;

    public Climb() {
        solLeft = new Solenoid(Config.getInt("climb_sol_l"));
        solRight = new Solenoid(Config.getInt("climb_sol_r"));
    }

    public void loop() {
        // if a button is pressed, extend elevator
        // if b button is pressed, retract elevator

        boolean aButtonVal = controller.getAButtonReleased();
        double bButtonVal = controller.getBButtonReleased();

        if (aButtonVal && !extended) {
            solLeft.set(true);
            solRight.set(true);
        } else if (bButtonVal && extended) {
            solLeft.set(false);
            solRight.set(false);
        }

        // TODO: add backups on the joysticks?
    }
}