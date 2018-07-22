package com.sinjinsong.toy.common.context;

import com.sinjinsong.toy.protocol.api.Invoker;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
public class RPCThreadLocalInvoker {
    private static final ThreadLocal<RPCThreadLocalInvoker> RPC_CONTEXT = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return new RPCThreadLocalInvoker();
        }
    };
    
    private RPCThreadLocalInvoker() {
        
    }
    private Invoker invoker;
    
    public Invoker getInvoker() {
        return invoker;
    }
    
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }
    
    public  static RPCThreadLocalInvoker getContext() {
        return RPC_CONTEXT.get();
    }
}
