<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="com.mazhangjing.muninn.v2.entry.Chapter" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
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
<script type="application/javascript">
    $(function() {
        $(".delps").click(function () {
                var href = $(this).attr("href");
                $(".delete_method").attr("action", href).submit();
                return false;
            }
        )
    });
    $('#exampleModal').on('show.bs.modal', function (event) {
    })
</script>
<body>

<%
    //用于时间格式化
    String today = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日").format(LocalDate.now());
    request.setAttribute("today",today);

    Chapter chapter = (Chapter) request.getAttribute("chapter");

    //用于抽取当前 URL
    String id = Optional
            .ofNullable(chapter).filter(cha -> cha.getId() != null).map(Chapter::getId)
            .orElse("000");
    request.setAttribute("url",id + "/postscript");

    //用于构建当前章节的介绍
    String intro = Optional.ofNullable(chapter).map(Chapter::getIntro).orElse("");
    String realIntro = Arrays.stream(intro.split("\n")).filter(Objects::nonNull).filter(p -> !p.isEmpty())
            .map(pa -> "<p class='card-text'>" + pa + "</p>").collect(Collectors.joining("\n"));
    request.setAttribute("intro",Optional.ofNullable(realIntro).orElse(""));

    //用于构建当前章节笔记
    String content = Optional.ofNullable(chapter).map(Chapter::getContent).orElse("");
    StringBuilder builder = new StringBuilder();
    List<String> collect = Optional.of(content)
            .map(con -> con.split("\n"))
            .map(array -> Arrays.stream(array)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> line.startsWith("<h1") || line.startsWith("<h2"))
                    .collect(Collectors.toList()))
            .orElse(new ArrayList<>());

    int index = 0; int h2_index = 0;
    for (String line : collect) {
        if (line.startsWith("<h1")) {
            h2_index = 0;
            builder.append("<li class=\"list-group-item\">\n");
            //添加 H1 标题
            builder.append("\t").append(
                    Optional.ofNullable(Jsoup.parse(collect.get(index)).text()).orElse(collect.get(index))
            ).append("\n");
        } else if (line.startsWith("<h2")) {
            if (h2_index == 0)
                builder.append("\t<ul class=\"list-group list-group-flush mt-3\">\n\t\t<li class=\"list-group-item border-top\">");
            else {
                builder.append("\t\t<li class=\"list-group-item\">");
            }
            //添加 H2 标题
            builder.append(Optional.ofNullable(Jsoup.parse(collect.get(index)).text()).orElse(collect.get(index)));
            String next = index+1 < collect.size() ? collect.get(index + 1) :"DONE_WITH_H2" ;
            if (next.startsWith("<h2")) {
                builder.append("</li>\n");
            } else if (next.startsWith("<h1")) {
                builder.append("</li>\n\t</ul>\n");
            } else if (next.startsWith("DONE_WITH_H2")) {
                builder.append("</li>\n").append("\n\t</ul>\n");
            } h2_index ++;
        }
        String next = index+1 < collect.size() ? collect.get(index + 1) :"DONE_WITH_H1";
        if (next.equals("<h1")) { builder.append("</li>\n"); } else if (next.equals("DONE_WITH_H1")) {
            builder.append("</li>");
        } index ++;
    };

    request.setAttribute("content", builder.toString());
%>

<c:import url="fragment/header.jsp" charEncoding="UTF-8">
    <c:param name="head" value="${head}"/>
</c:import>
<c:import url="fragment/subheader.jsp">
    <c:param name="title" value="${chapter.volume.title}" />
    <c:param name="isInfo" value="${true}" />
</c:import>


<%--添加笔记对话框和表单--%>
<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Create a new PostScript - Link</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="ps-form" action="${url}" method="post">
                    <div class="form-group">
                        <label for="recipient-name" class="col-form-label">Title:</label>
                        <input name="title" type="text" class="form-control" id="recipient-name">
                    </div>
                    <div class="form-group">
                        <label for="message-text" class="col-form-label">Content:</label>
                        <textarea name="body" class="form-control" id="message-text" rows="7"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button type="submit" form="ps-form" class="btn btn-primary">Add to This Chapter</button>
            </div>
        </div>
    </div>
