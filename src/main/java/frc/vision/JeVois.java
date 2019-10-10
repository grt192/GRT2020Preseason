package frc.vision;

import edu.wpi.first.hal.util.UncleanStatusException;
import edu.wpi.first.wpilibj.SerialPort;

public class JeVois extends Thread {

    private boolean enabled = true;

    private SerialPort camera;
    public String lastString;
    public volatile JeVoisMessage lastMessage = null;
    public volatile long lastReceivedTimestamp;

    public JeVois() {
        this(SerialPort.Port.kUSB);
    }

    public JeVois(SerialPort.Port port) { // port should be kUSB, kUSB1, or kUSB2
        try {
            this.camera = new SerialPort(115200, port);
        } catch (UncleanStatusException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (camera == null)
            return;
        while (true) {
            if (this.enabled) {
                try {
                    String body = camera.readString().trim();
                    String[] lines = body.split("\n");
                    if (lines.length == 0) {
                        continue;
                    }
                    String line = lines[lines.length - 1];
                    if (line.split(" ").length < 8) {
                        continue;
                    }
                    this.lastString = line;
                    if (!lastString.equals("")) {
                        System.out.println(line);
                        lastReceivedTimestamp = System.currentTimeMillis();
                        this.lastMessage = new JeVoisMessage(lastString, lastReceivedTimestamp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public JeVoisMessage getLastMessage() {
        return lastMessage;
    }

    public long getLastReceivedTimestamp() {
        return lastReceivedTimestamp;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

}