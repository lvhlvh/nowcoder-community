package com.lvhao.nowcodercommunity.interceptor;

import com.lvhao.nowcodercommunity.annotation.LoginRequired;
import com.lvhao.nowcodercommunity.constant.UserConstants;
import com.lvhao.nowcodercommunity.entity.LoginTicket;
import com.lvhao.nowcodercommunity.entity.User;
import com.lvhao.nowcodercommunity.service.UserService;
import com.lvhao.nowcodercommunity.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    /**
     * 在Controller之前执行
     * <p>
     * true, 放行; false, 中断
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle: {}", handler.toString());

        // 每次拦截都判断ticket, ticket有效更新user到session
        String ticket = CookieUtils.getCookieValue(request, "ticket");
        if (ticket != null) {
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            // 2. 检测ticket是否有效
            if (loginTicket != null
                    && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {
                // 3. ticket有效, (检查session中有没有user对象，如果没有设置一下，供后续使用)
                HttpSession session = request.getSession();
                if (session.getAttribute(UserConstants.USER_IN_SESSION) == null) {
                    session.setAttribute(UserConstants.USER_IN_SESSION,
                            userService.getUserById(loginTicket.getUserId()));
                }
            }
        }

        // 如果前面的ticket有效的话，此时session中必定存在user对象了
        // 需要拦截的方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 需要登录但是ticket已经失效
            if (loginRequired != null && request.getSession().getAttribute(UserConstants.USER_IN_SESSION) == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        return true;
    }

    /**
     * Controller之后, 渲染View之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) {
        log.info("postHandle: {}", handler.toString());

        User user = (User) request.getSession().getAttribute(UserConstants.USER_IN_SESSION);
        if (user != null && modelAndView != null) {
            modelAndView.addObject(UserConstants.USER_IN_MV, user);
        }
    }
}
