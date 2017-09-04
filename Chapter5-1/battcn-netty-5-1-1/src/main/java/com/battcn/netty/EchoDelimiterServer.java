package com.battcn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * 基于Netty实现的TimeServer
 *
 * @author Levin
 * @create 2017/9/4
 */
public class EchoDelimiterServer {

    public static void bind(int port) {
        EventLoopGroup masterGroup = new NioEventLoopGroup();//线程组,含一组NIO线程,专门用来处理网络事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//NIO服务端启动辅助类
            bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer(EchoConstant.SEPARATOR.getBytes());
                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
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


    private static class EchoServerHandler extends ChannelHandlerAdapter {
        private int counter;
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String body = (String) msg;
            System.out.println("EchoDelimiterServer 接收到的消息 :" + body + "; 当前统计:" + ++counter);
            body = body + EchoConstant.SEPARATOR;
            ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
            ctx.writeAndFlush(echo);
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


    public static void main(String[] args) {
        EchoDelimiterServer.bind(EchoConstant.ECHO_DELIMITER_PORT);
    }

}
