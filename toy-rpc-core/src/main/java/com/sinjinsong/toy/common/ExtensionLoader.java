package com.sinjinsong.toy.common;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.enumeration.support.ExtensionBaseType;
import com.sinjinsong.toy.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
@Slf4j
public class ExtensionLoader {
    private static ExtensionLoader INSTANCE = new ExtensionLoader();
    
    public static ExtensionLoader init(Map<String, Map<String, Object>> extensionMap) {
        INSTANCE.extensionMap = extensionMap;
        return INSTANCE;
    }
    
    public static  ExtensionLoader getInstance() {
        return INSTANCE;
    }
    
    private Map<String, Map<String, Object>> extensionMap = new HashMap<>();
    private ExtensionLoader(){}
    
    public <T> T load(Class<T> interfaceClass, Class enumType, String type) {
        ExtensionBaseType<T> extensionBaseType = ExtensionBaseType.valueOf(enumType, type.toUpperCase());
        if (extensionBaseType != null) {
            return extensionBaseType.getInstance();
        }
        if (!extensionMap.containsKey(interfaceClass.getName())) {
            throw new RPCException(ErrorEnum.NO_SUPPORTED_INSTANCE, "{} 没有可用的实现类", interfaceClass);

        }
        Object o = extensionMap.get(interfaceClass.getName()).get(type);
        if (o == null) {
            throw new RPCException(ErrorEnum.NO_SUPPORTED_INSTANCE, "{} 没有可用的实现类", interfaceClass);
        }
        return interfaceClass.cast(o);
    }
    
    public <T> List<T> load(Class<T> interfaceClass) {
        if(!extensionMap.containsKey(interfaceClass.getName())) {
            return Collections.EMPTY_LIST;
        }
        Collection<Object> values = extensionMap.get(interfaceClass.getName()).values();
        List<T> instances = new ArrayList<>();
        values.forEach(value -> instances.add(interfaceClass.cast(value)));
        return instances;
    }
    
    public void register(Class<?> interfaceClass, String alias, Object instance) {
        if(!extensionMap.containsKey(interfaceClass.getName())) {
            extensionMap.put(interfaceClass.getName(),new HashMap<>());
        }
        log.info("注册bean: interface:"+ interfaceClass + ",alias:{},instance:{}",alias,instance);
        extensionMap.get(interfaceClass.getName()).put(alias,instance);
    }
}
