package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

import java.util.Map;

/**
 * @author sinjinsong
 * @date 2018/7/22
 * 无状态
 */
public interface FailureHandler {
    RPCResponse handle(Map<String,Invoker> excludedInvokers, ClusterInvoker clusterInvoker, InvokeParam invokeParam);
}
