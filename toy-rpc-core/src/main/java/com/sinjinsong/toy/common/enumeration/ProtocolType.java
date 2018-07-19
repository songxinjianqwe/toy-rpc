package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.protocol.api.Protocol;
import com.sinjinsong.toy.protocol.http.HttpProtocol;
import com.sinjinsong.toy.protocol.injvm.InJvmProtocol;
import com.sinjinsong.toy.protocol.toy.ToyProtocol;

/**
 * @author sinjinsong
 * @date 2018/7/14
 */
public enum ProtocolType {
    HTTP(new HttpProtocol()), INJVM(new InJvmProtocol()), TOY(new ToyProtocol());
    private Protocol protocol;

    ProtocolType(Protocol protocol) {
        this.protocol = protocol;
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
