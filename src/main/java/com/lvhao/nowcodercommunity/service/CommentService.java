package com.lvhao.nowcodercommunity.service;

import com.lvhao.nowcodercommunity.constant.CommentConstants;
import com.lvhao.nowcodercommunity.dao.CommentMapper;
import com.lvhao.nowcodercommunity.dao.DiscussPostMapper;
import com.lvhao.nowcodercommunity.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<Comment> getCommentsByEntityOnePage(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntityOnePage(entityType, entityId, offset, limit);
    }

    public int getCommentCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // Step 1和Step2要么都做，要么都不做

        // Step 1: 往comment表插入数据
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        int rows = commentMapper.insert(comment);

        // Step 2: 更新discuss_post表的评论数量字段
        // 如果是帖子类型，需要更新帖子评论数量字段
        if (comment.getEntityType() == CommentConstants.ENTITY_TYPE_POST) {
            discussPostMapper.updateCommentCount(comment.getEntityId(),
                    commentMapper.selectCountByEntity(CommentConstants.ENTITY_TYPE_POST, comment.getEntityId()));
        }

        return rows;
    }
}
