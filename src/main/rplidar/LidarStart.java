
public class LidarStart {

    public static void main(String[] args) {
        Runnable gatherData = new RPLidar();
        Thread getData = new Thread(gatherData);
        getData.start();

        try {
            Thread.sleep(7000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runnable positionTracker = new PositionTracker(new Position(4114.8, 4114.8));
        Thread getPos = new Thread(positionTracker);
        getPos.start();

    }

}