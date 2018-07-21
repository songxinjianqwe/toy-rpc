package com.sinjinsong.toy.protocol.http;

import com.sinjinsong.toy.config.ApplicationConfig;
import com.sinjinsong.toy.protocol.api.support.AbstractRemoteInvoker;
import com.sinjinsong.toy.transport.api.Endpoint;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import com.sinjinsong.toy.transport.http.client.HttpEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/7/18
 */
@Slf4j
public class HttpInvoker<T> extends AbstractRemoteInvoker<T> {

    @Override
    protected Function<RPCRequest, Future<RPCResponse>> getProcessor() {
        return rpcRequest -> getEndpoint().submit(rpcRequest);
    }

    @Override
    protected Endpoint doInitEndpoint(String address, ApplicationConfig applicationConfig) {
        HttpEndpoint httpEndpoint = new HttpEndpoint();
        httpEndpoint.init(applicationConfig, address);
        return httpEndpoint;
    }
}
