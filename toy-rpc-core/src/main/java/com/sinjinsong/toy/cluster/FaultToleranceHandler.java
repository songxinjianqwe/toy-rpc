package com.sinjinsong.toy.cluster;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;

/**
 * @author sinjinsong
 * @date 2018/7/22
 * 无状态
 * 注意！
 * 集群容错只对同步调用有效
 */
public interface FaultToleranceHandler {
    RPCResponse handle(ClusterInvoker clusterInvoker, InvokeParam invokeParam,RPCException e);
}
