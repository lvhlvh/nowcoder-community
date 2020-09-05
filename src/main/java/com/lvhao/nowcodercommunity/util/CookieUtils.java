package com.lvhao.nowcodercommunity.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class CookieUtils {

    public static String getCookieValue(HttpServletRequest request,
                                        String name) {
        if (request == null || name == null) {
            log.error("getCookieValue(): {}", "参数为空");
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;

    }
}
