package com.xck.agent.netty;

import com.xck.util.LogUtil;
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

    private volatile boolean isRunning;

    private int port;

    public NettyServer() {
        this.port = 8666;
    }

    public NettyServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        LogUtil.info("开始启动服务端");
        isRunning = true;
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
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
                            socketChannel.pipeline().addLast(new HttpServerHandler());
                        }
                    });

            channelFuture = serverBootstrap.bind(port)
                    .addListener(new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            if (future.isSuccess()) {
                                LogUtil.info("启动服务端成功, 端口: " + port);
                            }
                        }
                    }).sync();

            channelFuture.channel().closeFuture().sync();
            LogUtil.info("关闭服务端成功, 端口: " + port);
        } catch (Throwable e) {
            LogUtil.error("服务端异常, 端口: " + port + ", errMsg: " + e);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void shutdown() throws InterruptedException{
        LogUtil.info("准备关闭服务端，服务端运行状态: " + isRunning);
        if (isRunning && channelFuture != null) {
            channelFuture.channel().close();
        }
        while (isRunning) {
            LogUtil.info("等待服务端关闭, 1s...");
            Thread.sleep(1000);
        }
    }
}
