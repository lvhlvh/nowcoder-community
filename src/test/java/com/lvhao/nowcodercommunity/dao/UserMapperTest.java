package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    void insert() {
        User user = User.builder()
                .username("tom")
                .email("1205728818@qq.com")
                .activationCode(UUID.randomUUID().toString())
                .build();
        userMapper.insert(user);
        System.out.println(user.getId());
    }
}