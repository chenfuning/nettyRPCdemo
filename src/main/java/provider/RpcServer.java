package provider;

import com.sun.jmx.snmp.ServiceName;
import provider.register.RegisterCenter;

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
            String serviceName=Annotation.value().getName();
            handlerMap.put(serviceName,service);
        }
    }

    public void publisher() {
        //把handlermap里的实现类都注册到zk中去,也就是把这个服务的所有服务注册到zk
        for(String serviceName: handlerMap.keySet()){
            registerCenter.register(serviceName,serverAddress);
        }
        //启动一个监听 Netty 实时监听 调用者发送过来的请求数据
    }
}
