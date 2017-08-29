package com.battcn.bio;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 初窥NIO-TimeServer：同步阻塞方式的I/O创建
 *
 * @author Levin
 * @create 2017/8/28 0028
 */
public class TimeServerPool {

    public static void main(String[] args) {
        int port = 4041;
        System.out.println("start server......" + port);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            TimeServerHandlerExecutePool executePool = new TimeServerHandlerExecutePool(50, 100);//创建I/O任务线程池
            executePool.execute(new TimeServerHandler(socket));
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
