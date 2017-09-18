package com.battcn.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

/**
 * @author Levin
 * @create 2017/9/18 0018
 */
public class ByteBuffDemo1 {

    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer(10);
        buf.writeBytes("鏖战八方QQ群391619659".getBytes());//扩容算法稍后讲解
        System.out.println(buf);
        System.out.println("//////////////////////////////////////////无耻的分割线//////////////////////////////////////////");
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put("鏖战八方QQ群391619659".getBytes());
        System.out.println(buffer);

    }


}
