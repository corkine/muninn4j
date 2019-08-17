<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="UTF-8">
    <title>Welcome to Muninn!</title>
    <link rel="stylesheet" href="css/bootstrap.css">
</head>
<body>
<nav class="navbar navbar-expand navbar-dark bg-dark">
    <a class="navbar-brand" href="/">MUNINN <small>by Corkine Ma</small></a>
    <div class="navbar-collapse collapse justify-content-between">
        <ul class="navbar-nav">
            <c:forEach items="${requestScope.header}" var="cate" varStatus="s">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="${cate.key.hashCode()}" data-toggle="dropdown">${cate.key}</a>
                <div class="dropdown-menu" aria-labelledby="${cate.key.hashCode()}">
                    <c:forEach items="${cate.value}" var="volu" varStatus="vols">
                        <a class="dropdown-item" href="/volume/${volu.id}">${volu.title}<small></small></a>
                    </c:forEach>
                </div>
            </li>
            </c:forEach>
        </ul>
    </div>
    <div class="collapse navbar-collapse justify-content-end"></div>
</nav>

<div class="container">
    <div class="row pt-5">
        <div class="col-md-7">
            <div class="jumbotron jumbotron-fluid  p-5 m-0">
                <h1 class="display-4">Hello, Corkine</h1>
                <!--<p class="lead">Muninn是一个存放在Github等静态网站托管服务商上的笔记系统，-->
                <!--此系统被设计用来整合不同领域的知识。本站点包含了奖励系统、课程系统和笔记系统。-->
                <!--你可以使用动态脚本生成站点。</p>-->
                <!--<hr class="my-2">-->
                <!--<p class="text-muted">本网站是Jupyter Notebook的展示模块，一个动态脚本用来遍历所有文件夹的课程章节的iPynb文件，-->
                <!--并且生成html文本，从此脚本中检索标题、更新日期、相关笔记，然后套用一个精致的排版展示出来。-->
                <!--</p>-->
                <!--<p class="mt-4 lead">-->
                <!--<a class="btn btn-lg btn-primary" href="#" role="button">了解更多</a>-->
                <!--</p>-->
                <p class="mt-3 lead">本周您更新了 1 章节，记录了 3 篇笔记，继续保持！</p>
                <hr class="my-2">
                <p class="mt-3">自从上次更新，你获得了 <span class="badge badge-secondary">文思泉涌</span> 勋章。</p>
                <p class="mt-3 text-muted">最后更新于：2018年6月13日</p>
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
                <p class="card-title lead mt-4 ml-4">勋章墙 <span class="badge badge-light">06/13 - 06/20</span></p>
                <div class="row">
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/idea_596.1401384083px_1209661_easyicon.net.png"  height="70" width="70" />
                            <h6 class="lead text-center pt-3">点子多多</h6>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 m-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/diary_638px_1209882_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">文思泉涌</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/studying_669.79180887372px_1205971_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">挑灯夜战</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/Book_2000px_1170680_easyicon.net.png"  height="70" width="70" />
                            <h5 class="lead text-center pt-3">日积月累</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 m-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/quote_128px_1088353_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">引经据典</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/sun_783px_1209087_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">一天之计</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/classroom_645.00155520995px_1210165_easyicon.net.png"  height="70" width="70" />
                            <h6 class="lead text-center pt-3">课程达人</h6>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 m-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/plant_639px_1210114_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">新的希望</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/bee_704px_1210071_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">乐此不疲</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pl-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/cup_1088.8906649616px_1205872_easyicon.net.png"  height="70" width="70" />
                            <h5 class="lead text-center pt-3">完美一周</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-0 pt-3">
                            <img class="rounded mx-auto d-block" src="src/aias_562.56010230179px_1205860_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">探索发现</h5>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="pr-3 pt-3">
                            <img class="rounded mx-auto d-block" src="src/nobel_1564px_1205896_easyicon.net.png" height="70" width="70"/>
                            <h5 class="card-title lead text-center pt-3">诺贝尔奖</h5>
                        </div>
                    </div>

                </div>
            </div>
            <div class="pt-3 pl-1 text-muted small">

                <p>我的 Jupyter 笔记系统 · 我的 Github 笔记展示仓库
                    <br>笔记博客系统部署指南 · Github 代码仓库
                    <br>Design & Developed by Corkine Ma (Github@corkine)
                    <br>Powered by Spring & Hibernate & Java Platform
                    <br>Server Version: 0.8.1
                    <br>© Marvin Studio 2018 </p>
            </div>
        </div>
    </div>
    <footer class="mt-5 pt-5 pl-5 text-muted text-center text-small">
        <ul class="list-inline">
            <li class="list-inline-item"></li>
            <li class="list-inline-item"></li>
        </ul>
    </footer>
</div>

</body>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</html>