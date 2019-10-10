/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Add your docs here.
 */
public class FieldGUIServer extends Thread {

    private ServerSocket server;
    private Socket client;
    private InputStream input;
    private Scanner scanner;
    private PrintStream output;

    public FieldGUIServer() {
        try {
            server = new ServerSocket(5800);
            System.out.println("Server running on " + server.getInetAddress() + ":" + server.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED TO CREATE NETWORK");
        }
    }

    @Override
    public void run() {
        try {
            client = server.accept();
            input = client.getInputStream();
            scanner = new Scanner(input);
            output = new PrintStream(client.getOutputStream());
            System.out.println("Successfully connected");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to connect. Try again.");
        }
    }

    public String readLine() {
        try {
            if (scanner == null || input.available() == 0)
                return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return scanner.nextLine();
    }

    public void sendData(String data) {
        if (output != null) {
            output.println(data);
        }
    }

}
