package com.sinjinsong.toy.common.util;

import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.support.RPCInvokeParam;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;

/**
 * @author sinjinsong
 * @date 2018/7/19
 */
public class InvokeParamUtil {
    
    public static RPCRequest extractRequestFromInvokeParam(InvokeParam invokeParam) {
        return  ((RPCInvokeParam)invokeParam).getRpcRequest();
    }
    
    public static ReferenceConfig extractReferenceConfigFromInvokeParam(InvokeParam invokeParam) {
        return  ((RPCInvokeParam)invokeParam).getReferenceConfig();
    }
    
}
