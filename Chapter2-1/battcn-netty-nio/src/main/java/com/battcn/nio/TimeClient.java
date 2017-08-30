package com.battcn.nio;

/**
 * 基于NIO的TimeServer实现
 *
 * @author Levin
 * @create 2017/8/30 0030
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 4040;
        new Thread(new TimeClientHandler("127.0.0.1",port),"NIO-MultiplexerTimeServer-1").start();
    }
}
