package com.battcn.netty;

import com.battcn.netty.proto.OrderProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;


/**
 * @author Levin
 * @date 2017-09-10.
 */
public class OrderServer {

    public static void bind(int port) {
        EventLoopGroup masterGroup = new NioEventLoopGroup();//线程组,含一组NIO线程,专门用来处理网络事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//NIO服务端启动辅助类
            bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            channel.pipeline().addLast(new ProtobufDecoder(OrderProto.OrderRequest.getDefaultInstance()));
                            channel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            channel.pipeline().addLast(new ProtobufEncoder());
                            channel.pipeline().addLast(new OrderServerHandler());
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


    private static class OrderServerHandler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            OrderProto.OrderRequest request = (OrderProto.OrderRequest) msg;
            System.out.println("Service Accept Client Order Request :[" + request + "]");
            OrderProto.OrderResponse.Builder builder = OrderProto.OrderResponse.newBuilder();
            builder.setOrderId(request.getOrderId());
            builder.setRespCode("200");
            builder.setDesc("Order Submit Successfully");
            ctx.writeAndFlush(builder);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();//发生异常时候，执行重写后的 exceptionCaught 进行资源关闭
        }
    }

    public static void main(String[] args) {
        OrderServer.bind(4040);
    }


}
