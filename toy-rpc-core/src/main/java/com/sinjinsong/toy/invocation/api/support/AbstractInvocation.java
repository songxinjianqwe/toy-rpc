package com.sinjinsong.toy.invocation.api.support;


import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.invocation.api.Invocation;
import com.sinjinsong.toy.protocol.api.Invoker;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public abstract class AbstractInvocation implements Invocation {
    protected ReferenceConfig referenceConfig;
    protected Invoker invoker;
    
    @Override
    public void setReferenceConfig(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    @Override
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }
}
