import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class RPLidar implements Runnable {

    public RPLidar() {

    }

    @Override
    public void run() {

        System.out.println("RPLidar.main 000");
        String port = "/dev/serial/by-id/usb-Silicon_Labs_CP2102_USB_to_UART_Bridge_Controller_0001-if00-port0";
        String fileLocation = "/home/pi/Documents/GRTLidar/";
        int baud = 115200;
        try (RPLidarDriver drv = RPLidarDriver.create()) {

            System.out.println("RPLidar.main 010");
            drv.connect(port, baud);
            System.out.println("RPLidar.main 020");
            DeviceInfo deviceInfo = drv.getDeviceInfo();
            System.out.println("RPLidar.main 030");

            System.out.println("model = " + deviceInfo.model + " firmware = " + deviceInfo.getFirmwareString() + "("
                    + deviceInfo.firmwareVersion + ")" + " hardware = " + deviceInfo.hardwareVersion + " serial = "
                    + deviceInfo.getSerialnumString());

            System.out.println("RPLidar.main 9999");
            DeviceHealth deviceHealth = drv.getDeviceHealth();
            System.out.println("status = " + deviceHealth.status + " error = " + deviceHealth.errorCode);
            if (!deviceHealth.isOk()) {
                System.out.println("Status Bad");
                return;

            }

            try {
                drv.startMotor();
                try {
                    drv.startScan(false, true);
                    Measurement[] measurements = new Measurement[8192];
                    for (int i = 0; i < measurements.length; i++) {
                        measurements[i] = new Measurement();
                    }
                    File fileDone = new File(fileLocation + "data.txt");

                    while (true) {
                        // long time = System.currentTimeMillis();
                        File fileTemp = new File(fileDone.getPath() + ".temp");
                        drv.grabScanDataHq(measurements);
                        boolean seenNonZeroAngle = false;
                        boolean seen359 = false;
                        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileTemp))) {

                            for (int i = 0; i < measurements.length; i++) {
                                // long t =System.currentTimeMillis();
                                Measurement m = measurements[i];
                                float angle = m.getAngle();
                                if (angle == 0 && seenNonZeroAngle) {
                                    break;
                                }
                                if (seen359) {
                                    break;
                                }
                                seenNonZeroAngle = true;
                                if (359 == (int) m.getDistance()) {
                                    seen359 = true;
                                }
                                String data = new String(String.format("%s theta: %03.2f Dist: %08.2f Q: %d",
                                        m.isSyncBit() ? "S " : "  ", angle, m.getDistance(), m.getQuality()));
                                // System.out.println(data);
                                out.write(data + "\n");
                                // System.out.println(System.currentTimeMillis()-t);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!fileTemp.renameTo(fileDone)) {
                            System.out.println("File failed to rename " + fileTemp + " to " + fileDone);
                        }
                        // .out.println(System.currentTimeMillis() - time);
                    }
                } finally {
                    drv.stop();
                }
            } finally {
                drv.stopMotor();
            }

        }
    }

}