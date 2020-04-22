package com.lvhao.nowcodercommunity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TestServiceTest {

    @Autowired
    private TestService testService;

    @Test
    void save() {
        testService.save();
    }
}