package cn.sinjinsong.rpc.util;

import cn.sinjinsong.rpc.annotation.RPCService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by SinjinSong on 2017/7/31.
 */
@Slf4j
public class AnnotationUtilTest {
    @Test
    public void getClassesWithAnnotation() throws Exception {
        Map<String, Object> services = AnnotationUtil.getServices();
        log.info("{}",services);
        List<Class<?>> classesWithAnnotation = AnnotationUtil.getClassesWithAnnotation(RPCService.class, "cn.sinjinsong.netty.serializable.service");
        log.info("{}",classesWithAnnotation);
    }
    

}