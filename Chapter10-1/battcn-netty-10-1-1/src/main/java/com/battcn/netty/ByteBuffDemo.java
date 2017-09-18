package com.battcn.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * @author Levin
 * @create 2017/9/18 0018
 */
public class ByteBuffDemo {

    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer(10);
        buf.writeBytes("鏖战八方QQ群391619659".getBytes());//扩容算法稍后讲解
        System.out.println("Netty：" + buf);
        byte[] by = new byte[buf.readableBytes()];
        buf.readBytes(by);
        System.out.println("Netty：" + new String(by));

        System.out.println("//////////////////////////////////////////无耻的分割线//////////////////////////////////////////");
        ByteBuffer bf1 = ByteBuffer.allocate(100);
        bf1.put("鏖战八方QQ群391619659".getBytes());
        System.out.println("JDK："+bf1);
        System.out.println("当前指针：" + bf1.position());
        byte[] by1 = new byte[bf1.remaining()];
        System.out.println(by1.length);//What's 居然是74
        bf1.get(by1);
        System.out.println("未使用flip："+new String(by1));//居然是空的
        System.out.println("//////////////////////////////////////////无耻的分割线//////////////////////////////////////////");
        ByteBuffer bf2 = ByteBuffer.allocate(100);
        bf2.put("鏖战八方QQ群391619659".getBytes());
        System.out.println("JDK："+bf2);
        System.out.println("当前指针：" + bf2.position());
        bf2.flip();
        byte[]  by2 = new byte[bf2.remaining()];
        System.out.println(by2.length);//是26了
        bf2.get(by2);
        System.out.println("使用flip："+new String(by2));//拿到了
    }


}
