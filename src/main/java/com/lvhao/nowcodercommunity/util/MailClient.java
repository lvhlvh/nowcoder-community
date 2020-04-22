package com.lvhao.nowcodercommunity.util;

import com.lvhao.nowcodercommunity.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class MailClient {

    /* Subjects of Mail */
    public static final String ACTIVATION = "激活邮件";

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ITemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 发送邮件
     *
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送邮件失败: {}", e.getMessage());
        }
    }

    public String generateActivationEmail(User user) {
        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/activation/101/code
        String url = domain + CommonUtils.cleanUrlFragment(contextPath)
                + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        return templateEngine.process("/mail/activation", context);
    }
}
