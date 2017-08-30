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

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();//打开多路复用器
            serverSocketChannel = ServerSocketChannel.open();//打开ServerSocket通道
            serverSocketChannel.configureBlocking(false);//设置异步非阻塞模式,与Selector使用 Channel 必须处于非阻塞模式
            serverSocketChannel.bind(new InetSocketAddress(port), 1024);//绑定端口为4040并且初始化系统资源位1024个
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//将Channel管道注册到Selector中去,监听OP_ACCEPT操作
            System.out.println("TimeServer启动成功,当前监听的端口 : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);//如果初始化失败，退出
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                //int select = this.selector.select(1000); 1S唤醒一次,加休眠时间则可以不要if(select > 0 )
                if (!selector.isOpen()) {
                    System.out.println("selector is closed");
                    break;
                }
                int select = selector.select();
                if (select > 0) {
                    Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();//删掉处理过的key
                        try {
                            handleInput(key);
                        } catch (Exception e) {
                            if (key != null) {
                                key.cancel();
                                if (key.channel() != null)
                                    key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {//处理新接入的请求消息
            if (key.isAcceptable()) {
                SocketChannel accept = ((ServerSocketChannel) key.channel()).accept();
                accept.configureBlocking(false);
                //添加新的连接到selector中
                accept.register(this.selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {//读取数据
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);//一次最多读取1024
                int read = sc.read(buffer);
                if (read > 0) {
                    buffer.flip();//反转缓冲区
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String msg = new String(bytes, "UTF-8");
                    System.out.println("TimeServer 接收到的消息 :" + msg);
                    doWrite(sc, "挽歌君老帅了...");
                } else if (read < 0) {
                    key.cancel();
                    sc.close();
                } else {
                    //读取0个字节忽略
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String resp) throws IOException {
        if (resp != null && resp.trim().length() > 0) {
            byte[] bytes = resp.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);//根据字节大小创建一个Buffer
            writeBuffer.put(bytes);//将字节数组复制到缓冲区
            writeBuffer.flip();//反转缓冲区
            channel.write(writeBuffer);//调用管道API将数据写出
        }
    }
}
