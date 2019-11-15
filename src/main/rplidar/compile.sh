#!/bin/sh -xve
cd /home/pi/Documents/GRTLidar
javac RPLidar.java
g++ -fPIC rplidar.cpp -I./rplidar_sdk/sdk/sdk/include/ -I./rplidar_sdk/sdk/sdk/src/ -I/usr/lib/jvm/java-11-openjdk-armhf/include/ -I/usr/lib/jvm/java-11-openjdk-armhf/include/linux/ ./rplidar_sdk/sdk/output/Linux/Release/librplidar_sdk.a -lpthread -shared -o librplidar.so
java -classpath /home/pi/Documents/GRTLidar/ -Djava.library.path=/home/pi/Documents/GRTLidar/ RPLidar
