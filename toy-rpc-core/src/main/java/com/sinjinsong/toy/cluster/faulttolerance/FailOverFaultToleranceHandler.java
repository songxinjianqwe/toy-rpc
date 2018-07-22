package com.sinjinsong.toy.cluster.faulttolerance;

import com.github.rholder.retry.*;
import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author sinjinsong
 * @date 2018/7/22
 * 
 * 如果有4个invoker，invoker0~invoker3
 * 先调用loadbalancer选出一个invoker，如果失败，则retry
 * retry0：从全部invoker去掉调用失败的invoker，再调用loadbalancer，选出一个invoker
 * 如果全部去掉，还没有调用成功；或者超时重试次数，则抛出RPCException，结束
 */
@Slf4j
public class FailOverFaultToleranceHandler implements com.sinjinsong.toy.cluster.FaultToleranceHandler {
    
    @Override
    public  RPCResponse handle(Map<String,Invoker> excludedInvokers, ClusterInvoker clusterInvoker, InvokeParam invokeParam) {
        log.error("出错,FailOver! requestId:{}", invokeParam.getRequestId());
        try {
            return retry(excludedInvokers,clusterInvoker,invokeParam);
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } catch (RetryException e1) {
            e1.printStackTrace();
            log.info("超过出错重试次数，不再重试  requestId:{}", invokeParam.getRequestId());
            throw new RPCException(ErrorEnum.RETRY_EXCEED_MAX_TIMES,e1, "超过出错重试次数 requestId:{}", invokeParam.getRequestId());
        }
        return null;
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
    private  RPCResponse retry(Map<String,Invoker> excludedInvokers, ClusterInvoker clusterInvoker,InvokeParam invokeParam) throws ExecutionException, RetryException {
        Retryer<RPCResponse> retryer = RetryerBuilder.<RPCResponse>newBuilder()
                .retryIfException(
                        t -> {
                            // 如果一个异常是RPCException并且是没有服务，则不再重试
                            // 其他情况俊辉重试
                            if(t instanceof RPCException) {
                                RPCException rpcException = (RPCException) t;
                                if(rpcException.getErrorEnum() == ErrorEnum.NO_SERVER_AVAILABLE) {
                                    return false;
                                }
                            }
                            return true;
                        }
                ) // 抛出Throwable时重试 
                .withWaitStrategy(WaitStrategies.incrementingWait(1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 重试3次后停止  
                .build();
        return retryer.call(() -> {
            log.info("开始本次重试...");
            return clusterInvoker.invoke(excludedInvokers,invokeParam);
        });
    }
}
