<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh_cn">
<head>
    <meta charset="UTF-8">
    <title>${requestScope.chapter.title} - 课程和笔记</title>
    <link rel="stylesheet" href="/css/bootstrap.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</head>
<body>
<%--一级标题--%>
<nav class="navbar navbar-expand navbar-dark bg-dark">
    <a class="navbar-brand" href="/">MUNINN <small>by Corkine Ma</small></a>
    <div class="navbar-collapse collapse justify-content-between">
        <ul class="navbar-nav">
            <c:forEach items="${requestScope.header}" var="cate" varStatus="s">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="${cate.key.hashCode()}" data-toggle="dropdown">${cate.key}</a>
                    <div class="dropdown-menu" aria-labelledby="${cate.key.hashCode()}">
                        <c:forEach items="${cate.value}" var="vol" varStatus="vols">
                            <a class="dropdown-item" href="/volume/${vol.id}">${vol.title}<small></small></a>
                        </c:forEach>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="collapse navbar-collapse justify-content-end"></div>
</nav>
<%--二级标题--%>
<nav class="nav nav-tabs bg-light">
    <li class="nav-item">
        <small><a class="nav-link">${requestScope.volume.title}</a></small>
    </li>
    <li class="nav-item">
        <small><a class="nav-link active" href="#">Overview</a></small>
    </li>
    <li class="nav-item">
        <small><a class="nav-link" href="#">Course Info</a></small>
    </li>
    <li class="nav-item">
        <small><a class="nav-link disabled" href="#">Notebook</a></small>
    </li>
</nav>
<div class="container mt-5 ml-auto">
    <div class="row">
        <!--左侧-->
        <div class="col-md-4">
            <!--章节名称-->
            <div class="list-group list-group-flush">
                <c:forEach items="${requestScope.volume.chapters}" var="chapter">
                    <a href="/volumn/${chapter.volume.id}/chapter/${chapter.id}" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                            ${chapter.title}<span class="badge badge-primary badge-pill">${chapter.version}</span></a>
                </c:forEach>
            </div>
            <!--章节详细信息-->
            <div class="card mt-4">
                <ul class="list-group">
                    <li class="list-group-item list-group-item-light">章节目录</li>
                    ${requestScope.chapter.content}
                </ul>
            </div>
        </div>
        <!--右侧-->
        <div class="col-md-8 pl-3">
            <!--本章概览-->
            <div class="card">
                <div class="card-header">本章概览</div>
                <div class="card-body">
                    ${requestScope.chapter.intro}
                    <p class="card-text">
                        <small class="text-muted">最后更新于 ${requestScope.chapter.lastEdit} <a href="/source/${requestScope.volume.title.replace(" ","_")}/${requestScope.chapter.title.replace(" ","_")}.html"
                                                                                            target="_black" class="card-link">查看笔记</a></small>
                    </p>
                </div>
            </div>
            <!--笔记详情-->
            <div class="card-columns mt-5">

            </div>
        </div>
    </div>
</div>
<footer class="mt-5 pt-5 pl-5 text-muted text-center text-small">
    <ul class="list-inline">
        <li class="list-inline-item">&copy; 2017-2018 Marvin Studio</li>
        <li class="list-inline-item"><a href="#">About Project Muninn</a></li>
    </ul>
</footer>
</body>
</html>