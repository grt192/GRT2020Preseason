/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sequence;

import edu.wpi.first.wpilibj.Notifier;

/**
 * Add your docs here.
 */
public abstract class Sequence implements Runnable {

    public static OuttakeSequence OUTTAKE_IN;
    public static OuttakeSequence OUTTAKE_OUT;
    public static ElevatorSequence ELEVATOR_UP;
    public static ElevatorSequence ELEVATOR_DOWN;

    private volatile boolean isRunning;

    public static void initSequences() {
        OUTTAKE_IN = new OuttakeSequence(true);
        OUTTAKE_OUT = new OuttakeSequence(false);
        ELEVATOR_UP = new ElevatorSequence(true);
        ELEVATOR_DOWN = new ElevatorSequence(false);
    }

    private Notifier notifier;

    public Sequence() {
        notifier = new Notifier(this);
        isRunning = false;
    }

    public final void start() {
        notifier.startSingle(0);
    }

    @Override
    public final void run() {
        isRunning = true;
        runSequence();
        isRunning = false;
    }

    public abstract void runSequence();

    public boolean isRunning() {
        return isRunning;
    }

    public final Notifier getNotifier() {
        return notifier;
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
