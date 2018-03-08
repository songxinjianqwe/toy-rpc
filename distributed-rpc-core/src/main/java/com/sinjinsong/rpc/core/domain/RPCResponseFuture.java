package com.sinjinsong.rpc.core.domain;

/**
 * 响应凭证
 *
 * @author prestigeding@126.com
 */
public class RPCResponseFuture {

    private RPCResponse response;

    public RPCResponse getResponse() {
        if (response != null) {
            return response;
        }

        /**
         * 使用本身的对象锁即可
         */
        synchronized (this) {
            if (response != null) {
                return response;
            }
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public void setResponse(RPCResponse result) {
        this.response = result;
        synchronized (this) {
            this.notifyAll();
        }
    }
}
