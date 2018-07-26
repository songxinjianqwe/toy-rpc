package com.sinjinsong.toy.common;

import com.sinjinsong.toy.common.enumeration.ErrorEnum;
import com.sinjinsong.toy.common.enumeration.support.ExtensionBaseType;
import com.sinjinsong.toy.common.exception.RPCException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */
@Slf4j
public class ExtensionLoader {
    private static ExtensionLoader INSTANCE = new ExtensionLoader();

    public static ExtensionLoader getInstance() {
        return INSTANCE;
    }
    
    public void loadResources() {
        URL parent = this.getClass().getClassLoader().getResource("toy");
        if (parent != null) {
            log.info("/toy配置文件存在，开始读取...");
            File dir = new File(parent.getFile());
            File[] files = dir.listFiles();
            for (File file : files) {
                handleFile(file);
            }
            log.info("配置文件读取完毕!");
        }
    }

    private void handleFile(File file) {
        log.info("开始读取文件:{}", file);
        String interfaceName = file.getName();
        try {
            Class<?> interfaceClass = Class.forName(interfaceName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=");
                if (kv.length != 2) {
                    log.error("配置行不是x=y的格式的:{}", line);
                    throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "配置行不是x=y的格式的:{}", line);
                }
                // 如果有任何异常，则跳过这一行
                try {
                    Class<?> impl = Class.forName(kv[1]);
                    if (!interfaceClass.isAssignableFrom(impl)) {
                        log.error("实现类{}不是该接口{}的子类", impl, interfaceClass);
                        throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "实现类{}不是该接口{}的子类", impl, interfaceClass);
                    }
                    Object o = impl.newInstance();
                    register(interfaceClass, kv[0], o);
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new RPCException(ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "实现类对象{}加载类或实例化失败", kv[1]);
                }
            }
            br.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RPCException(e, ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "接口对象{}加载类失败", file.getName());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPCException(e, ErrorEnum.EXTENSION_CONFIG_FILE_ERROR, "配置文件{}读取失败", file.getName());
        }
    }

    private Map<String, Map<String, Object>> extensionMap = new HashMap<>();

    private ExtensionLoader() {
    }

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
        if (!extensionMap.containsKey(interfaceClass.getName())) {
            return Collections.EMPTY_LIST;
        }
        Collection<Object> values = extensionMap.get(interfaceClass.getName()).values();
        List<T> instances = new ArrayList<>();
        values.forEach(value -> instances.add(interfaceClass.cast(value)));
        return instances;
    }

    public void register(Class<?> interfaceClass, String alias, Object instance) {
        if (!extensionMap.containsKey(interfaceClass.getName())) {
            extensionMap.put(interfaceClass.getName(), new HashMap<>());
        }
        log.info("注册bean: interface:" + interfaceClass + ",alias:{},instance:{}", alias, instance);
        extensionMap.get(interfaceClass.getName()).put(alias, instance);
    }
}
