package com.lvhao.nowcodercommunity.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppConstants {

    public static final List<String> LOGIN_INTERCEPT_URL_LIST
            = Collections.singletonList("/**");

    public static final List<String> LOGIN_NO_INTERCEPT_URL_LIST
            = Arrays.asList("/**/*.css", "/**/*.htm*", "/**/*.js",
            "/**/*.png", "/**/*.jpg", "/**/*.jpeg",
            "/kaptcha*");
}
