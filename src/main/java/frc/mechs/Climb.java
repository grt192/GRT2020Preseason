package frc.mechs;

import frc.robot.Mech;
import frc.config.Config;
import frc.input.Input;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.XboxController;

public class Climb extends Mech {
    private Solenoid solLeft;
    private Solenoid solRight;
    private XboxController controller = Input.MECH_XBOX;

    private boolean extended = false;

    public Climb() {
        solLeft = new Solenoid(Config.getInt("climb_sol_l"));
        solRight = new Solenoid(Config.getInt("climb_sol_r"));
    }

    public void loop() {
        // if a button is pressed, extend elevator
        // if b button is pressed, retract elevator

        boolean aButton = controller.getAButtonReleased();
        boolean bButton = controller.getBButtonReleased();

        if (aButton && !extended) {
            solLeft.set(true);
            solRight.set(true);
            extended = true;
        } else if (bButton && extended) {
            solLeft.set(false);
            solRight.set(false);
            extended = false;
        }
    }

    // allows for pneumatic to be extended without a button press (for auton)
    public void setExtended(boolean val) {
        extended = val;
    }
}