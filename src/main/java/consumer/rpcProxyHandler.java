package consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.xml.ws.Response;

public class rpcProxyHandler extends ChannelInboundHandlerAdapter {
    private Object Response;
    public  Object getResponse() {
        return Response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端有数据了");
        Response=msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端已连接");
    }
}
