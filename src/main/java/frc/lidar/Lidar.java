package frc.lidar;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Lidar implements Closeable {
    //

    // Note Let it run for more than 2 minutes for improved accuracy
    // Code for RoboPeak Lidar model A1
    // Helpful Links
    // http://www.slamtec.com/en/Lidar/A1
    // http://www.robopeak.com/
    // https://github.com/slamtec/rplidar_sdk
    // https://github.com/slamtec/rplidar_ros

    // Bod rate 115200
    // 8N1 = 8 Data Bits, No Parity, 1 Stop Bit
    // stty -F /dev/ttyUSB0 115200 cs8 -cstopb -parenb

    // Introduction and Datasheet
    // http://bucket.download.slamtec.com/60ef2c5a82129bf7213f0ff5d51e732d1c124c3a/LD108_SLAMTEC_rplidar_datasheet_A1M8_v2.3_en.pdf

    // Developer Kit User Manual
    // http://bucket.download.slamtec.com/e680b4e2d99c4349c019553820904f28c7e6ec32/LM108_SLAMTEC_rplidarkit_usermaunal_A1M8_v1.0_en.pdf

    // Interface Protocol and Application Notes
    // http://bucket.download.slamtec.com/ccb3c2fc1e66bb00bd4370e208b670217c8b55fa/LR001_SLAMTEC_rplidar_protocol_v2.1_en.pdf

    // Introduction to Standard SDK
    // http://bucket.download.slamtec.com/351a5409ddfba077ad11ec5071e97ba5bf2c5d0a/LR002_SLAMTEC_rplidar_sdk_v1.0_en.pdf

    public static final String PORT = "/dev/serial/by-id/usb-Silicon_Labs_CP2102_USB_to_UART_Bridge_Controller_0001-if00-port0";

    public static final byte CMD_GET_HEALTH = 0x52;
    public static final byte CMD_STOP = 0x25;
    public static final byte CMD_RESET = 0x40;
    public static final byte CMD_SCAN = 0x20;
    public static final byte CMD_GET_INFO = 0x50;
    public static final byte CMD_ACC_BOARD_FLAG = (byte) 0xFF;

    public static final byte REQUEST_START_FLAG = (byte) 0xA5;
    public static final byte RESPONSE_START_FLAG1 = (byte) 0xA5;
    public static final byte RESPONSE_START_FLAG2 = 0x5A;

    private FileInputStream in;
    private FileOutputStream out;

    // Initializes the Lidar
    public boolean init() {

        // GET_LIDAR_CONF
        // SCAN

        try {
            System.out.println("Lidar.init");
            File port = new File(PORT);

            if (!port.exists()) {
                System.out.println("Unable to Access " + PORT);
            }
            System.out.println("Lidar.init Opening input");
            in = new FileInputStream(port);
            System.out.println("Lidar.init Opening output");
            out = new FileOutputStream(port);
            System.out.println("Lidar.init getHealthStatus");
            getHealthStatus();

            System.out.println("Lidar.init stop");
            stop();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Lindar.init reset");

            // reset();

            System.out.println("Lidar.init scan");
            scan();

            System.out.println("Lidar.init getting health status");
            HealthStatus healthStatus = getHealthStatus();
            System.out.println("healthStatus.status = " + healthStatus.status);
            System.out.println("healthStatus.error_code = " + healthStatus.error_code);
            if (healthStatus.status != HealthStatus.STATUS_GOOD) {
                System.out.println("Health Status not good.");
                return false;
            }

            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }

    public void sendRequest(byte command, byte[] payload) throws IOException {
        System.out.println("Sending a request " + byteToHexString(command));
        int payloadLength = (payload == null) ? 0 : (2 + payload.length); // 2 = payload size, checksum
        byte[] request = new byte[2 + payloadLength]; // 2 = StartFlag, Command
        request[0] = REQUEST_START_FLAG;
        request[1] = command;
        if (payload != null) {
            request[2] = (byte) payload.length;
            System.arraycopy(payload, 0, request, 3, payload.length);
            byte checksum = 0;
            for (int i = 0; i < request.length - 2; i++) {
                checksum = (byte) ((checksum ^ (request[i]) & 0xFF));
            }
            request[payload.length - 1] = checksum;
        }
        out.write(request);
    }

    public final class Response {
        public static final byte SEND_MODE_SINGLE_RESPONSE = 0x0;
        public static final byte SEND_MODE_MULTIPLE_RESPONSE = 0x1;

        public static final byte DATA_TYPE_DEVHEALTH = 0x6;
        public static final byte DATA_TYPE_DEVINFO = 0x4;
        public static final byte DATA_TYPE_MEASUREMENT = (byte) 0x81;
        public static final byte DATA_TYPE_ACC_BOARD_FLAG = (byte) 0xff;

        final byte sendMode;
        final byte dataType;
        final byte[] payload;

        public Response(byte sendMode, byte dataType, byte[] payload) {
            this.sendMode = sendMode;
            this.dataType = dataType;
            this.payload = payload;

        }

    }

    public Response readResponse(int expectedDataResponseLength, byte expectedSendMode, byte expectedDataType)
            throws IOException {
        // 7 bytes =
        // 1 byte startFlag1
        // 1 byte StartFlag2
        // 4 bytes (30 bits dataReponseLength + 2 bits of sendMode)
        // 1 byte dataType

        int responseLength = 7 + expectedDataResponseLength;
        byte[] response = new byte[responseLength];

        long startTimeMills = System.currentTimeMillis();
        final long READ_RESPONSE_TIMEOUT_MILLS = 5000;

        int currentByte = 0;
        while (true) {

            if ((System.currentTimeMillis() - startTimeMills) > READ_RESPONSE_TIMEOUT_MILLS) {
                throw new IOException("Timed out in reading response.");
            }
            System.out.println("Reading byte " + currentByte + " ");

            int data = in.read();
            if (data == -1) {
                // throw new IOException("Unexpected end of file while reading response.");
                System.out.println("Waiting for bytes");
                continue;
            }
            System.out.println(byteToHexString(data));
            if ((currentByte == 0) && (data != RESPONSE_START_FLAG1)) {
                System.out.println("Skipping garbage data.");
                continue;
            }

            response[currentByte] = (byte) data;
            currentByte++;
            if (response.length == responseLength) {
                break;
            }

        }

        if (response[0] != RESPONSE_START_FLAG1) {
            throw new IOException("Expected RESPONSE_START_FLAG1, recieved " + byteToHexString(response[0]));
        }
        if (response[1] != RESPONSE_START_FLAG2) {
            throw new IOException("Expected RESPONSE_START_FLAG2, recieved " + byteToHexString(response[1]));
        }

        int byte3 = (((response[5] & (1 << 6) - 1)) << 24); // Drop two sendmode bits
        int byte2 = ((response[4]) << 16);
        int byte1 = ((response[3]) << 8);
        int byte0 = ((response[2]));
        int recievedDataRepsonseLength = byte3 | byte2 | byte1 | byte0;
        if (recievedDataRepsonseLength != expectedDataResponseLength) {
            throw new IOException("Bad DataResponseLength. Expected = " + expectedDataResponseLength + " Recieved = "
                    + recievedDataRepsonseLength);
        }

        byte sendMode = (byte) (response[5] >> 6); // sendMode bits
        byte dataType = response[6];

        if (sendMode != expectedSendMode) {
            throw new IOException("sendmode = " + sendMode + " did not equal expectedSendMode = " + expectedSendMode);
        }
        if (dataType != expectedDataType) {
            throw new IOException("dataType = " + dataType + " did not equal expectedDataType = " + expectedDataType);
        }

        byte[] payload = new byte[expectedDataResponseLength];
        System.arraycopy(response, 7, payload, 0, expectedDataResponseLength);

        return new Response(sendMode, dataType, payload);
    }

    public final static class HealthStatus {
        final int status;
        final int error_code;
        public static final byte STATUS_GOOD = 0;
        public static final byte STATUS_WARNING = 1;
        public static final byte STATUS_ERROR = 2;

        HealthStatus(int status, int error_code) {
            this.status = status;
            this.error_code = error_code;

        }

    }

    public HealthStatus getHealthStatus() throws IOException {

        sendRequest(CMD_GET_HEALTH, null);
        Response response = readResponse(3, Response.SEND_MODE_SINGLE_RESPONSE, Response.DATA_TYPE_DEVHEALTH);

        byte status = response.payload[0];
        int error_code = (response.payload[1] << 8) | response.payload[0];
        return new HealthStatus(status, error_code);
    }

    public void stop() throws IOException {
        sendRequest(CMD_STOP, null);
    }

    public boolean checkMotorControlSupport() throws IOException {
        sendRequest(CMD_ACC_BOARD_FLAG, new byte[] { 0x0, 0x0, 0x0, 0x0 });
        readResponse(4, Response.SEND_MODE_SINGLE_RESPONSE, Response.DATA_TYPE_ACC_BOARD_FLAG);
        return false;
    }

    public void reset() throws IOException {
        sendRequest(CMD_RESET, null);
        try {
            Thread.sleep(10); // Documentation reccomends 2 millisecond wait
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public final static class Scan {
        public double theta;
        public int distance;
        public int quality;

        Scan(double theta, int distance, int quality) {
            this.theta = theta;
            this.distance = distance;
            this.quality = quality;
        }
    }

    public void scan() throws IOException {
        sendRequest(CMD_SCAN, null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Response response = readResponse(5, Response.SEND_MODE_MULTIPLE_RESPONSE, Response.DATA_TYPE_MEASUREMENT);

        // return new Scan(theta, distance, quality);

    }

    public void readData() {

        System.out.println("Lidar.readData");
    }

    public void close() throws IOException {
        in.close();
        out.close();
    }

    public static String byteToHexString(int b) {
        return Integer.toHexString(b & 0xFF);
    }
}