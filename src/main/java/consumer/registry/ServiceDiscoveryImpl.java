package consumer.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscoveryImpl implements ServiceDiscovery {
    //存放获取到的url集合
    List<String> respUrlLists=new ArrayList<>();
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
    @Override
    public String discover(String ServiceName) {
        String path="/registrys/"+ServiceName;
        System.out.println(path);
        try {
        //获取父节点下面的子节点集合
        respUrlLists=curatorFramework.getChildren().forPath(path);
            System.out.println(curatorFramework.getChildren().forPath(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //动态感知服务节点的一个变化
        registerWatch(path);
        //负载均衡
        //...

        //这里我直接获取第一个url
        if(respUrlLists.size()>0)
        return  respUrlLists.get(0);
        else return null;
    }
    private void registerWatch(final String path){
        PathChildrenCache childrenCache=new PathChildrenCache(curatorFramework,path,true);
        PathChildrenCacheListener pathChildrenCacheListener=new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                //如果发生了变化，就重新获取更新子节点数据
                respUrlLists=curatorFramework.getChildren().forPath(path);
            }
        };
        try {
            childrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
