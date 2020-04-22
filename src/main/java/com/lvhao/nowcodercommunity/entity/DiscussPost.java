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
public class DiscussPost {
    private Integer id;

    private Integer userId;

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    private Date createTime;

    private Integer commentCount;

    private Double score;
}