public class Measurement {
    private short angle_z_q14;
    private int dist_mm_q2;
    private byte quality;
    private byte flag;

    public boolean isSyncBit() {
        return (flag & 0x1) == 0x1;
    }

    public float getAngle() {
        return ((angle_z_q14 & 0xFFFF) * 90.0f) / (1 << 14);
    }

    public float getDistance() {
        return dist_mm_q2 / (4.0f);
    }

    public int getQuality() {
        return (quality & 0xFF);
    }
}