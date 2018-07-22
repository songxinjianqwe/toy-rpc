package com.sinjinsong.toy.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author sinjinsong
 * @date 2018/7/22
 */

public class PlaceHolderUtilTest {
    
    @Test
    public void test() {
        assertEquals("测试一下1,123",PlaceHolderUtil.replace("测试一下{},{}",1,"123"));
    }
}
