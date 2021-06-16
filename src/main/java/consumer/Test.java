package consumer;

import consumer.com.ning.service.HelloService;
import consumer.registry.ServiceDiscovery;
import consumer.registry.ServiceDiscoveryImpl;

/**
 * @desc   客户端启动类
 * @author  Admin
 * @create  2021/6/16
 **/
public class Test {
    public static void main(String[] args) {
        ServiceDiscovery serviceDiscovery=new ServiceDiscoveryImpl();
        //创建代理类
        RpcClientProxy rpcClientProxy=new RpcClientProxy(serviceDiscovery);
        HelloService getHello=rpcClientProxy.create(HelloService.class);
        System.out.println(getHello.hello("ning"));
    }
}
