package consumer;

import consumer.registry.ServiceDiscovery;
import httpResult.MarshallingCodeCFactory;
import httpResult.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import provider.RpcServerHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;

public class RpcClientProxy {
    private ServiceDiscovery serviceDiscovery;
    public RpcClientProxy(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery=serviceDiscovery;
    }
    public <T> T create(final Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                       //封装RpcRequest对象
                        RpcRequest request=new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getSimpleName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);
                        System.out.println("客户端封装的数据是"+request.getInterfaceName()+"/"+request.getMethodName());
                        System.out.println("客户端可以调用动态代理类并调用了invoke方法"+request.getMethodName());
                        //通信把RpcRequest发送个对应的服务端
//                        String serviceName=interfaceClass.getName();
                        String serviceName=interfaceClass.getSimpleName();
                        //从zk里面获取url
                        String serverAddress=serviceDiscovery.discover(serviceName);
                        String[] addrs=serverAddress.split(":");
                        String ip=addrs[0];
                        int port=Integer.parseInt(addrs[1]);
                        final rpcProxyHandler proxyHandler=new rpcProxyHandler();
                        //通过netty发送
                        EventLoopGroup group=new NioEventLoopGroup();
                        try {
                        Bootstrap bootstrap=new Bootstrap();
                        bootstrap.group(group).channel(NioSocketChannel.class)
                                .remoteAddress(new InetSocketAddress(ip, port))
                                .handler(new ChannelInitializer<Channel>() {
                                    @Override
                                    protected void initChannel(Channel channel) throws Exception {
                                        ChannelPipeline pipeline=channel.pipeline();
//                                        //LengthFieldBasedFrameDecoder自定义长度解决TCP粘包黏包问题
//                                        pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
//                                        pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
//                                        //对象参数类型编码器
//                                        pipeline.addLast("Encoder",new ObjectEncoder());
//                                        // 对象参数类型解码器
//                                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                        pipeline.addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                                        pipeline.addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                                        //自定义handler，把发送数据到channel
                                        pipeline.addLast(proxyHandler);
                                    }
                                });
                        ChannelFuture future=bootstrap.connect().sync();
                        System.out.println("客户端"+future.channel().localAddress()+"连接");
                        //future.channel().writeAndFlush("123123");
                        future.channel().writeAndFlush(request);
                        System.out.println("客户端已发送请求数据");
                        future.channel().closeFuture().sync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            group.shutdownGracefully();
                        }
                        return proxyHandler.getResponse();
                    }
                });
    }
}
