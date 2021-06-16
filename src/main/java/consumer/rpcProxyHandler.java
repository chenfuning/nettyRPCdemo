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
        Response=msg;
    }
}
