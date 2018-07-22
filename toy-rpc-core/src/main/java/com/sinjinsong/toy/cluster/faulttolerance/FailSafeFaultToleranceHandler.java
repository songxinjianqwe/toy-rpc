package com.sinjinsong.toy.cluster.faulttolerance;

import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.cluster.FaultToleranceHandler;
import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
@Slf4j
public class FailSafeFaultToleranceHandler implements FaultToleranceHandler {
    @Override
    public RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam, RPCException e) {
        log.error("出错,FailSafe! requestId:{}", invokeParam.getRequestId());
        return null;
    }
}
