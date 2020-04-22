package com.lvhao.nowcodercommunity.util;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonUtils {

    /**
     * 生成随机字符串
     *
     * @return 随机字符串
     */
    public static String generateRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 返回字符串str的md5值
     *
     * @param str 字符串
     * @return str!=null, 字符串str的md5值; str==null或为空串, null
     */
    public static String md5(String str) {
        return StringUtils.isBlank(str) ? null
                : DigestUtils.md5DigestAsHex(str.getBytes());
    }

    public static String cleanUrlFragment(String urlFragment) {
        if (urlFragment.equals("/")) {
            return "";
        }

        return urlFragment.endsWith("/") ? urlFragment.substring(0, urlFragment.length() - 1)
                : urlFragment;
    }

    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        Gson gson = new Gson();
        Map<String, Object> m = new HashMap<>();
        m.put("code", code);
        if (msg != null) {
            m.put("msg", msg);
        }
        if (map != null && map.size() != 0) {
            m.putAll(map);
        }

        return gson.toJson(m);
    }

    public static String getJsonString(int code, String msg) {
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code) {
        return getJsonString(code, null, null);
    }
}
