public class DeviceHealth {
    public static final byte STATUS_OK = 0;
    public static final byte STATUS_WARNING = 1;
    public static final byte STATUS_ERROR = 2;

    public byte status;
    public short errorCode;

    public boolean isOk() {
        return status == STATUS_OK;
    }

}