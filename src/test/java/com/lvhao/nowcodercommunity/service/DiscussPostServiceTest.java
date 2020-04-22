package com.lvhao.nowcodercommunity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class DiscussPostServiceTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    void getDiscussPostByUserIdOnePage() {
        List<Map<String, Object>> posts = discussPostService.getDiscussPostByUserIdOnePage(0, 0, 10);
        posts.forEach(post -> {
            System.out.println(post.get("post"));
            System.out.println(post.get("user"));
        });
    }

    @Test
    void getDiscussPostCountsByUserId() {
    }
}