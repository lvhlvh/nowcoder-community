package com.lvhao.nowcodercommunity.controller;

import com.lvhao.nowcodercommunity.annotation.LoginRequired;
import com.lvhao.nowcodercommunity.constant.CommentConstants;
import com.lvhao.nowcodercommunity.constant.UserConstants;
import com.lvhao.nowcodercommunity.entity.Comment;
import com.lvhao.nowcodercommunity.entity.User;
import com.lvhao.nowcodercommunity.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 回帖 (comment, not reply)
     * <p>
     * 传入discussPostId是为了提交后能刷新/重定向页面
     */
    @PostMapping("/add/{discussPostId}")
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId,
                             Comment comment,
                             HttpSession session) {
        User user = (User) session.getAttribute(UserConstants.USER_IN_SESSION);
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
