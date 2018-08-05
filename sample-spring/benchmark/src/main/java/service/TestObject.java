package service;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sinjinsong
 * @date 2018/8/6
 */
@Data
public class TestObject implements Serializable {
    private int i = 0;
    private String name = "123";
}
