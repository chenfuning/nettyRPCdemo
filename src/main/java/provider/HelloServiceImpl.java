package provider;
/**
 * @desc    提供服务的实现类
 * @author  Admin
 * @create  2021/6/15
 **/
@RpcAnnotation(HelloService.class) //这个注解标明是实现哪一个接口的
public class HelloServiceImpl  implements  HelloService{
    public String hello(String name){return "hello"+name;}
}
