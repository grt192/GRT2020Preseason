class LidarData{

    private double theta;
    private double distance;
    private double quality;

    public LidarData(double theta, double distance, double quality){
        this.theta = theta;
        this.distance = distance;
        this.quality = quality;
    }

    public double getTheta(){
        return theta;
    }

    public double getDistance(){
        return distance;
    }

    public double getQuality(){
        return quality;
    }

    public String toString(){
        return "theta: " + theta + " distance: " + distance + " quality: " + quality;
    }

}