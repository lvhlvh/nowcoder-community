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
public class Message {
    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String conversationId;

    private String content;

    private Integer status;

    private Date createTime;
}