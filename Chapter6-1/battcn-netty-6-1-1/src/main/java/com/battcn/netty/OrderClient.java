package com.battcn.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author Levin
 * @date 2017-09-10.
 */
public class OrderClient {

    public static void connect(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future;
        try {
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingResolver(this.getClass().getClassLoader())));
                            channel.pipeline().addLast(new ObjectEncoder());
                            channel.pipeline().addLast(new OrderClientHandler());
                        }
                    });
            //发起异步请求
            future = bootstrap.connect(host, port).sync();
            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static class OrderClientHandler extends ChannelHandlerAdapter {

        public OrderClientHandler() {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 1; i <= 3; i++) {
                ctx.write(request(i));
            }
            ctx.flush();
        }

        private Object request(int i) {
            OrderRequest request = new OrderRequest();
            request.setAddress("上海市青浦区赵重公路1888号");
            request.setOrderId(i);
            request.setPhoneNumber("130XXXX1912");
            request.setProductName("一起来学Netty");
            request.setUserName("Levin");
            return request;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Receive Server Response :[" + msg + "]");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("释放资源:" + cause.getMessage());//不重写将会看到堆栈信息以及资源无法关闭
            ctx.close();
        }
    }

    public static void main(String[] args) {
        OrderClient.connect("127.0.0.1", 4040);
    }


}
