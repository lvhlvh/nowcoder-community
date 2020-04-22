package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    List<Comment> selectCommentsByEntityOnePage(Integer entityType, Integer entityId, Integer offset, Integer limit);

    int selectCountByEntity(Integer entityType, Integer entityId);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);
}