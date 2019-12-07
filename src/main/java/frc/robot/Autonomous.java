/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import edu.wpi.first.wpilibj.Filesystem;
import frc.sequence.Sequence;

public class Autonomous {

    private Robot robot;
    private Queue<String> lines;
    private boolean done;

    private boolean finishedFlag;
    private long delayTime;

    public Autonomous(Robot robot) {
        this.robot = robot;
        done = true;
    }

    public void init(String filename) {
        lines = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(new File(Filesystem.getDeployDirectory(), "auton/" + filename)));
            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("#"))
                    lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        done = false;
        finishedFlag = true;
        delayTime = 0;
    }

    public void loop() {
        long time = System.currentTimeMillis();
        while (true) {
            if (done || !finishedFlag || time < delayTime)
                return;
            String line = lines.poll();
            if (line == null) {
                done = true;
                return;
            }
            System.out.println(line);
            String[] cmd = line.trim().split(" ");
            switch (cmd[0]) {
            case "delay":
                delayTime = time + Integer.parseInt(cmd[1]);
                break;
            case "wait":
                finishedFlag = false;
                break;
            case "setspeed":
                double speed = Double.parseDouble(cmd[1]);
                System.out.println("set speed to" + speed);
                Robot.TANK.setRaw(speed, speed);
                break;
            case "outtakein": 
                Sequence.OUTTAKE_IN.start();
                break;
            case "outtakeout": 
                Sequence.OUTTAKE_OUT.start();
            case "elevatorup":
                System.out.println("elevator moving up");
                Sequence.ELEVATOR_UP.start();
                break;
            case "elevatordown":
                System.out.println("elevator moving down");
                Sequence.ELEVATOR_DOWN.start();
                break;
            }
        }
    }

    

    public void modeFinished() {
        finishedFlag = true;
    }

    public void kill() {
        done = true;
    }
}
