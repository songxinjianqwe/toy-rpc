package com.sinjinsong.toy.sample.spring.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private String username;
}
