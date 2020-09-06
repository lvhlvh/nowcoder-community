# 仿牛客网的在线社区项目

## 项目概述

基于Spring Boot + MyBatis + MySQL的在线社区项目

- 完成了用户登录注册模块, 使用拦截器实现了统一的登录状态验证，期间学习到了**Cookie、Session、JWT**等身份认证相关的技术
- 对于首页帖子的分页展示功能做了**SQL语句层面的优化**，缩短了查询时间，期间加深了对**limit语句原理**、**explain命令**、**子查询**、**join算法**等概念的理解
- 使用JSR-303中的注解实现了参数验证，基于**AOP**实现了统一的日志记录和异常管理

## 项目要点

- [首页帖子分页的实现](pagination-implementation.md)
- [首页帖子分页的优化](pagination-optimization.md)