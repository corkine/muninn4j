package com.mazhangjing.muninn.v2.service;

import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final String CURRENT_VERSION = "2.9.12B";

    private final String LOG =
            "版本 2.9.12 添加了公共奖章 API，添加了 Tomcat 跨域访问。" +
            "版本 2.9.11 解决了 Docker TOMCAT 的 JVM 时区设置问题。修正了主页的一个日期展示的错误。降低了未获得勋章的透明度。" +
                    "修正了'精益求精'奖章的计算规则。修正了三级标题造成章节目录显示嵌套的问题。\n" +
            "版本 2.9.10 添加了版本和日志记录服务，添加关于页面。解决了数据库连接的时区配置问题。增加了对主菜单条目的排序。实现了了奖励统计系统。\n" +
            "版本 2.9.2 服务器最终释出版本。\n" +
            "版本 2.9.1 服务器测试版本。解决了 Docker TOMCAT UTF-8 编码问题，以及根目录问题。\n" +
            "版本 2.9.0 服务器测试版本。解决了 Docker TOMCAT 对于 Java 8 的 JSP 解析支持问题。\n" +
            "版本 2.8.1 服务器测试版本，使用 Docker 虚拟化 Tomcat。\n" +
            "版本 2.8.0 服务器测试版本。项目部署在腾讯云，使用了腾讯的关系型数据库产品提供数据支持。\n" +
            "版本 2.7.0 服务器最终释出版本。项目部署在天翼云，使用了腾讯的关系型数据库产品提供数据支持。\n" +
            "版本 2.0.0 改用 Java + JSP + Servlet + Spring + Spring MVC + Hibernate + MySQL 实现 Muninn 项目，抛弃原来基于 Python 的静态实现，更新速度由 10分钟提高到 1分钟。\n" +
            "版本 1.0.0 使用 Python 静态解析 Jupyter Notebook 内容，生成 HTML 托管在 Github 上，国内使用 CDN 进行加速。\n";

    public String getLog() {
        return LOG.replace("\n","<br/>");
    }

    public String getVersion() {
        return CURRENT_VERSION;
    }

}
