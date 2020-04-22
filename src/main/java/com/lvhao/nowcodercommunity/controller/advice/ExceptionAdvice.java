package com.lvhao.nowcodercommunity.controller.advice;

import com.lvhao.nowcodercommunity.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        log.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            log.error(stackTraceElement.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = null;
            writer = response.getWriter();
            writer.write(CommonUtils.getJsonString(1, "服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
