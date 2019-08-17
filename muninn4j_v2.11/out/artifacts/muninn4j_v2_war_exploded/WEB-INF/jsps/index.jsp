<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.mazhangjing.muninn.v2.reward.Reward" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core_1_1" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <title>Muninn - Jupyter Notebook Viewer</title>
    <link rel="stylesheet" href="/css/bootstrap.css">
</head>
<body>
<c:import url="fragment/header.jsp">
    <c:param name="head" value="${head}"/>
</c:import>
<%
    LocalDate date = LocalDate.now();
    LocalDate before = date.with(temporal -> {
        LocalDate localDate = (LocalDate) temporal;
        switch (localDate.getDayOfWeek()) {
            case MONDAY:
                return localDate.minusDays(0);
            case TUESDAY:
                return localDate.minusDays(1);
            case WEDNESDAY:
                return localDate.minusDays(2);
            case THURSDAY:
                return localDate.minusDays(3);
            case FRIDAY:
                return localDate.minusDays(4);
            case SATURDAY:
                return localDate.minusDays(5);
            case SUNDAY:
                return localDate.minusDays(6);
            default:
                return null;
        }
    });
    LocalDate after = before.plusDays(6);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
    request.setAttribute("before",formatter.format(before));
    request.setAttribute("after",formatter.format(after));
    String today = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日").format(date);
    session.setAttribute("today",today);
%>
<%
Map<Reward, Long> map = (Map<Reward, Long>) request.getAttribute("reward");
request.setAttribute("note",map.get(Reward.NOTE_THIS_WEEK));
request.setAttribute("post",map.get(Reward.POST_THIS_WEEK));
List<Reward> collect = map.keySet().stream()
        .filter(k -> k != Reward.NOTE_THIS_WEEK && k != Reward.POST_THIS_WEEK)
        .sorted(Comparator.comparing(Reward::getId)).collect(Collectors.toList());
request.setAttribute("keys",collect);
%>
<div class="container">
    <div class="row pt-5">
        <div class="col-md-7">
            <div class="jumbotron jumbotron-fluid  p-5 m-0">
                <h1 class="display-4">Hello, Corkine</h1>
                <p class="mt-3 lead">本周您更新了 ${note} 章节，记录了 ${post} 则灵感，继续保持！</p>
                <hr class="my-2">
                <p class="mt-3">自从上次更新，你获得了 <span class="badge badge-secondary">新的希望</span> 勋章。</p>
                <p class="mt-3 text-muted">最后更新于：${today}</p>
            </div>
            <h1 class="lead mt-3 pb-0 pl-1 pt-4">博客聚焦
                <br><span class="small text-muted font-weight-light">所有的博客均发布于 blog.mazhangjing.com</span> </h1>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">Python数据处理学习笔记 - seaborn统计数据可视化篇</h5>
                    <p class="card-text small text-muted">Seaborn是基于Python的一个统计绘图工具包。Seaborn提供了一组高层次封装的matplotlib API接口。使用Seaborn而不是matplotlib，
                        绘图只需要少数几行代码，并且可以更加容易控制Style、Palette。本文基本是按照官方Guide顺序写就的。</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">编程范式——《像计算机科学家一样思考》读书笔记（下）</h5>
                    <p class="card-text small text-muted">这是我关于《如何像计算机科学家一样思考》一书的体会和总结。此书的副标题叫做《Think Python》，
                        作者是Allen.B.Downey，欧林计算机学院计算机科学教授，MIT计算机科学学士和硕士，UCB计算机科学博士。
                        作者本身写作此书的原因是用来讲解的语言是java入门知识，其目标是：简短、循序渐进、专注于编程而非语言。这本书里出现的编程知识基本上是所有语言所共用的，
                        因此用来做一个程序学习之架构是非常合适，这也是本文希望做的——在这本书的基础上建立一个学习所有编程语言的基本框架。</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">使用Python和Slack实现字幕组美剧的更新和通知</h5>
                    <p class="card-text small text-muted">本文介绍了使用Python和Slack实现字幕组美剧更新推送的一种方法，
                        脚本可以部署在Windows或者GNU/Linux或者macOS平台，使用计划任务或者CRON进行定时执行。你需要一个Slack的Webhook地址，
                        用于将消息POST到你的APP – Web、Android、iOS、watchOS以及PC、macOS均支持的Slack应用程序。</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>
            <div class="card mt-3">
                <div class="card-body">
                    <h5 class="card-title lead">图书馆七楼的落地窗 - 2017 于桂子山下</h5>
                    <p class="card-text small text-muted">写在华中师大，2017年底某个上弦月（半月），在地球上，武汉循进阳光的黑暗时（大约下午五点三十九分）。
                        此文另有姊妹篇，一年前于华中农业大学图书馆，见此： 图书馆五楼的落地窗 - 2016 于狮子山下</p>
                    <a href="#" class="card-link">查看全文</a>
                </div>
            </div>

        </div>
        <div class="col-md-5 pl-5">
            <div class="card pb-3">
                <!--<div class="card-header">勋章墙</div>-->
                <p class="card-title lead mt-4 ml-4">勋章墙 <span class="badge badge-light">${before} - ${after}</span></p>
                <div class="row pl-4 pr-4">
                    <c:forEach items="${keys}" var="key" varStatus="status">
                        <div class="col-md-4">
                            <div class="p-0 m-0 pt-3">
                                <c:if test="${requestScope.reward != null and requestScope.reward.get(key) == 0}">
                                    <img style="opacity: 0.3" class="rounded mx-auto d-block" src="src/${key.id}.png"  height="70" width="70" />
                                    <h6 class="lead text-center pt-3 text-muted">${key.name}</h6>
                                </c:if>
                                <c:if test="${requestScope.reward != null and requestScope.reward.get(key) != 0}">
                                    <img class="rounded mx-auto d-block disabled" src="src/${key.id}.png"  height="70" width="70" />
                                    <h6 class="lead text-center pt-3">${key.name}</h6>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            <div class="pt-3 pl-1 text-muted small">

                <p>我的 Jupyter 笔记系统 · Github 代码仓库
                    <br>Design and Developed by Corkine Ma (Github@corkine)
                    <br>Powered by Java · Spring · SpringMVC · Hibernate · Jupyter Notebook
                    <br>Corkine ❤ OpenSource · Server Version: ${sessionScope.version}
                    <br>© Marvin Studio 2018 </p>
            </div>
        </div>
    </div>
<c:import url="fragment/footer.jsp" charEncoding="UTF-8">
</c:import>
</div>

</body>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</html>