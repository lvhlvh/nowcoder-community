package com.lvhao.nowcodercommunity.controller;

import com.google.code.kaptcha.Producer;
import com.lvhao.nowcodercommunity.annotation.LoginRequired;
import com.lvhao.nowcodercommunity.entity.User;
import com.lvhao.nowcodercommunity.form.UserLoginForm;
import com.lvhao.nowcodercommunity.form.UserRegForm;
import com.lvhao.nowcodercommunity.service.UserService;
import com.lvhao.nowcodercommunity.constant.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/login")
    public String getLoginPage() {
        return "site/login";
    }

    @PostMapping("/login")
    public String login(@Valid UserLoginForm userLoginForm,
                        BindingResult bindingResult,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model) {
        // 表单参数验证失败
        if (bindingResult.hasErrors()) {
            model.addAttribute("usernameMsg", bindingResult.getFieldError("username"));
            model.addAttribute("passwordMsg", bindingResult.getFieldError("password"));
            model.addAttribute("codeMsg", bindingResult.getFieldError("code"));
            log.error("表单参数有误: {}", bindingResult.getFieldErrors());
            return "site/login";
        }

        // 1. 检查验证码
        String kaptchaInSession = (String) session.getAttribute(UserConstants.KAPTCHA_IN_SESSION);
        if (StringUtils.isBlank(kaptchaInSession) || !kaptchaInSession.equalsIgnoreCase(userLoginForm.getCode())) {
            model.addAttribute("codeMsg", UserConstants.WRONG_ACTIVATION_CODE);
            return "/site/login";
        }

        // 2. 验证码OK, 尝试登录
        int expiredSeconds = userLoginForm.getRememberme() ? UserConstants.REMEMBER_EXPIRED_SECONDS
                : UserConstants.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> loginResultMap = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword(), expiredSeconds);
        if (loginResultMap.containsKey("ticket")) { // login success
            // 2.1 将ticket存放在Cookie中发送给客户端
            Cookie cookie = new Cookie("ticket", (String) loginResultMap.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            // 2.2 将user对象存储到session里面
            session.setAttribute(UserConstants.USER_IN_SESSION, userService.getUserById((Integer) loginResultMap.get("userId")));
            return "redirect:/index";
        } else { // login fail
            model.addAttribute("usernameMsg", loginResultMap.get("usernameMsg"));
            model.addAttribute("passwordMsg", loginResultMap.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket,
                         HttpSession session) { // 从Cookie中取值
        userService.logout(ticket);
        session.removeAttribute(UserConstants.USER_IN_SESSION);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "site/register";
    }

    @PostMapping("/register")
    public String getRegisterPage(@Valid UserRegForm userRegForm,
                                  BindingResult bindingResult,
                                  Model model) {
        log.info("getRegisterPage() start");
        // 表单参数验证失败
        if (bindingResult.hasErrors()) {
            model.addAttribute("usernameMsg", bindingResult.getFieldError("username"));
            model.addAttribute("passwordMsg", bindingResult.getFieldError("password"));
            model.addAttribute("emailMsg", bindingResult.getFieldError("email"));
            log.error("表单参数有误: {}", bindingResult.getFieldErrors());
            return "site/register";
        }

        User user = new User();
        BeanUtils.copyProperties(userRegForm, user);
        Map<String, String> registerResultMap = userService.register(user);
        if (registerResultMap == null || registerResultMap.isEmpty()) {
            model.addAttribute("msg", UserConstants.REG_SUCCESS);
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", registerResultMap.get("usernameMsg"));
            model.addAttribute("passwordMsg", registerResultMap.get("passwordMsg"));
            model.addAttribute("emailMsg", registerResultMap.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activate(@PathVariable("userId") int userId,
                           @PathVariable("code") String activationCode,
                           Model model) {
        int activateStatus = userService.activate(userId, activationCode);
        String msg = "";
        String target = "";
        switch (activateStatus) {
            case UserConstants.ACTIVATION_SUCCESS:
                msg = UserConstants.ACTIVATION_SUCCESS_MSG;
                target = "/login";
                break;
            case UserConstants.ACTIVATION_REPEAT:
                msg = UserConstants.ACTIVATION_REPEAT_MSG;
                target = "/index";
                break;
            case UserConstants.ACTIVATION_FAILURE:
                msg = UserConstants.ACTIVATION_FAILURE_MSG;
                target = "/index";
                break;
        }
        model.addAttribute("msg", msg);
        model.addAttribute("target", target);

        return "/site/operate-result";
    }

    @LoginRequired
    @GetMapping("/user/setting")
    public String getSettingPage() {
        return "site/setting";
    }

    /**
     * 生成验证码图片作为响应内容返回，同时将验证码对应的文本存储到session
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletRequest request,
                           HttpServletResponse response,
                           HttpSession session) {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        session.setAttribute(UserConstants.KAPTCHA_IN_SESSION, text);
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            log.error("生成验证码出错: {}", e.getMessage());
        }
    }
}
