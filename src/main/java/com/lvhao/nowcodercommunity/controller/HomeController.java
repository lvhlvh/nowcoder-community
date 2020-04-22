package com.lvhao.nowcodercommunity.controller;

import com.lvhao.nowcodercommunity.entity.Page;
import com.lvhao.nowcodercommunity.service.DiscussPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping({"/", "/index"})
    public String getIndexPage(Model model, Page page) {
        // getIndexPage()方法被调用前，Spring MVC会自动实例化Model和Page, 并将Page注入Model,
        // 所以, 下面无需我们显示地执行model.addAttribute(..., page)
        page.setRows(discussPostService.getDiscussPostCountByUserId(0));
        page.setPath("/index");

        List<Map<String, Object>> posts = discussPostService.getDiscussPostByUserIdOnePage(0, page.getOffset(), page.getLimit());
        model.addAttribute("posts", posts);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }
}
