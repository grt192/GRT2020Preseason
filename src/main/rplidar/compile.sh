#!/bin/sh -xve
cd /home/pi/Documents/GRTLidar
javac LidarStart.java
#javac DeviceHealth.java
#javac DeviceInfo.java
#javac LidarData.java
#javac Measurement.java
#javac Position.java
#javac PositionTracker.java
#javac RPLidar.java
#javac RPLidarDriver.java
g++ -fPIC rplidar.cpp -I./rplidar_sdk/sdk/sdk/include/ -I./rplidar_sdk/sdk/sdk/src/ -I/usr/lib/jvm/java-11-openjdk-armhf/include/ -I/usr/lib/jvm/java-11-openjdk-armhf/include/linux/ ./rplidar_sdk/sdk/output/Linux/Release/librplidar_sdk.a -lpthread -shared -o librplidar.so
pkill java
java -classpath /home/pi/Documents/GRTLidar/ -Djava.library.path=/home/pi/Documents/GRTLidar/ LidarStart
