package com.lvhao.nowcodercommunity.service;

import com.lvhao.nowcodercommunity.dao.LoginTicketMapper;
import com.lvhao.nowcodercommunity.dao.UserMapper;
import com.lvhao.nowcodercommunity.entity.LoginTicket;
import com.lvhao.nowcodercommunity.entity.User;
import com.lvhao.nowcodercommunity.util.CommonUtils;
import com.lvhao.nowcodercommunity.util.MailClient;
import com.lvhao.nowcodercommunity.constant.UserConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;


    public User getUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public Map<String, Object> login(String username,
                                     String password,
                                     int expiredSeconds) {
        Map<String, Object> result = new HashMap<>();

        // 1. 账号是否存在
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            result.put("usernameMsg", UserConstants.ACCOUNT_NOT_EXISTS);
            return result;
        }
        // 2. 账号状态是否激活
        if (user.getStatus() == 0) {
            result.put("usernameMsg", UserConstants.ACCOUNT_NOT_ACTIVATED);
            return result;
        }
        // 3. 验证密码
        if (!user.getPassword().equals(CommonUtils.md5(password + user.getSalt()))) {
            result.put("passwordMsg", UserConstants.WRONG_PASSWORD);
            return result;
        }

        // 验证成功，生成登录凭证
        LoginTicket loginTicket = LoginTicket.builder()
                .userId(user.getId())
                .ticket(CommonUtils.generateRandomString())
                .status(0)
                .expired(new Date(System.currentTimeMillis() + expiredSeconds * 1000))
                .build();
        loginTicketMapper.insert(loginTicket);

        result.put("ticket", loginTicket.getTicket());
        result.put("userId", loginTicket.getUserId());
        return result;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatusByTicket(ticket, 1);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Map<String, String> register(User user) {
        Map<String, String> result = new HashMap<>();

        User u = userMapper.selectByUsername(user.getUsername());
        if (u != null) {
            result.put("usernameMsg", UserConstants.ACCOUNT_EXISTS);
            return result;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            result.put("emailMsg", UserConstants.EMAIL_EXISTS);
            return result;
        }

        final String salt = CommonUtils.generateRandomString().substring(0, 5);
        User userToBeInserted = User.builder()
                .username(user.getUsername())
                .salt(salt)
                .email(user.getEmail())
                .password(CommonUtils.md5(user.getPassword() + salt))
                .type(0)
                .status(0)
                .activationCode(CommonUtils.generateRandomString())
                .headerUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)))
                .createTime(new Date())
                .build();
        userMapper.insert(userToBeInserted);
        mailClient.sendMail(userToBeInserted.getEmail(),
                MailClient.ACTIVATION,
                mailClient.generateActivationEmail(userToBeInserted));

        return result;
    }

    public int activate(int userId, String activationCode) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user.getStatus() == 1) {
            return UserConstants.ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateStatusByPrimaryKey(userId, 1);
            return UserConstants.ACTIVATION_SUCCESS;
        } else {
            return UserConstants.ACTIVATION_FAILURE;
        }
    }

    public LoginTicket getLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }
}
