package frc.mechs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.config.Config;
import frc.input.Input;
import frc.robot.Mech;

public class Shooter extends Mech {

    // power (speed) that the motor in the hopper will run at
    private double powerHopper;

    // power (speed) that the motor for the flywheels will run at
    private double powerFlywheel;
    private double powerFlywheelOrig;

    // max speed the talon should run at
    private final double maxSpeed;

    // increment that the bumpers will change the speed of the flywheel motor
    private final double speedChange;

    private TalonSRX motorHopper;
    private TalonSRX motorFlywheel;
    private XboxController controller = Input.MECH_XBOX;

    // whether or not the shooter motors (hopper + flywheel) are running
    private boolean shooterOn = false;

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

    public void loop() {
        // if x button is pressed, start shooter
        // if y button is pressed, stop shooter

        boolean xButton = controller.getXButtonReleased();
        boolean yButton = controller.getYButtonReleased();

        // if the x button is pressed and the shooter is not on, turn it on
        if (xButton && !shooterOn)
            shooterOn = true;

        // if the y button is pressed and the shooter is on, turn it off
        if (yButton && shooterOn)
            shooterOn = false;

        // if left bumper pressed, increase bumper speed
        // if right bumper pressed, decrease bumper speed
        // needs to be getBumperReleased because otherwise this would be called every
        // time it loops, causing many more updates than expected

        boolean leftBumper = controller.getBumperReleased(GenericHID.Hand.kLeft);
        boolean rightBumper = controller.getBumperReleased(GenericHID.Hand.kRight);

        if (leftBumper)
            powerFlywheel = Math.min(maxSpeed, powerFlywheel + speedChange);
        if (rightBumper)
            powerFlywheel = Math.max(0, powerFlywheel - speedChange);

        // if both bumpers pressed, reset to original speed
        // may need to change to controller.getBumper() if it is hard to release both at
        // exact same time

        if (leftBumper && rightBumper)
            powerFlywheel = powerFlywheelOrig;

        // set motor speeds

        if (shooterOn) {
            motorHopper.set(ControlMode.PercentOutput, powerHopper);
            motorFlywheel.set(ControlMode.PercentOutput, powerFlywheel);
        } else {
            motorHopper.set(ControlMode.PercentOutput, 0);
            motorFlywheel.set(ControlMode.PercentOutput, 0);
        }
    }

    // allows for shooter to be turned on without a button press (for auton)
    public void setShooterOn(boolean val) {
        shooterOn = val;
    }
}