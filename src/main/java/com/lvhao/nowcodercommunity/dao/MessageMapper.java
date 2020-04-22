package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);
}