package com.lvhao.nowcodercommunity.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SensitiveWordsFilter {

    private TrieNode root;

    private static final String DEFAULT_REPLACEMENT = "***";

    public SensitiveWordsFilter() {
        root = new TrieNode();
    }

    private class TrieNode {
        private boolean isWordEnd;
        private Map<Character, TrieNode> next;

        private TrieNode() {
            isWordEnd = false;
            next = new HashMap<>();
        }
    }


    /**
     * 加载文件中的敏感词, 只会被执行一次
     */
    @PostConstruct
    public void init() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
        if (in == null) {
            log.error("加载敏感词文件失败");
            return;
        }

        try {
            BufferedReader bw = new BufferedReader(new InputStreamReader(in));
            String word;
            while ((word = bw.readLine()) != null) {
                addWord(word);
            }
            in.close();
            bw.close();
        } catch (IOException e) {
            log.error("读取敏感词文件出错: {}", e.getMessage());
        }
    }

    /**
     * 往Trie中添加敏感词
     *
     * @return true, if 敏感词不存在; false, if 敏感词已经存在
     */
    private boolean addWord(String word) {
        TrieNode cur = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (cur.next.get(ch) == null) {
                cur.next.put(ch, new TrieNode());
            }
            cur = cur.next.get(ch);
        }

        if (!cur.isWordEnd) {
            cur.isWordEnd = true;
            return true;
        }

        return false;
    }


    public String filterSensitiveWords(String text) {
        return filterSensitiveWords(text, DEFAULT_REPLACEMENT);
    }

    /**
     * 过滤敏感词, 将text中的文本替换为replacement
     *
     * @param text 过滤前的文本
     * @param  replacement 替换敏感词的文本
     * @return 过滤后的文本
     */
    public String filterSensitiveWords(String text, String replacement) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        StringBuilder sb = new StringBuilder();
        TrieNode cur = root;
        int begin = 0;
        int end = 0;

        while (end < text.length()) {
            char ch = text.charAt(end);

            // 跳过特殊符号
            if (isSymbol(ch)) {
                // cur==root, 说明begin==end, 匹配还没开始,
                // 应该跳过开头的特殊字符
                if (cur == root) {
                    sb.append(ch);
                    begin++;
                }
                // cur!=root, 说明begin!=end, 已经匹配了至少
                // 一个字符, 应该跳过中间的特殊字符继续匹配
                end++;
                continue;
            }

            // 检查下级节点
            // 1. 下级节点是null
            cur = cur.next.get(ch);
            if (cur == null) {
                sb.append(text.charAt(begin));
                begin++;
                end = begin;
                cur = root;
            }
            // 2. 下级节点不是null 且 是wordEnd
            else if (cur.isWordEnd) {
                sb.append(replacement);
                end++;
                begin = end;
                cur = root;
            } else {
                end++;
            }
        }

        // 末尾可能存在和敏感词部分匹配的情况
        if (begin < text.length()) {
            sb.append(text.substring(begin));
        }

        return sb.toString();
    }

    /**
     * 判断ch是不是特殊符号
     *
     * @param ch 待判断字符
     * @return true, ch是特殊符号; false, ch不是特殊符号
     */
    private boolean isSymbol(char ch) {
        return !CharUtils.isAsciiAlphanumeric(ch) && // ch不是ASCII中的数字或字符
                (ch < 0x2E80 || ch > 0x9FFF); // ch不是东亚文字
    }
}
