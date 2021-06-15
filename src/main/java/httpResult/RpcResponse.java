package httpResult;
/**
 * @desc    响应消息结构
 * @author  Admin
 * @create  2021/6/15
 **/
public class RpcResponse {
    //请求id
    private String requestId;
    //异常
    private Exception exception;
    //结果
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
