/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.mechs;

import edu.wpi.first.wpilibj.Solenoid;
import frc.config.Config;

/**
 * Add your docs here.
 */
public class Climber {

    private Solenoid front;
    private Solenoid back;

    public Climber() {
        front = new Solenoid(Config.getInt("climber_front"));
        back = new Solenoid(Config.getInt("climber_back"));
    }

    public void setFront(boolean on) {
        front.set(on);
    }

    public void setBack(boolean on) {
        back.set(on);
    }

    public void toggleFront() {
        front.set(!front.get());
    }

    public void toggleBack() {
        back.set(!back.get());
    }
}
