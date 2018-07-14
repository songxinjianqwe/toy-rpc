package com.sinjinsong.toy.invocation.sync;

import com.github.rholder.retry.*;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.invocation.api.support.AbstractInvocation;
import com.sinjinsong.toy.transport.common.domain.RPCRequest;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
@Slf4j
public class SyncInvocation extends AbstractInvocation {
        
    @Override
    public Object invoke(RPCRequest request) throws RPCException {
        RPCResponse response = null;
        try {
            response = executeAndWaitForResponse(request,referenceConfig.getTimeout());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("出错,FailOver!");
            try {
                response = retry(referenceConfig.getTimeout(),request);
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            } catch (RetryException e1) {
                e1.printStackTrace();
                log.info("超过出错重试次数，不再重试");
                throw new RPCException("超过出错重试次数",e1);
            }
        }
        log.info("客户端读到响应");
        if (response.hasError()) {
            throw new RPCException("invocation failed",response.getCause());
        } else {
            return response.getResult();
        }
    }
    
    private RPCResponse executeAndWaitForResponse(RPCRequest request, Long timeout) throws Exception {
        Future<RPCResponse> future = execute(request);
        return future.get(timeout, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 实现重新连接的重试策略
     * 一开始是等待5s，第二次是等待10s，再下一次是等待15s
     * 但是在发现服务器地址时会等待10s，如果一直没有服务器信息变动的话
     *
     * @return
     * @throws ExecutionException
     * @throws RetryException
     */
    private RPCResponse retry(Long timeout, RPCRequest request) throws ExecutionException, RetryException {
        Retryer<RPCResponse> retryer = RetryerBuilder.<RPCResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 抛出IOException时重试 
                .withWaitStrategy(WaitStrategies.incrementingWait(5, TimeUnit.SECONDS, 5, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5)) // 重试5次后停止  
                .build();
        return retryer.call(() -> {
            log.info("重新连接中...");
            return executeAndWaitForResponse(request,timeout);
        });
    }
}
