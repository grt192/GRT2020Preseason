package frc.mechs;

import frc.robot.Mech;
import frc.config.Config;
import frc.input.Input;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Shooter extends Mech {

    private double powerHopper;
    private double powerFlywheel;
    private double powerFlywheelOrig;

    private final double maxSpeed;
    private final double speedChange;

    private TalonSRX motorHopper;
    private TalonSRX motorFlywheel;
    private XboxController controller = Input.MECH_XBOX;

    private boolean shooterOn = False;
    
    public Shooter() {
        this.motorHopper = new TalonSRX(Config.getInt("motor_hopper"));
        this.motorFlywheel = new TalonSRX(Config.getInt("motor_flywheel"));

        this.powerHopper = Config.getDouble("power_hopper");
        this.powerFlywheel = Config.getDouble("power_flywheel");
        this.powerFlywheelOrig = this.powerFlywheel;

        this.maxSpeed = Config.getDouble("max_speed");
        this.speedChange = Config.getDouble("speed_change");
        
        Config.defaultConfigTalon(motorHopper);
        Config.defaultConfigTalon(motorFlywheel);
    }

    public void loop () {
        // if x button is pressed, start shooter
        // if y button is pressed, stop shooter

        boolean xButton = controller.getXButtonReleased();
        boolean yButton = controller.getYButtonReleased();

        if (xButton && !shooterOn) shooterOn = true;
        if (yButton && shooterOn) shooterOn = false;

        // if left bumper pressed, increase bumper speed
        // if right bumper pressed, decrease bumper speed

        boolean leftBumper = controller.getBumperReleased​(GenericHID.Hand.kLeft);
        boolean rightBumper = controller.getBumperReleased​(GenericHID.Hand.kRight);

        if (leftBumper) powerFlywheel = Math.min(maxSpeed, powerFlywheel + speedChange);
        if (rightBumper) powerFlywheel = Math.max(0, powerFlywheel - speedChange);

        // if left stick pushed down, reset to original speed

        boolean leftStick = controller.getStickButtonReleased​(GenericHID.Hand.kLeft);
        if (leftStick) powerFlywheel = powerFlywheelOrig;

        // set motor speeds

        if (shooterOn) {
            motorHopper.set(ControlMode.PercentOutput, powerHopper);
            motorFlywheel.set(ControlMode.PercentOutput, powerFlywheel);
        } else {
            motorHopper.set(ControlMode.PercentOutput, 0);
            motorFlywheel.set(ControlMode.PercentOutput, 0);
        }
    }
}