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
public class LoginTicket {
    private Integer id;

    private Integer userId;

    private String ticket;

    private Date expired;

    private Integer status;
}