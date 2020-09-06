package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DiscussPost record);

    int insertSelective(DiscussPost record);

    DiscussPost selectByPrimaryKey(Integer id);

    List<DiscussPost> selectByUserIdOnePage(Integer userId, Integer offset, Integer limit);

    int selectDiscussPostCountByUserId(Integer userId);

    int updateByPrimaryKeySelective(DiscussPost record);

    int updateByPrimaryKey(DiscussPost record);

    int updateCommentCount(int id, int newCommentCount);

    int selectDiscussPostCount();

    List<DiscussPost> selectOnePage(Integer offset, Integer limit);
}