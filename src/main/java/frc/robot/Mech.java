package frc.robot;

public abstract class Mech implements Runnable {

    private Thread thread;

    public Mech() {
    }

    public final void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public final void run() {
        while (!thread.isInterrupted()) {
            try {
                loop();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public abstract void loop() throws InterruptedException;

    public final void stop() {
        if (thread != null)
            thread.interrupt();
    }

}