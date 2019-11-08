public class RPLidarDriver implements AutoCloseable {

    private long drv;

    private RPLidarDriver() {
    }

    public static RPLidarDriver create() {

        RPLidarDriver rpLidarDriver = new RPLidarDriver();
        if (!nativeCreateDriver(rpLidarDriver)) {
            throw new IllegalStateException("Failed to create RPLidarDriver");
        }

        return rpLidarDriver;
    }

    public static final native boolean nativeCreateDriver(RPLidarDriver rpLidarDriver);

    public void connect(String port, int baud) {
        if (!nativeConnect(port, baud)) {
            throw new IllegalStateException("Failed to connect");
        }
    }

    public native boolean nativeConnect(String port, int baud);

    public DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        if (!nativeGetDeviceInfo(deviceInfo)) {
            throw new IllegalStateException("Failed to get Device Info.");
        }
        return deviceInfo;
    }

    private native boolean nativeGetDeviceInfo(DeviceInfo deviceInfo);

    public DeviceHealth getDeviceHealth() {
        DeviceHealth deviceHealth = new DeviceHealth();
        if (!nativeGetDeviceHealth(deviceHealth)) {
            throw new IllegalStateException("Failed to get Health");
        }
        return deviceHealth;
    }

    private native boolean nativeGetDeviceHealth(DeviceHealth deviceHealth);

    public void startMotor() {
        if (!nativeStartMotor()) {
            throw new IllegalStateException("Failed to start Motor");
        }
    }

    private native boolean nativeStartMotor();

    public void stopMotor() {
        if (!nativeStopMotor()) {
            throw new IllegalStateException("Failed to stop Motor");
        }
    }

    private native boolean nativeStopMotor();

    public void reset() {
        if (!nativeReset()) {
            throw new IllegalStateException("Failed to reset");
        }
    }

    private native boolean nativeReset();

    public void startScan(boolean force, boolean useTypicalScan) {
        if (!nativeStartScan(force, useTypicalScan)) {
            throw new IllegalStateException("Failed to star scanning");
        }
    }

    private native boolean nativeStartScan(boolean force, boolean useTypicalScan);

    public void grabScanDataHq(Measurement[] measurements) {
        if (!nativeGrabScanDataHq(measurements)) {
            throw new IllegalStateException("Failed to grab scan data");
        }
    }

    private native boolean nativeGrabScanDataHq(Measurement[] measurements);

    public void stop() {
        if (!nativeStop()) {
            throw new IllegalStateException("Failed to stop");
        }
    }

    private native boolean nativeStop();

    @Override
    public native void close();

    @Override
    protected void finalize() {
        close();
    }

    static {
        System.loadLibrary("rplidar");
    }
}