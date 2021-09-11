import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * netty的http客户端服务端测试
 *
 * @author xuchengkun
 * @date 2021/09/07 11:35
 **/
public class NettyHttpClientServerTest {

    public static void main(String[] args) throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EventLoopGroup bossGroup = new NioEventLoopGroup();
                    EventLoopGroup workGroup = new NioEventLoopGroup();

                    ServerBootstrap serverBootstrap = new ServerBootstrap();
                    serverBootstrap.group(bossGroup, workGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline().addLast(new HttpServerCodec());
                                    socketChannel.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
                                    socketChannel.pipeline().addLast(new IdleHandler());
                                }
                            });

                    ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();
                    System.out.println("服务端启动");
                    channelFuture.channel().closeFuture().sync();
                    System.out.println("服务端关闭");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EventLoopGroup workGroup = new NioEventLoopGroup();

                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(workGroup)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline().addLast(new HttpClientCodec());
                                    socketChannel.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
                                    socketChannel.pipeline().addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
                                    socketChannel.pipeline().addLast(new IdleHandler());
                                }
                            });

                    ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888);
                    System.out.println("客户端启动");

                    channelFuture.channel().closeFuture().sync();
                    System.out.println("客户端关闭");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static class IdleHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

            ByteBuf resp = Unpooled.wrappedBuffer("{\"resp\":\"ok\"}".getBytes("UTF-8"));

            //执行控制器并返回响应
            ByteBuf byteBuf = msg.content();
            String reqContent = byteBuf.toString(Charset.forName("UTF-8"));
            System.out.println("收到请求: " +  reqContent);

            HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                    , HttpResponseStatus.OK, resp);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_ENCODING, "UTF-8");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, resp.readableBytes());
            ctx.writeAndFlush(httpResponse);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("发送探测");
            ByteBuf req = Unpooled.wrappedBuffer("{\"test\":\"is ok?\"}".getBytes("UTF-8"));

            FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1
                    ,HttpMethod.POST, "/server/activeTest", req);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_ENCODING, "UTF-8");
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, req.readableBytes());

            ctx.writeAndFlush(fullHttpRequest);
        }
    }
}
