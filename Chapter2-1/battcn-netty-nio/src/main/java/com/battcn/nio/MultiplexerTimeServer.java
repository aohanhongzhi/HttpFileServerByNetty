package com.battcn.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Levin
 * @create 2017/8/30 0030
 */
public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();//打开多路复用器
            serverSocketChannel = ServerSocketChannel.open();//打开ServerSocket通道
            serverSocketChannel.configureBlocking(false);//设置异步非阻塞模式
            serverSocketChannel.bind(new InetSocketAddress(port), 1024);//绑定端口为4040并且初始化系统资源位1024个
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//将Channel管道注册到Selector中去,监听OP_ACCEPT操作
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);//如果初始化失败，退出
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!this.stop) {
            try {
                this.selector.select(1000);//1S唤醒一次
                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();//删掉处理过的key
                    try {
                        handleInput(selectionKey);
                    } catch (Exception e) {
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null)
                                selectionKey.channel().close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void handleInput(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isValid()) {//处理新接入的请求消息
            if (selectionKey.isAcceptable()) {
                ServerSocketChannel sc = (ServerSocketChannel) selectionKey.channel();
                SocketChannel accept = sc.accept();
                accept.configureBlocking(false);
                accept.register(this.selector, SelectionKey.OP_READ);
            }
            if (selectionKey.isReadable()) {//读取数据
                SocketChannel sc = (SocketChannel) selectionKey.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);//一次最多读取1024
                int read = sc.read(buffer);
                if (read > 0) {
                    buffer.flip();//模式切换,写切换成读
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String msg = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order :" + msg);
                    doWrite(sc, "hello client");
                } else if (read < 0) {
                    selectionKey.cancel();
                    sc.close();
                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String resp) throws IOException {
        if (resp != null && resp.trim().length() > 0) {
            byte[] bytes = resp.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            sc.write(buffer);
        }
    }
}
