public class DeviceInfo {

    public byte model = 0;
    public short firmwareVersion = 0;
    public byte hardwareVersion = 0;
    public final byte[] serialnum = new byte[16];

    public String getFirmwareString() {
        return String.format("%d.%02d", (firmwareVersion >> 8), (firmwareVersion & 0xFF));
    }

    public String getSerialnumString() {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < serialnum.length; i++) {
            b.append(String.format("%02x", serialnum[i]));
        }
        return b.toString();
    }
}