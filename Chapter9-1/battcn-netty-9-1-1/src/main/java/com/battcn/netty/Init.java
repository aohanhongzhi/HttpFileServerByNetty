package com.battcn.netty;

/**
 * @author Levin
 * @create 2017/9/16 0016
 */
public interface Init {
    int PORT = 5050;
    String HOST = "localhost";
    String WEB_SOCKET_URL = String.format("ws://%s:%d/websocket", HOST, PORT);
}
