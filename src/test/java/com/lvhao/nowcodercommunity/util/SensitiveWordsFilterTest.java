package com.lvhao.nowcodercommunity.util;

import org.junit.jupiter.api.Test;

class SensitiveWordsFilterTest {

    @Test
    void filterSensitiveWords() {
        SensitiveWordsFilter filter = new SensitiveWordsFilter();
        filter.init();
        System.out.println(filter.filterSensitiveWords("￥￥￥嫖娼的玩意儿, 傻@@@逼我@@操$$*你妈我操"));
    }
}