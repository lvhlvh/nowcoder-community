## 用于封装分页信息的Page类

Page类封装了**当前页码**、**每页记录数**、**总记录数**、**页数**、**查询路径**、**前端UI起始页码**、**前端UI终止页码**。

其中：

- 通过**前端传入参数指定**的有：当前页码、每页记录数(不指定默认为10)、查询路径
- 从**数据库查询得到**的有：总记录数
- **后端计算得到**的有：页数、前端UI起始页码、前端UI终止页码

```java
public class Page {
    // 当前页码
    private int current = 1;
    // 每页的记录数
    private int limit = 10;
    // 记录总数
    private int rows;
    // 查询路径(用于复用分页查询)
    private String path;

    public int getCurrent() { return current;}

    public void setCurrent(int current) {
        if (current > 0) { this.current = current;}
    }

    public int getLimit() { return limit;}

    public void setLimit(int limit) {
        if (limit > 0 && limit <= 100) { this.limit = limit;}
    }

    public int getRows() { return rows; }

    public void setRows(int rows) {
        if (rows >= 0) { this.rows = rows;}
    }

    public String getPath() { return path;}

    public void setPath(String path) { this.path = path;}

    /**
     * 获取当前页的起始行
     */
    public int getOffset() { return (current - 1) * limit;}

    /**
     * 获取总页数
     */
    public int getTotal() { return rows % limit == 0 ? rows / limit : rows / limit + 1;}

    /**
     * 获取当前展示的起始页码
     * <p>...5 6 7 8 9..., 则起始页码为5
     */
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * 获取当前展示的终止页码
     * <p>...5 6 7 8 9..., 则终止页码为9
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
```

## 前端的分页实现

前端结合Thymeleaf模板引擎实现了分页逻辑，代码如下：

```html
<!-- 分页 -->
<nav class="mt-5" th:if="${page.rows>0}" th:fragment="pagination">
    <ul class="pagination justify-content-center">
        <li class="page-item">
            <a class="page-link" th:href="@{${page.path}(current=1)}">首页</a>
        </li>
        <li th:class="|page-item ${page.current==1?'disabled':''}|">
            <a class="page-link" th:href="@{${page.path}(current=${page.current-1})}">上一页</a></li>
        <li th:class="|page-item ${i==page.current?'active':''}|" th:each="i:${#numbers.sequence(page.from,page.to)}">
            <a class="page-link" th:href="@{${page.path}(current=${i})}" th:text="${i}">1</a>
        </li>
        <li th:class="|page-item ${page.current==page.total?'disabled':''}|">
            <a class="page-link" th:href="@{${page.path}(current=${page.current+1})}">下一页</a>
        </li>
        <li class="page-item">
            <a class="page-link" th:href="@{${page.path}(current=${page.total})}">末页</a>
        </li>
    </ul>
</nav>
```

实现的UI如下：

<img src="https://raw.githubusercontent.com/lvhlvh/pictures/master/img/20200905231203.png" style="zoom:80%;" />

上述代码根据后端传入的Page对象获取**起始页码**(page.from)、**终止页码**(page.to)、**末页页码**等信息。

当点击页码按钮时，会以`http://127.0.0.1:8080/community/index?current=页码`形式的url访问，后端获取url参数中的页码，进而取到指定页的记录。

## 后端的分页实现

```java
@GetMapping({"/", "/index"})
public String getIndexPage(Model model, Page page) {
    // getIndexPage()方法被调用前，Spring MVC会自动实例化Model和Page, 并将Page注入Model,
    // 所以, 下面无需我们显示地执行model.addAttribute(..., page)
    
    // 1. 首先，获取帖子表总记录数，总记录数用来计算
    page.setRows(discussPostService.getDiscussPostCount());
    page.setPath("/index");

    List<Map<String, Object>> posts = discussPostService.getDiscussPostOnePage(page.getOffset(), page.getLimit());
    model.addAttribute("posts", posts);
    return "index";
}
```

