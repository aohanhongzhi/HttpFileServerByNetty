package com.battcn.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Levin
 * @date 2017-08-30.
 */
public class TimeClientHandler implements Runnable {

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;


    public TimeClientHandler(String host, int port) {
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;
        try {
            selector = Selector.open();//打开连接
            socketChannel = SocketChannel.open();//打开连接
            //TODO 该处存在一个小BUG,第一条数据会丢失
            //TODO 如果该处设为true阻塞,则客户端会报错,应该是本人写法还存在点问题
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);//异常情况断开连接
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);//异常情况断开连接
        }
        while (!stop) {
            try {
                if (selector.select() > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        try {
                            handleInput(selectionKey);
                        } catch (Exception e) {
                            if (selectionKey != null) {
                                selectionKey.cancel();
                                if (selectionKey.channel() != null) {
                                    selectionKey.channel().close();
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.exit(1);
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {//验证这个key是否有效
            SocketChannel channel = (SocketChannel) key.channel();
            if (key.isConnectable()) {//判断SocketChannel是否处于连接状态
                if (channel.finishConnect()) {//判断 SocketChannel 是否连接成功
                    channel.register(this.selector, SelectionKey.OP_READ);//连接成功则注册OP_READ事件到Selector选择器中
                    doWrite(channel);
                } else {
                    System.exit(1);
                }
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);//创建读取所需Buffer
                int read = channel.read(readBuffer);
                if (read > 0) {
                    readBuffer.flip();//反转缓冲区
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String msg = new String(bytes, "UTF-8");
                    System.out.println("TimeClient 接收到的消息:" + msg);
                }
                this.stop = true;//如果接收完毕退出循环
            }
        }
    }

    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(this.selector, SelectionKey.OP_CONNECT);//向Reactor线程的Selector注册OP_CONNECT事件
        }
    }

    private void doWrite(SocketChannel channel) throws IOException {
        byte[] req = "挽歌君帅不帅".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(req.length);
        buffer.put(req);//将字节数组复制到缓冲区
        buffer.flip();//反转缓冲区
        channel.write(buffer);
        if (!buffer.hasRemaining()) {
            System.out.println("消息发送成功");
        }
    }
}
