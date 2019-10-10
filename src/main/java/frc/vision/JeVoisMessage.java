package frc.vision;

public class JeVoisMessage {

    public final String originalLine;

    public final long receivedTime; // millis
    public final double cameraTime; // seconds since camera startup

    public final double translateX; // inches
    public final double translateY;
    public final double translateZ;

    public final double rotateX; // radians
    public final double rotateY;
    public final double rotateZ;

    public JeVoisMessage(String line, Long receivedTime) throws NumberFormatException {
        this.originalLine = line;
        this.receivedTime = receivedTime;

        String[] split = line.split(" ");

        this.cameraTime = Double.valueOf(split[0]);

        // this.retval = Double.valueOf(split[1]);

        this.translateX = Double.valueOf(split[2]);
        this.translateY = Double.valueOf(split[3]);
        this.translateZ = Double.valueOf(split[4]);

        this.rotateX = Double.valueOf(split[5]);
        this.rotateY = Double.valueOf(split[6]);
        this.rotateZ = Double.valueOf(split[7]);
    }

    public String toString() {
        return originalLine;
    }

}