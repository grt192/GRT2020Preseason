set host=pi@raspberrypi
set host=pi@10.1.92.50

scp *.java *.cpp run.bat RPLidarDriver.h compile.sh %host%:/home/pi/Documents/GRTLidar/ 
if %errorlevel% neq 0 exit /b %errorlevel%

rem If it promtps you for a password use keygen.

ssh %host% sh -xv /home/pi/Documents/GRTLidar/compile.sh
if %errorlevel% neq 0 exit /b %errorlevel%
