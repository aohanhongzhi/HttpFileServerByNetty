package com.battcn.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;

/**
 * @author Levin
 * @date 2017-08-23.
 */
public class BattcnServerSocketChannel {

    public void startServer(){
        System.out.println("start server......");
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(8999));//绑定端口
            serverSocketChannel.configureBlocking(false);
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel != null) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(48);
                    int len = socketChannel.read(byteBuffer);
                    while (len > 0){
                        byteBuffer.flip();//转换通道
                        Charset charset = Charset.forName("UTF-8");//设置字符编码,否则乱码
                        System.out.println(charset.newDecoder().decode(byteBuffer));
                        len = socketChannel.read(byteBuffer);
                    }
                    byteBuffer.clear();//清空buffer,减少内存开销
                    ByteBuffer response = ByteBuffer.wrap("Hello 我已经接受到你的请求了".getBytes("UTF-8"));
                    socketChannel.write(response);
                    response.clear();
                    socketChannel.close();//读取完毕后关闭socket通道
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startSelectServer(){
        System.out.println("start server......");
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(8999));//绑定端口
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();//打开连接
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                int select = selector.select();
                if(select > 0){
                    for (SelectionKey key : selector.selectedKeys()){
                        if(key.isAcceptable()) {//可接收的
                            SocketChannel channel = ((ServerSocketChannel)key.channel()).accept();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(48);
                            int len = channel.read(byteBuffer);
                            while (len > 0){
                                byteBuffer.flip();//转换通道,将写转换成读
                                System.out.println( Charset.forName("UTF-8").newDecoder().decode(byteBuffer));
                                len = channel.read(byteBuffer);
                            }
                            byteBuffer.clear();//清空buffer,减少内存开销
                            ByteBuffer response = ByteBuffer.wrap("Hello Selector 我已经接受到你的请求了".getBytes("UTF-8"));
                            channel.write(response);
                            response.clear();
                            channel.close();//读取完毕后关闭socket通道
                            selector.selectedKeys().remove(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BattcnServerSocketChannel().startSelectServer();
    }

}
