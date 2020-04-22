package com.lvhao.nowcodercommunity.service;

import com.lvhao.nowcodercommunity.dao.DiscussPostMapper;
import com.lvhao.nowcodercommunity.dao.UserMapper;
import com.lvhao.nowcodercommunity.entity.DiscussPost;
import com.lvhao.nowcodercommunity.entity.User;
import com.lvhao.nowcodercommunity.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TestService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String save() {
        String salt = CommonUtils.generateRandomString().substring(0, 5);
        User user = User.builder()
                .username("test-transaction")
                .salt(salt)
                .password(CommonUtils.md5("12345" + salt))
                .email("test-transaction@qq.com")
                .createTime(new Date())
                .build();
        userMapper.insertSelective(user);

        DiscussPost discussPost = DiscussPost.builder()
                .userId(user.getId())
                .title("新人报道: " + user.getUsername())
                .content("新人报道")
                .createTime(new Date())
                .build();
        discussPostMapper.insertSelective(discussPost);

        // 模拟事务执行过程发生异常
        int a = 1 / 0;

        return "OK";
    }
}
