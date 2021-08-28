package com.xck.agent.netty;

import com.xck.agent.AnnotationScanner;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;

/**
 * http处理器
 *
 * @author xuchengkun
 * @date 2021/08/25 11:13
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        ByteBuf resp = Unpooled.wrappedBuffer("{\"resp\":\"system error\"}".getBytes("UTF-8"));

        try {
            String uri = msg.uri();
            AnnotationScanner.ObjExecutor objExecutor = AnnotationScanner.controllerMap.get(uri);
            if (objExecutor == null) {
                objExecutor = AnnotationScanner.originPluginMap.get(uri);
            }
            if (objExecutor == null) {
                resp = Unpooled.wrappedBuffer("{\"resp\":\"404 NOT FOUND\"}".getBytes("UTF-8"));
                return;
            }

            //执行控制器并返回响应
            ByteBuf byteBuf = msg.content();
            String response = (String) objExecutor.getMethod().invoke(objExecutor.getObject()
                    , byteBuf.toString(Charset.forName("UTF-8")));
            resp = Unpooled.wrappedBuffer(response.getBytes("UTF-8"));
        } finally {
            HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                    , HttpResponseStatus.OK, resp);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_ENCODING, "UTF-8");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, resp.readableBytes());
            ctx.writeAndFlush(httpResponse);
        }
    }
}
