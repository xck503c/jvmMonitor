package com.xck.agent.netty;

import com.xck.util.LogUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * netty服务端
 *
 * @author xuchengkun
 * @date 2021/08/25 11:05
 **/
public class NettyClient extends Thread {

    private ChannelFuture channelFuture;

    private volatile boolean isRunning;

    private int port;

    public NettyClient(int port) {
        this.port = port;
        setDaemon(true);
    }

    @Override
    public void run() {

        LogUtil.info("开始启动服务端");
        isRunning = true;
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {

            Bootstrap client = new Bootstrap();
            client.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                                    Integer.MAX_VALUE, 4, 4));
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            channelFuture = client.connect("127.0.0.1", port);

            if (channelFuture.isSuccess()) {
                LogUtil.info("启动客户端成功, 连接端口: " + port);
            }

            channelFuture.channel().closeFuture().sync();
            LogUtil.info("关闭客户端成功, 端口: " + port);
        } catch (Throwable e) {
            LogUtil.error("客户端异常, 端口: " + port + ", errMsg: ", e);
        } finally {
            workerGroup.shutdownGracefully();
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void shutdown() throws InterruptedException{
        LogUtil.info("准备关闭客户端，客户端运行状态: " + isRunning);
        if (isRunning && channelFuture != null) {
            channelFuture.channel().close();
        }
        while (isRunning) {
            LogUtil.info("等待客户端关闭, 1s...");
            Thread.sleep(1000);
        }
    }

    public int getPort() {
        return port;
    }
}
