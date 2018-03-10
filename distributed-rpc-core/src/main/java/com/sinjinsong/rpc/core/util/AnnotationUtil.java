package com.sinjinsong.rpc.core.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2018/3/10
 */
@Slf4j
public class AnnotationUtil {
    
    public static Annotation getMainClassAnnotation(Class annotationClass) {
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            log.info("Systen.env: entry:{}",entry);
            if (entry.getKey().startsWith("JAVA_MAIN_CLASS")) {
                String mainClass = entry.getValue();
                log.debug("Main class: {}", mainClass);
                try {
                    Class<?> cls = Class.forName(mainClass);
                    return cls.getAnnotation(annotationClass);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Cannot determine main class.");
                }
            }
        }
        return null;
    }
}
