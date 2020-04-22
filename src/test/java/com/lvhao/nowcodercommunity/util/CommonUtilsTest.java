package com.lvhao.nowcodercommunity.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Test
    void getJsonString() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", "18");
        System.out.println(CommonUtils.getJsonString(200, "OK", map));
    }

    @Test
    void test() {
        System.out.println((A) null);
    }
}

class A {

}