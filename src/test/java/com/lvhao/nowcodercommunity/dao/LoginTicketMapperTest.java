package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.LoginTicket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith({SpringExtension.class})
class LoginTicketMapperTest {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void deleteByPrimaryKey() {
    }

    @Test
    void insert() {
        LoginTicket loginTicket = LoginTicket.builder()
                .userId(130)
                .ticket("abcde")
                .status(1)
                .expired(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .build();
        loginTicketMapper.insert(loginTicket);
        Assertions.assertEquals(1, loginTicket.getId());
    }

    @Test
    void insertSeUlective() {
    }

    @Test
    void selectByPrimaryKey() {
    }

    @Test
    void selectByTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abcde");
        System.out.println(loginTicket);
    }

    @Test
    void updateByPrimaryKeySelective() {
    }

    @Test
    void updateByPrimaryKey() {
    }

    @Test
    void updateStatusByTicket() {
        loginTicketMapper.updateStatusByTicket("abcde", 0);
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abcde");
        System.out.println(loginTicket);
        Assertions.assertEquals(0, loginTicket.getStatus());
    }
}