package com.lvhao.nowcodercommunity.controller;

import com.lvhao.nowcodercommunity.constant.CommentConstants;
import com.lvhao.nowcodercommunity.constant.DiscussPostConstants;
import com.lvhao.nowcodercommunity.constant.UserConstants;
import com.lvhao.nowcodercommunity.entity.Comment;
import com.lvhao.nowcodercommunity.entity.DiscussPost;
import com.lvhao.nowcodercommunity.entity.Page;
import com.lvhao.nowcodercommunity.entity.User;
import com.lvhao.nowcodercommunity.service.CommentService;
import com.lvhao.nowcodercommunity.service.DiscussPostService;
import com.lvhao.nowcodercommunity.service.UserService;
import com.lvhao.nowcodercommunity.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
@Slf4j
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content,
                                 HttpSession session) {
        User user = (User) session.getAttribute(UserConstants.USER_IN_SESSION);
        // 未登录
        if (user == null) {
            return CommonUtils.getJsonString(403, UserConstants.ACCOUNT_NOT_LOGIN);
        }
        // title为空
        if (StringUtils.isBlank(title)) {
            return CommonUtils.getJsonString(403, DiscussPostConstants.EMPTY_TITLE);
        }

        DiscussPost discussPost = DiscussPost.builder()
                .userId(user.getId())
                .title(title)
                .content(content)
                .build();
        discussPostService.addDiscussPost(discussPost);

        // TODO: 报错的情况将来统一处理

        return CommonUtils.getJsonString(0, DiscussPostConstants.ADD_SUCCESS);
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPostById(@PathVariable("discussPostId") int discussPostId,
                                     Model model, Page page) {
        // post
        DiscussPost discussPost = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        // user
        User user = userService.getUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // comment
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());

        List<Comment> commentList =
                commentService.getCommentsByEntityOnePage(CommentConstants.ENTITY_TYPE_POST,
                        discussPost.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("commentContent", comment);
                commentVo.put("user", userService.getUserById(comment.getUserId()));
                // 针对该评论的回复(评论)
                List<Comment> replyList =
                        commentService.getCommentsByEntityOnePage(CommentConstants.ENTITY_TYPE_COMMENT,
                                comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("replyContent", reply);
                        replyVo.put("user", userService.getUserById(reply.getUserId()));

                        // 评论有两类，一类是: "user: 评论"; 另一类是"user2 回复 user1: 评论"
                        // target代表后面那种类型中的user1
                        User target = reply.getTargetId() == null ? null :
                                userService.getUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        log.info("target: {}", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replayCount = commentService.getCommentCountByEntity(CommentConstants.ENTITY_TYPE_COMMENT,
                        comment.getId());
                commentVo.put("replyCount", replayCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }


}
