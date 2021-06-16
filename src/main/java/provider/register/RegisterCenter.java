package provider.register;

public interface RegisterCenter {
    /**
     * @desc    服务注册
     * String serviceName,String serverAdress一起绑定到zk上面
     * @author  Admin
     * @create  2021/6/16
     **/
    void register(String serviceName,String serverAdress);
}
