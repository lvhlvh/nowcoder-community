package com.lvhao.nowcodercommunity.util;

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
class MailClientTest {

    @Autowired
    private MailClient mailClient;

    @Test
    void sendMail() {
        mailClient.sendMail("1205728818@qq.com",
                "你好",
                "你好");
    }


    @Test
    void generateActivationEmail() {
        User user = User.builder()
                .id(1)
                .username("tom")
                .email("1205728818@qq.com")
                .activationCode(UUID.randomUUID().toString())
                .build();
        System.out.println(mailClient.generateActivationEmail(user));
    }
}