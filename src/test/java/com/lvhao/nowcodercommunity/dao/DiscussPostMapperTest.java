package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.DiscussPost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @DisplayName("selectByUserIdOnePage()")
    @Test
    void selectByUserIdOnePage() {
        List<DiscussPost> discussPosts = discussPostMapper.selectByUserIdOnePage(0, 0, 10);
        discussPosts.forEach(System.out::println);
    }

    @DisplayName("selectDiscussPostCountsByUserId()")
    @Test
    void selectDiscussPostCountsByUserId() {
        int postCount = discussPostMapper.selectDiscussPostCountByUserId(0);
        System.out.println(postCount);
    }
}