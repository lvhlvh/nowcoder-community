package com.lvhao.nowcodercommunity.dao;

import com.lvhao.nowcodercommunity.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    User selectByUsername(String username);

    User selectByEmail(String email);

    int updateStatusByPrimaryKey(int userId, int status);
}