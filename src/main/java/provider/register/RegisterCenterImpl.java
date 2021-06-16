package provider.register;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterCenterImpl implements RegisterCenter {
    //连接zk
    //初始化CuratorFramework
    private CuratorFramework curatorFramework;
    {
        curatorFramework= CuratorFrameworkFactory.builder()
                .connectString("192.168.6.128:2181")
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,10)).build();
        //启动,连接zk
        curatorFramework.start();
    }
    /**
     * @desc     注册服务，也就是把服务名称和服务路径保存为node
     * @author  Admin
     * @create  2021/6/16
     **/
    @Override
    public void register(String serviceName, String serverAdress) {
        //创建节点
        // 父节点的路径： /registrys/hello
        String servicePath="/registrys/"+serviceName;
        try {
            //判断节点是否存在
            if(curatorFramework.checkExists().forPath(servicePath)==null){
                    //创建持久的父节点
                    curatorFramework.create().creatingParentsIfNeeded()
                            //创建持久的父节点,把0作为数据先放入
                            .withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
                    //有了父节点 /registrys/hello
                    //服务发布的地址127.0.0.1:8080，保存到父节点下面，作为临时(EPHEMERAL短暂的)节点： /registrys/hello/127.0.0.1:8080
                    String childaddresspath=servicePath+"/"+ serverAdress;
                    String renode=curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(childaddresspath,"0".getBytes());
                System.out.println("服务注册成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
