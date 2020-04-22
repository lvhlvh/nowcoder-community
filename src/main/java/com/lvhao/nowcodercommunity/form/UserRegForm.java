package com.lvhao.nowcodercommunity.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserRegForm {
    @NotBlank(message = "用户名不能为空!")
    private String username;
    @NotBlank(message = "密码不能为空!")
    private String password;
    @Email(message = "email地址非法!")
    private String email;
}
