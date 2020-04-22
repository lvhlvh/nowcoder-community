package com.lvhao.nowcodercommunity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer id;

    private String username;

    private String password;

    private String salt;

    private String email;

    private Integer type;

    private Integer status;

    private String activationCode;

    private String headerUrl;

    private Date createTime;

}