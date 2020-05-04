package com.lvhao.nowcodercommunity.service;

import com.lvhao.nowcodercommunity.dao.DiscussPostMapper;
import com.lvhao.nowcodercommunity.dao.UserMapper;
import com.lvhao.nowcodercommunity.entity.DiscussPost;
import com.lvhao.nowcodercommunity.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    public List<Map<String, Object>> getDiscussPostByUserIdOnePage(int userId, int offset, int limit) {
        List<DiscussPost> discussPosts = discussPostMapper.selectByUserIdOnePage(userId, offset, limit);
        List<Map<String, Object>> discussPostsWithUser = new ArrayList<>();
        if (discussPosts != null) {
            discussPosts.forEach(discussPost -> {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                map.put("user", userMapper.selectByPrimaryKey(discussPost.getUserId()));
                discussPostsWithUser.add(map);
            });
        }
        return discussPostsWithUser;
    }

    public int getDiscussPostCountByUserId(int userId) {
        return discussPostMapper.selectDiscussPostCountByUserId(userId);
    }

    public void addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("discussPost不能为空!");
        }

        discussPost.setTitle(sensitiveWordsFilter.filterSensitiveWords(HtmlUtils.htmlEscape(discussPost.getTitle())));
        discussPost.setContent(sensitiveWordsFilter.filterSensitiveWords(HtmlUtils.htmlEscape(discussPost.getContent())));
        discussPost.setType(0); // 普通
        discussPost.setStatus(0); // 正常
        discussPost.setCreateTime(new Date());
        discussPost.setCommentCount(0);
        discussPost.setScore(0.0);

        discussPostMapper.insertSelective(discussPost);
    }

    public DiscussPost getDiscussPostById(int discussPostId) {
        return discussPostMapper.selectByPrimaryKey(discussPostId);
    }

    public int updateCommentCount(int id, int newCommentCount) {
        return discussPostMapper.updateCommentCount(id, newCommentCount);
    }
}
