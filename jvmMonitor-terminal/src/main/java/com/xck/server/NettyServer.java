package com.xck.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * netty服务端
 *
 * @author xuchengkun
 * @date 2021/08/25 11:05
 **/
public class NettyServer extends Thread {

    private ChannelFuture channelFuture;

    private volatile boolean isRunning;
    private volatile boolean isFinish;

    private volatile int port;

    public NettyServer() {
        setDaemon(true);
        setName("termail-server-thread");
    }

    @Override
    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                                    Integer.MAX_VALUE, 4, 4));
                            socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });

            channelFuture = serverBootstrap.bind(port).sync();
            port = ((InetSocketAddress)channelFuture.channel().localAddress()).getPort();

            if (channelFuture.isSuccess()) {
                System.out.println("启动终端服务成功, 端口: " + port);
                isRunning = true;
                isFinish = true;
            }

            channelFuture.channel().closeFuture().sync();
            System.out.println("关闭服务端成功, 端口: " + port);
        } catch (Throwable e) {
            System.err.println("服务端异常, 端口: " + port + ", errMsg: " + e);
        } finally {
            isRunning = false;
            isFinish = true;
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void shutdown() throws InterruptedException{
        System.out.println("准备关闭服务端，服务端运行状态: " + isRunning);
        if (isRunning && channelFuture != null) {
            channelFuture.channel().close();
        }
        while (isRunning) {
            System.out.println("等待服务端关闭, 1s...");
            Thread.sleep(1000);
        }
    }

    public int getPort() throws InterruptedException{
        while (!isRunning || !isFinish) {
            Thread.sleep(1000);
        }
        return port;
    }
}
