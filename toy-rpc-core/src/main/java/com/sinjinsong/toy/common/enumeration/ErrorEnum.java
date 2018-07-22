package com.sinjinsong.toy.common.enumeration;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
public enum ErrorEnum {
    NO_SERVER_AVAILABLE("未找到可用服务器"),
    CONNECT_TO_SERVER_FAILURE("连接失败,关闭客户端"),
    SUBMIT_AFTER_ENDPOINT_CLOSED("Endpoint关闭后仍在提交任务"),
    TRANSPORT_FAILURE("RPC传输失败"),
    RETRY_EXCEED_MAX_TIMES("超时重试RPC调用次数"),
    GET_PROCESSOR_MUST_BE_OVERRIDE_WHEN_INVOKE_DID_NOT_OVERRIDE("没有重写AbstractInvoker#invoke方法的时候，必须重写getProcessor方法"),
    SAME_INTERFACE_ONLY_CAN_BE_REFERED_IN_THE_SAME_WAY("同一个接口只能以相同的配置引用"),
    TEMPLATE_REPLACEMENT_ERROR("出错消息模板替换出错"),
    EXPOSED_SERVICE_NOT_FOUND("未找到暴露的服务"),
    READ_LOCALHOST_ERROR("读取本地Host失败"),
    SERVICE_INVOCATION_FAILURE("服务调用失败"),
    REGISTRY_ERROR("注册中心故障"),
    SERIALIZER_ERROR("序列化故障"),
    CONFIG_ERROR("用户配置错误"),
    AUTOWIRE_REFERENCE_PROXY_ERROR("注册proxy实例失败"),
    SERVICE_DID_NOT_IMPLEMENT_ANY_INTERFACE("该服务未实现任何服务接口"),
    ;
    private String errorCode;

    ErrorEnum(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
