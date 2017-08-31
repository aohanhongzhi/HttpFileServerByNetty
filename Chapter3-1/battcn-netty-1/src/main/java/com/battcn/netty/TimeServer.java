package com.battcn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * 基于Netty实现的TimeServer
 *
 * @author Levin
 * @create 2017/8/31 0031
 */
public class TimeServer {

    public static void bind(int port) {
        EventLoopGroup masterGroup = new NioEventLoopGroup();//线程组,含一组NIO线程,专门用来处理网络事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//NIO服务端启动辅助类
            bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功,
            System.out.println("绑定端口,同步等待成功......");
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
            System.out.println("等待服务端监听端口关闭......");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅退出释放线程池
            masterGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("优雅退出释放线程池......");
        }
    }

    private static class ChildChannelHandler extends ChannelInitializer {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast(new TimeServerHandler());
        }
        private static class TimeServerHandler extends ChannelHandlerAdapter {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf buf = (ByteBuf) msg;
                byte[] req = new byte[buf.readableBytes()];
                buf.readBytes(req);
                String body = new String(req, "UTF-8");
                System.out.println("TimeServer 接收到的消息 :" + body);
                ByteBuf resp = Unpooled.copiedBuffer("你在说什么呢...".getBytes());
                ctx.write(resp);
            }
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                //将消息队列中信息写入到SocketChannel中去,解决了频繁唤醒Selector所带来不必要的性能开销
                //Netty的 write 只是将消息放入缓冲数组,再通过调用 flush 才会把缓冲区的数据写入到 SocketChannel
                ctx.flush();
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.close();//发生异常时候，执行重写后的 exceptionCaught 进行资源关闭
            }
        }
    }

    public static void main(String[] args) {
        TimeServer.bind(4040);
    }
}
