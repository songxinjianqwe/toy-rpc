package com.sinjinsong.toy.common.enumeration;

import com.sinjinsong.toy.cluster.FaultToleranceHandler;
import com.sinjinsong.toy.cluster.faulttolerance.FailFastFaultToleranceHandler;
import com.sinjinsong.toy.cluster.faulttolerance.FailOverFaultToleranceHandler;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
public enum FaultToleranceType {
    FAILOVER(new FailOverFaultToleranceHandler()),FAILFAST(new FailFastFaultToleranceHandler());
    
    private FaultToleranceHandler faultToleranceHandler;

    FaultToleranceType(FaultToleranceHandler faultToleranceHandler) {
        this.faultToleranceHandler = faultToleranceHandler;
    }
    
    public FaultToleranceHandler getFaultToleranceHandler() {
        return faultToleranceHandler;
    }
}
