package com.xck.agent.netty;

import com.xck.agent.AnnotationScanner;
import com.xck.util.LogUtil;
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

        String uri = msg.uri();
        try {
            AnnotationScanner.ObjExecutor objExecutor = AnnotationScanner.controllerMap.get(uri);
            if (objExecutor == null) {
                objExecutor = AnnotationScanner.originPluginMap.get(uri);
            }
            if (objExecutor == null) {
                resp = Unpooled.wrappedBuffer("{\"resp\":\"404 未找到\"}".getBytes("UTF-8"));
                return;
            }

            //执行控制器并返回响应
            ByteBuf byteBuf = msg.content();
            String reqContent = byteBuf.toString(Charset.forName("UTF-8"));
            LogUtil.info("收到请求: uri: " + uri + ", 请求内容: " + reqContent);
            String response = (String) objExecutor.getMethod().invoke(objExecutor.getObject(), reqContent);
            LogUtil.info("响应: uri: " + uri + ", 响应内容: " + response);
            resp = Unpooled.wrappedBuffer(response.getBytes("UTF-8"));
        } catch (Throwable e) {
            LogUtil.error("系统错误: uri: " + uri + ", errMsg: " + e);
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
