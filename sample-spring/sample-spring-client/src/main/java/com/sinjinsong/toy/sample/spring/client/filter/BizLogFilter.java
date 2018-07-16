package com.sinjinsong.toy.sample.spring.client.filter;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.invoke.api.Invocation;
import com.sinjinsong.toy.transport.common.domain.RPCResponse;
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
    public RPCResponse invoke(Invocation invocation) throws RPCException {
        log.info("Biz Logger: invocation:{} start!",invocation);
        RPCResponse response = invocation.invoke();
        log.info("Biz Logger: invocation:{} complele!",invocation);
        return response;
    }
}
