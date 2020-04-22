package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTicketMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoginTicket record);

    int insertSelective(LoginTicket record);

    LoginTicket selectByPrimaryKey(Integer id);

    LoginTicket selectByTicket(String ticket);

    int updateByPrimaryKeySelective(LoginTicket record);

    int updateByPrimaryKey(LoginTicket record);

    int updateStatusByTicket(String ticket, Integer status);
}