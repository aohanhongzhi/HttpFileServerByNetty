package com.battcn.netty;

import com.battcn.netty.proto.OrderProto;
import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

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
                            channel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            channel.pipeline().addLast(new ProtobufDecoder(OrderProto.OrderResponse.getDefaultInstance()));
                            channel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            channel.pipeline().addLast(new ProtobufEncoder());
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
                OrderProto.OrderRequest.Builder request = OrderProto.OrderRequest.newBuilder();
                request.setAddress("China");
                request.setOrderId(i);
                request.setPhoneNumber("130****1912");
                request.setProductName("Netty Book");
                request.setUserName("Levin");
                ctx.write(request);
            }
            ctx.flush();
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
