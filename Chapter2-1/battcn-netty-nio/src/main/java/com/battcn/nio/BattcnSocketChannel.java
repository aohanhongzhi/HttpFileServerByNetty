package com.battcn.bio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Levin
 * @date 2017-08-23.
 */
public class BattcnSocketChannel {

    public void statrClient(){
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost",8999));//连接到localhost:8999
            //非阻塞模式,如果服务端一下未响应,socketChannel.read(response),会直接读取到空,设置为True会等待服务端响应完后在执行读取操作
            socketChannel.configureBlocking(true);
            String  request = "Hello Socket .......";
            //创建新的Buffer
            ByteBuffer byteBuffer = ByteBuffer.wrap(request.getBytes("UTF-8"));
            socketChannel.write(byteBuffer);//将Buffer写入
            ByteBuffer response = ByteBuffer.allocate(48);
            int read = socketChannel.read(response);//写完后从通道读取
            while (read > 0){
                response.flip();//转换通道
                Charset charset = Charset.forName("UTF-8");//设置字符编码,否则乱码
                System.out.println(charset.newDecoder().decode(response));
                response.clear();
                read = socketChannel.read(response);
            }
            response.clear();//清空buffer,减少内存开销
            socketChannel.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BattcnSocketChannel().statrClient();
    }

}
