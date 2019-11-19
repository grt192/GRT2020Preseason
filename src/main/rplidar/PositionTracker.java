
import java.util.ArrayList;

import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;

public class PositionTracker implements Runnable {

    private Position currPos;
    private ArrayList<LidarData> prevLidarData;
    private ArrayList<LidarData> currLidarData;
    private ArrayList<Position> LPositions;
    private boolean firstTime = false;

    public PositionTracker(Position startingPos) {
        currPos = startingPos;
    }

    // Read txt file and replace currLidarData
    // Updates LPositions;
    public void readData() {
        ArrayList<LidarData> newLidarData = new ArrayList<LidarData>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("data.txt"));
            String currLine;
            while ((currLine = br.readLine()) != null) {
                String[] point = currLine.split(" ");
                if (point.length > 8) {
                    newLidarData.add(new LidarData(Double.valueOf(point[4]), Double.parseDouble(point[6]),
                            Double.valueOf(point[8])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        currLidarData = newLidarData;
    }

    /*
     * When calculating avg difference If box center is to the right of currPos,
     * positions with angles between 315 - 45 and 135 - 225 (up and down) need to
     * shift to the left If box center is to the above currPos, positions with
     * angles 45 - 135 and 225 - 315 (left and right) need to shift down the other
     * points can stay where they are during the comparison
     */
    public void updatePosition(double boxLength) {
        if (!firstTime) {
            readData();
            firstTime = true;
        }

        prevLidarData = currLidarData;
        readData();

        LPositions = createPositions(currPos, prevLidarData);

        //Multiple iterations
        //Change the currPos and make boxlength smaller everty iteration
        int iterations = 2;
        while(iterations > 0){
            Position newPos = null;
            double minDifference = 1000000;
            for (double x = currPos.getX() - boxLength / 3; x <= currPos.getX() + boxLength / 2; x += boxLength / 3) {
                for (double y = currPos.getY() - boxLength / 3; y <= currPos.getY() + boxLength / 2; y += boxLength / 3) {
                    Position boxCenter = new Position(x, y);
                    double difference = avgDifference(boxCenter);
                    if (difference < minDifference) {
                        minDifference = difference;
                        newPos = boxCenter;
                    }
                }
            }
            if (newPos != null) {
                currPos = newPos;
            }
            iterations++;
            boxLength /= 3;
        }
    }

    // Uses boxCenter and current lidar data to get positions
    // If those positions relatively match the data gathered from prev lidar data
    // and prev position -> it's probably where the robot moved
    public double avgDifference(Position boxCenter) {
        // L Positions are positons gotten using lidar data and a point
        ArrayList<Position> guessedLPositions = createPositions(boxCenter, currLidarData);
        int compareCount = 0;
        double totalDistance = 0;
        // Index of the two arraylists
        int i = 0;
        int j = 0;
        // Assume both arraylists are sorted by theta value
        while (i < LPositions.size() && j < guessedLPositions.size()) {
            Position posBefore = LPositions.get(i);
            Position posAfter = guessedLPositions.get(j);
            // Shift guessedPositions by the vector to the prev position on certain angles
            // Assume (0,0) is bottom left, need to be changed if otherwise
            if (currPos.getX() != boxCenter.getX()) {
                if (posAfter.getTheta() < 45 || posAfter.getTheta() > 135 && posAfter.getTheta() < 225
                        || posAfter.getTheta() > 315) {
                    // posAfter.addVector(boxCenter.getX() - currPos.getX(), 0);
                }
            }
            if (currPos.getY() != boxCenter.getY()) {
                if (posAfter.getTheta() > 45 && posAfter.getTheta() < 135
                        || posAfter.getTheta() > 225 && posAfter.getTheta() < 315) {
                    // posAfter.addVector(0, boxCenter.getY() - currPos.getY());
                }
            }

            if (Math.abs(posBefore.getTheta() - posAfter.getTheta()) <= 1) {
                totalDistance += posBefore.distance(posAfter);
                compareCount++;
                i++;
                j++;
            } else if (posBefore.getTheta() < posAfter.getTheta()) {
                i++;
            } else {
                j++;
            }
        }
        return totalDistance / compareCount;
    }

    // Uses a point and lidar data to create an arraylist of positions
    // Removes all values of Q
    public static ArrayList<Position> createPositions(Position point, ArrayList<LidarData> lidarData) {
        ArrayList<Position> result = new ArrayList<Position>();
        for (LidarData lidar : lidarData) {
            if (lidar.getQuality() != 0) {
                result.add(new Position(lidar.getDistance(), lidar.getTheta(), point.getX(), point.getY()));
            }
        }
        return result;
    }

    @Override
    public void run() {

        String fileLocation = "/home/pi/Documents/GRTLidar/";
        System.out.println("\nPosition Tracking run");
        File fileDonePos = new File(fileLocation + "pos.txt");
        while (true) {

            File fileTempPos = new File(fileDonePos.getPath() + ".temp");
            updatePosition(150);
            try (BufferedWriter out = new BufferedWriter(new FileWriter(fileTempPos))) {
                String pos = currPos.toString();
                // System.out.println(pos);
                out.write(pos);

                if (!fileTempPos.renameTo(fileDonePos)) {
                    System.out.println("File failed to rename " + fileTempPos + " to " + fileDonePos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}