package consumer.registry;
/**
 * @desc    从zk获取服务地址
 * @author  Admin
 * @create  2021/6/16
 **/
public interface ServiceDiscovery {
    //根据服务名称获取 url地址
    String discover(String ServiceName);
}
