package com.battcn.nio;

import org.junit.Test;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * @author Levin
 * @date 2017-08-22.
 */

public class ByteBufferDemo {

    static final String RW = "rw";

    @Test
    public void test1(){
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("battcn.log",RW);
            FileChannel channel = randomAccessFile.getChannel();//获取文件管道
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);//每次读取的大小
            int read = channel.read(byteBuffer);
            while (read > 0) {
                byteBuffer.flip();//模式转换，从写转变成读
                Charset charset = Charset.forName("UTF-8");
                System.out.println(charset.newDecoder().decode(byteBuffer).toString());
                byteBuffer.clear();
                read = channel.read(byteBuffer);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }


}
