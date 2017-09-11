package com.battcn.netty.test;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;

/**
 * @author Levin
 * @create 2017/9/11 0011
 */
public class OrderTest {

    @Test
    public void test1() throws IOException {
        Order order = new Order(1, "Levin", "Netty Book", "130****1912", "China");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(order);
        os.flush();
        System.out.println("JDK序列化后的长度： " + out.toByteArray().length);
        os.close();
        out.close();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(order.getAddress().getBytes());
        buffer.put(order.getPhoneNumber().getBytes());
        buffer.put(order.getUserName().getBytes());
        buffer.put(order.getProductName().getBytes());
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        System.out.println("使用二进制序列化的长度：" + result.length);

    }

    @Test
    public void test2() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            long jdkTime = System.currentTimeMillis();
            IntStream.range(1, 10000).forEach(id -> {
                Order order = new Order(id, "Levin", "Netty Book", "130****1912", "China");
                try {
                    ObjectOutputStream os = new ObjectOutputStream(out);
                    os.writeObject(order);
                    os.flush();
                    out.toByteArray();
                    os.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            long endTime = System.currentTimeMillis();
            System.out.println("jdk序列化10000次耗时：" + (endTime - jdkTime));
            System.out.println("--------------------------------------------------------------------------------");
            long buffTime = System.currentTimeMillis();
            IntStream.range(1, 1000000).forEach(id -> {
                Order order = new Order(id, "Levin", "Netty Book", "130****1912", "China");
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put(order.getAddress().getBytes());
                buffer.put(order.getPhoneNumber().getBytes());
                buffer.put(order.getUserName().getBytes());
                buffer.put(order.getProductName().getBytes());
                buffer.flip();
                byte[] result = new byte[buffer.remaining()];
                buffer.get(result);
            });
            System.out.println("ByteBuffer序列化1000000次耗时：" + (System.currentTimeMillis() - buffTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


