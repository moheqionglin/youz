package com.example.auth.demo.common;

import com.sm.utils.PayUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;

public class PayTest {
    @Test
    public void signTest(){
        System.out.println("1- " +StringUtils.substring("1234567890", 0, 8));
        System.out.println("2- " +StringUtils.substring("1234567890", 8));

        System.out.println("3- " +StringUtils.substring("1234", 0, 8));
        System.out.println("4- " + StringUtils.substring("1234", 8));

    }
}
