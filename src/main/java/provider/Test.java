package provider;

import provider.com.ning.service.HelloService;
import provider.com.ning.service.HelloServiceImpl;
import provider.register.RegisterCenter;
import provider.register.RegisterCenterImpl;
/**
 * @desc    相当于服务启动类
 * @author  Admin
 * @create  2021/6/16
 **/
public class Test {
    public static void main(String[] args) {
        //测试服务注册是否成功
//        RegisterCenter registerCenter=new RegisterCenterImpl();
//        registerCenter.register("testparent","localhost:8844");
//        //当创建临时节点的客户端崩溃或者关闭了与Zookeeper的连接时，这个节点就会被删除
        /**
         * @desc    服务发布(注册服务+端口监听（接收数据）) -->集中给一个类rpcserver来处理
         * @author  Admin
         * @create  2021/6/16
         **/
        //实例化服务接口实例
        HelloService helloService=new HelloServiceImpl();
        RegisterCenter registerCenter=new RegisterCenterImpl();
        RpcServer rpcServer=new RpcServer(registerCenter,"127.0.0.1:8845");
        //再注册和端口监听之前，维护多个实例对象到hashmap
        rpcServer.bind(helloService);
        //服务发布(注册服务+端口监听（接收数据）)
        rpcServer.publisher();
    }
}
