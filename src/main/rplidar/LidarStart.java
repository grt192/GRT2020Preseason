public class LidarStart {

    public static void main(String[] args) {
        Runnable gatherData = new RPLidar();
        Thread getData = new Thread(gatherData);
        getData.setDaemon(true);
        getData.start();

        Runnable positionTracker = new PositionTracker(new Position(0, 0));
        Thread getPos = new Thread(positionTracker);
        getPos.setDaemon(true);
        getPos.start();

    }

}