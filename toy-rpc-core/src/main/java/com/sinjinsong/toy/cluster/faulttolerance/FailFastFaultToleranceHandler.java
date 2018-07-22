package com.sinjinsong.toy.cluster.faulttolerance;

import com.sinjinsong.toy.cluster.ClusterInvoker;
import com.sinjinsong.toy.cluster.FaultToleranceHandler;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

import java.util.Map;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
public class FailFastFaultToleranceHandler implements FaultToleranceHandler {
    
    @Override
    public RPCResponse handle(Map<String, Invoker> excludedInvokers, ClusterInvoker clusterInvoker, InvokeParam invokeParam) {
        return null;
    }
}
