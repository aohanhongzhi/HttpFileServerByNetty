package com.battcn.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Levin
 * @create 2017/8/29 0029
 */
public class TimeServerHandler implements Runnable {
    private Socket socket;

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new PrintWriter(this.socket.getOutputStream(), true);
            while (true) {
                String body = reader.readLine();
                if (body == null) break;
                System.out.println("The time server receive order : " + body);
                writer.println("我已经接收到你的请求了....");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.socket != null) socket.close();
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    TimeServerHandler(Socket socket) {
        this.socket = socket;
    }
}
