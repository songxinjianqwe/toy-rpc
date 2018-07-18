package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPCInvokeParam implements InvokeParam {
    protected ReferenceConfig referenceConfig;
    protected RPCRequest rpcRequest;

    public ReferenceConfig getReferenceConfig() {
        return referenceConfig;
    }

    public RPCRequest getRpcRequest() {
        return rpcRequest;
    }

    @Override
    public String getInterfaceName() {
        return rpcRequest.getInterfaceName();
    }

    @Override
    public String getMethodName() {
        return rpcRequest.getMethodName();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return rpcRequest.getParameterTypes();
    }

    @Override
    public Object[] getParameters() {
        return rpcRequest.getParameters();
    }

    @Override
    public String getRequestId() {
        return rpcRequest.getRequestId();
    }

    @Override
    public String toString() {
        return "RPCInvokeParam{" +
                rpcRequest +
                '}';
    }
}
