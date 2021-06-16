package provider;

import httpResult.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    private Map<String,Object> handlerMap=new HashMap<>();
    public RpcServerHandler(Map<String,Object> handlerMap){
        this.handlerMap=handlerMap;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx可以用来服务端发送数据给客户
        //msg是客户端发来的数据
        //获取客户端发送来的数据,转发为封装好的数据，
        System.out.println("有数据了");
        RpcRequest rpcRequest=(RpcRequest) msg;
        System.out.println("客户端封装好的数据是："+rpcRequest.getInterfaceName()+rpcRequest.getMethodName());
        //发送数据返回给客户端
        Object result=new Object();
        //如果客户端发送来的请求在服务map集合里面
        if(handlerMap.containsKey(rpcRequest.getInterfaceName())){
            //使用对象实例进行执行
            Object service=handlerMap.get(rpcRequest.getInterfaceName());
            /**person.getClass().getMethod("run", String.class);
             *获得person对象的run方法，因为run方法的形参是String类型的，所以parameterTypes为String.class
             * 如果对象内的方法的形参是int类型的，则parameterTypes是int.class
             */
            Method method=service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
            //调用方法后返回结果
            result=method.invoke(service,rpcRequest.getParameters());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
