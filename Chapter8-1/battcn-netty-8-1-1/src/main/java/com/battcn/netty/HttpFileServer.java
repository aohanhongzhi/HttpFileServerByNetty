/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.battcn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


public class HttpFileServer {
    private static final int DEFAULT_PORT = 4040;
    private static final String DEFAULT_IP = "127.0.0.1";
    //对应的映射目录
    private static final String DEFAULT_HOME = "F:/battcn-workspace/battcn-netty/Chapter8-1/battcn-netty-8-1-1/src/main/java/com";

    static void run(final int port, final String path) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel)
                                throws Exception {
                            channel.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            channel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(8 * 1024));
                            channel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            channel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            channel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(path));
                        }
                    });
            ChannelFuture future = b.bind(DEFAULT_IP, port).sync();
            System.out.println("HTTP文件目录服务器启动，网址是 : " + "http://" + DEFAULT_IP + ":" + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        HttpFileServer.run(DEFAULT_PORT, DEFAULT_HOME);
    }
}
