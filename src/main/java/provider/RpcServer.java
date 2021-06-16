package provider;

import com.sun.jmx.snmp.ServiceName;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import provider.register.RegisterCenter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc    处理服务发布类
 * @author  Admin
 * @create  2021/6/16
 **/
public class RpcServer {
    private RegisterCenter registerCenter;
    private String serverAddress;
    private Map<String,Object> handlerMap=new HashMap<>();//用来存放服务接口的多个实现类
    public RpcServer(RegisterCenter registerCenter,String serverAddress){
            this.registerCenter=registerCenter;
            this.serverAddress=serverAddress;
    }
    //发布服务到监听端口

    //把服务接口实现类放到handlerMap中
    public void bind(Object... services){
        for(Object service:services){
            //通过注解获取服务实现类的服务名称
            RpcAnnotation Annotation=service.getClass().getAnnotation(RpcAnnotation.class);

            //String serviceName=Annotation.value().getName();
            //和上面的区别就是com.ning.service.HelloService 和 HelloService
            String serviceName=Annotation.value().getSimpleName();
            handlerMap.put(serviceName,service);
        }
    }

    public void publisher() {
        //把handlermap里的实现类都注册到zk中去,也就是把这个服务的所有服务注册到zk
        for(String serviceName: handlerMap.keySet()){
            registerCenter.register(serviceName,serverAddress);
        }
        //启动一个监听 Netty 实时监听 调用者发送过来的请求数据
        try {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap bootstrap =new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.localAddress(new InetSocketAddress(8845));
        bootstrap.childHandler(new ChannelInitializer<Channel>(){

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline=channel.pipeline();
                //LengthFieldBasedFrameDecoder自定义长度解决TCP粘包黏包问题
                pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                //对象参数类型编码器
                pipeline.addLast("Encoder",new ObjectEncoder());
                // 对象参数类型解码器
                pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                //自定义handler
                pipeline.addLast(new RpcServerHandler(handlerMap));
            }
        });
        String[] addrs=serverAddress.split(":");
        String ip=addrs[0];
        int port=Integer.parseInt(addrs[1]);
        ChannelFuture future=bootstrap.bind().sync();
            System.out.println("netty服务端启动完成，监听"+future.channel().localAddress()+"等待客户端连接");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
