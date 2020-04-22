package com.lvhao.nowcodercommunity.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserLoginForm {
    @NotBlank(message = "用户名不能为空!")
    private String username;

    @NotBlank(message = "密码不能为空!")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String code;

    private Boolean rememberme;

    public Boolean getRememberme() {
        return rememberme == null ? false : rememberme;
    }
}
