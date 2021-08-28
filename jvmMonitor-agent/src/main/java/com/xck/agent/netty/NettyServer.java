package com.xck.agent.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * netty服务端
 *
 * @author xuchengkun
 * @date 2021/08/25 11:05
 **/
public class NettyServer extends Thread {

    private ChannelFuture channelFuture;

    @Override
    public void run() {
        System.out.println("寻找可用端口...");



        System.out.println("启动服务端");
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            socketChannel.pipeline().addLast("httpAggregator", new HttpObjectAggregator(512 * 1024));
                            socketChannel.pipeline().addLast(new HttpServerHandler());
                        }
                    });

            channelFuture = serverBootstrap.bind(8666)
                    .addListener(new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            if (future.isSuccess()) {
                                System.out.println("启动服务端成功");
                            }
                        }
                    }).sync();

            channelFuture.channel().closeFuture().sync();
            System.out.println("关闭服务端成功");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }
}
