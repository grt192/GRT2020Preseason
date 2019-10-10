package frc.mechs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import frc.config.Config;

public class BottomIntake {

    private Solenoid intake;
    private TalonSRX intakeMotor;
    private boolean desiredPos;

    public BottomIntake() {
        this.intake = new Solenoid(Config.getInt("roller_sol"));
        this.intakeMotor = new TalonSRX(Config.getInt("roller"));
        Config.defaultConfigTalon(intakeMotor);
        intakeMotor.setNeutralMode(NeutralMode.Coast);
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public void in() {
        desiredPos = false;
        intake.set(false);
    }

    public void out() {
        desiredPos = true;
        intake.set(true);
    }

    public void toggle() {
        if (intake.get())
            in();
        else
            out();
    }

    public void forceOut() {
        intake.set(true);
    }

    public void setToDesiredPos() {
        intake.set(desiredPos);
    }

    public boolean getPosition() {
        return intake.get();
    }
}