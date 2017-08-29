package com.battcn.bio;


import java.io.IOException;
import java.net.ServerSocket;


/**
 * 初窥NIO-TimeServer：同步阻塞方式的I/O创建
 *
 * @author Levin
 * @create 2017/8/28 0028
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 4040;
        System.out.println("start server......" + port);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new TimeServerHandler(serverSocket.accept()).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
