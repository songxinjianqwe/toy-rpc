package com.sinjinsong.toy.protocol.api.support;

import com.sinjinsong.toy.common.exception.RPCException;
import com.sinjinsong.toy.common.util.InvokeParamUtil;
import com.sinjinsong.toy.config.ReferenceConfig;
import com.sinjinsong.toy.filter.Filter;
import com.sinjinsong.toy.invoke.api.support.AbstractInvocation;
import com.sinjinsong.toy.invoke.async.AsyncInvocation;
import com.sinjinsong.toy.invoke.callback.CallbackInvocation;
import com.sinjinsong.toy.invoke.oneway.OneWayInvocation;
import com.sinjinsong.toy.invoke.sync.SyncInvocation;
import com.sinjinsong.toy.protocol.api.InvokeParam;
import com.sinjinsong.toy.protocol.api.Invoker;
import com.sinjinsong.toy.transport.api.domain.RPCRequest;
import com.sinjinsong.toy.transport.api.domain.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author sinjinsong
 * @date 2018/7/21
 */
@Slf4j
public abstract class AbstractInvoker<T> implements Invoker<T> {
    private Class<T> interfaceClass;
    
    @Override
    public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
        Function<RPCRequest, Future<RPCResponse>> logic = getProcessor();
        // 如果提交任务失败，则删掉该Endpoint，再次提交的话必须重新创建Endpoint
        AbstractInvocation invocation;
        ReferenceConfig referenceConfig = InvokeParamUtil.extractReferenceConfigFromInvokeParam(invokeParam);
        RPCRequest rpcRequest = InvokeParamUtil.extractRequestFromInvokeParam(invokeParam);
        if (referenceConfig.isAsync()) {
            invocation = new AsyncInvocation() {
                @Override
                protected Future<RPCResponse> doInvoke() {
                    return logic.apply(rpcRequest);
                }
            };
        } else if (referenceConfig.isCallback()) {
            invocation = new CallbackInvocation() {
                @Override
                protected Future<RPCResponse> doInvoke() {
                    return logic.apply(rpcRequest);
                }
            };
        } else if (referenceConfig.isOneWay()) {
            invocation = new OneWayInvocation() {
                @Override
                protected Future<RPCResponse> doInvoke() {
                    return logic.apply(rpcRequest);
                }
            };
        } else {
            invocation = new SyncInvocation() {
                @Override
                protected Future<RPCResponse> doInvoke() {
                    return logic.apply(rpcRequest);
                }
            };
        }
        invocation.setReferenceConfig(referenceConfig);
        invocation.setRpcRequest(rpcRequest);
        return invocation.invoke();
    }

    /**
     * 如果没有重写invoke方法，则必须重写该方法
     *
     * @return
     */
    protected Function<RPCRequest, Future<RPCResponse>> getProcessor() {
        return null;
    }

    /**
     * 最终给ClusterInvoker的invoker，是用户接触到的invoker
     *
     * @param filters
     * @param <T>
     * @return
     */
    public <T> Invoker<T> buildFilterChain(List<Filter> filters) {
        // refer 得到的，包含了endpoint
        AbstractInvoker<T> actualInvoker = (AbstractInvoker<T>) this;
        return new AbstractInvoker<T>() {
            // 比较的时候就是在比较interfaceClass
            @Override
            public int hashCode() {
                return actualInvoker.getInterface().hashCode();
            }
            
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Invoker) {
                    Invoker rhs = (Invoker) obj;
                    return actualInvoker.getInterface().equals(rhs.getInterface());
                }
                return false;
            }

            @Override
            public Class<T> getInterface() {
                return actualInvoker.getInterface();
            }
            
            @Override
            public String getAddress() {
                return actualInvoker.getAddress();
            }

            @Override
            public void close() {
                actualInvoker.close();
            }

            private ThreadLocal<AtomicInteger> filterIndex = new ThreadLocal() {
                @Override
                protected Object initialValue() {
                    return new AtomicInteger(0);
                }
            };

            @Override
            public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
                log.info("filterIndex:{}, invokeParam:{}", filterIndex.get().get(), invokeParam);
                final Invoker<T> invokerWrappedFilters = this;
                if (filterIndex.get().get() < filters.size()) {
                    return filters.get(filterIndex.get().getAndIncrement()).invoke(new AbstractInvoker() {
                        @Override
                        public Class<T> getInterface() {
                            return actualInvoker.getInterface();
                        }

                        @Override
                        public String getAddress() {
                            return actualInvoker.getAddress();
                        }

                        @Override
                        public RPCResponse invoke(InvokeParam invokeParam) throws RPCException {
                            return invokerWrappedFilters.invoke(invokeParam);
                        }
                    }, invokeParam);
                }
                filterIndex.get().set(0);
                return actualInvoker.invoke(invokeParam);
            }
        };
    }

    @Override
    public Class<T> getInterface() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public String getAddress() {
        try {
            log.info("getAddress->localAddress");
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {

    }
}
