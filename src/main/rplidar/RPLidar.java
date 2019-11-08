import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class RPLidar {

    public static final void main(String[] args) {

        System.out.println("RPLidar.main 000");
        String port = "/dev/serial/by-id/usb-Silicon_Labs_CP2102_USB_to_UART_Bridge_Controller_0001-if00-port0";
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
                    while (true) {
                        drv.grabScanDataHq(measurements);
                        boolean seenNonZeroAngle = false;
                        for (int i = 0; i < measurements.length; i++) {
                            Measurement m = measurements[i];
                            float angle = m.getAngle();
                            if (angle == 0 && seenNonZeroAngle) {
                                break;
                            }
                            seenNonZeroAngle = true;

                            String data = new String(String.format("%s theta: %03.2f Dist: %08.2f Q: %d",
                                    m.isSyncBit() ? "S " : "  ", angle, m.getDistance(), m.getQuality()));
                            System.out.println(data);
                            dataToFile(data);
                        }
                    }
                } finally {
                    drv.stop();
                }
            } finally {
                drv.stopMotor();
            }

        }
    }

    private static void dataToFile(String data) {
        File file = new File("/home/lvuser/deploy/data.txt");
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            br.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}