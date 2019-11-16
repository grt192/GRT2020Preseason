set host=pi@10.1.92.50
set host=pi@raspberrypi

javac RPLidar.java
if %errorlevel% neq 0 exit /b %errorlevel%

C:\Users\Public\frc2019\roborio\bin\arm-frc2019-linux-gnueabi-g++.exe -IC:\Users\Public\frc2019\jdk\include -IC:\Users\rmc.CARLSTROM\Documents\rplidar_sdk\sdk\sdk\include -IC:\Users\rmc.CARLSTROM\Documents\rplidar_sdk\sdk\sdk\src -I. -fPIC rplidar.cpp librplidar_sdk.a -lpthread -shared -o librplidar.so
if %errorlevel% neq 0 exit /b %errorlevel%

scp RPLidar.class RPLidarDriver.class DeviceInfo.class DeviceHealth.class Measurement.class librplidar.so data.txt run.sh %host%:/home/pi/Documents/GRTLidar/ 
if %errorlevel% neq 0 exit /b %errorlevel%

rem If it prompts you for a password use keygen.

ssh %host% sh -xv /home/pi/Documents/GRTLidar/run.sh
if %errorlevel% neq 0 exit /b %errorlevel%