</div>

<%--删除笔记表单--%>
<form class="delete_method" action="" method="post">
    <input type="hidden" name="_method" value="delete" />
</form>

<%--主体部分--%>
<div class="container mt-5 ml-auto">
    <div class="row">
        <!--左侧-->
        <div class="col-md-4">
            <!--章节名称-->
            <div class="list-group list-group-flush">
                <c:if test="${volume ne null and volume.chapters ne null}">
                    <c:forEach items="${volume.chapters}" var="chapter">
                        <c:if test="${chapter ne null and chapter.volume ne null and chapter.volume.id ne null
                    and chapter.title ne null}">
                            <a href="/volume/${chapter.volume.id}/chapter/${chapter.id}" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                                    ${chapter.title}<span class="badge badge-primary badge-pill">
                                <c:if test="${chapter.version eq null}">
                                    0.1
                                </c:if> ${chapter.version}
                            </span></a>
                        </c:if>
                    </c:forEach>
                </c:if>
            </div>
            <!--章节详细信息-->
            <div class="card mt-4">
                <ul class="list-group">
                    <li class="list-group-item list-group-item-light">章节目录</li>
                    ${content}
                </ul>
            </div>
        </div>
        <!--右侧-->
        <div class="col-md-8 pl-3">
            <div class="card">
                <div class="card-header align-text-top">${chapter.title} - 内容简介
                    <button type="button" class="btn btn-link btn-sm text-dark  float-right m-0 p-0" data-toggle="modal" data-target="#exampleModal">
                        ＋
                    </button>
                </div>
                <div class="card-body">
                    ${intro}
                    <p class="card-text mt-2">
                        <small class="text-muted">最后更新于 ${chapter.lastEdit} <a href="/source/${chapter.fileName}"
                                                                               target="_black" class="card-link"> 查看笔记</a>

                        </small>
                    </p>
                </div>
            </div>
            <!--笔记详情-->
            <div class="card-columns mt-5">
                <c:forEach items="${chapter.postscripts}" var="ps">
                    <c:if test="${ps.getClass().getSimpleName() eq 'Quote'}">
                        <div class="card text-right p-3">
                            <blockquote class="blockquote mb-0">
                                <p>${ps.title}</p>
                                <footer class="blockquote-footer">
                                    <c:if test="${ps.footer ne null and ps.footer ne ''}">
                                        <small class="text-muted">${ps.footer}</small>
                                    </c:if>
                                    <c:if test="${ps.footer eq null or ps.footer eq ''}">
                                        <small class="text-muted">A Great Man</small>
                                    </c:if>
                                </footer>
                            </blockquote>
                        </div>
                    </c:if>
                    <c:if test="${ps.getClass().getSimpleName() eq 'Question'}">
                        <div class="card border-danger">
                            <div class="card-body">
                                <p class="card-text">${ps.title}</p>
                                <div class="float-right"><a href="${url}/${ps.id}" class="text-dark  card-link delps">&times;</a></div>
                                <span class="card-text"><small>${ps.time}</small></span>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${ps.getClass().getSimpleName() eq 'Link'}">
                        <div class="card">
                            <div class="card-body">
                                <c:if test="${(ps.title ne null) and ps.title ne ''}">
                                    <h5 class="card-title">
                                        <a class="card-link" href="${ps.url}">${ps.title}</a>
                                    </h5>
                                </c:if>
                                <p class="card-text">${ps.body}</p>
                                <div class="float-right"><a href="${url}/${ps.id}" class="text-dark  card-link delps">&times;</a></div>
                                <small class="card-text text-muted">${ps.time}</small>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${ps.getClass().getSimpleName() eq 'Note'}">
                        <div class="card">
                            <div class="card-body">
                                <p class="card-text">${ps.body}</p>
                                <div class="float-right"><a href="${url}/${ps.id}" class="text-dark  card-link delps">&times;</a></div>
                                <span class="card-text"><small>${ps.time}</small></span>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<c:import url="fragment/footer.jsp" charEncoding="UTF-8" />
</body>
</html>