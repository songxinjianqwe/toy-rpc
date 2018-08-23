package com.sinjinsong.toy.sample.spring.client.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.common.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author sinjinsong
 * @date 2018/7/16
 */
@Slf4j
@Component
public class BizLogFilter implements Filter {
    @Override
    public RPCResponse invoke(Invoker invoker, InvokeParam invokeParam) throws RPCException {
         log.info("Biz Logger: invokeParam:{} start!",invokeParam);
        RPCResponse response = invoker.invoke(invokeParam);
        log.info("Biz Logger: invokeParam:{} complele!",invokeParam);
        return response;
    }
}
