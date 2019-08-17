<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh_cn">
<head>
    <meta charset="UTF-8">
    <title>${volume.title} - 课程和笔记</title>
    <link rel="stylesheet" href="/css/bootstrap.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
</head>
<body>
<%
    String today = DateTimeFormatter.ofPattern("yyyy 年 MM 月 dd 日").format(LocalDate.now());
    request.setAttribute("today",today);
%>
<c:import url="fragment/header.jsp" charEncoding="UTF-8">
    <c:param name="head" value="${head}"/>
</c:import>
<c:import url="fragment/subheader.jsp">
    <c:param name="title" value="${volume.title}" />
    <c:param name="isOverview" value="${true}" />
    <c:param name="isInfo" value="${false}" />
</c:import>
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
            <br/>
        </div>
        <!--右侧-->
        <div class="col-md-8 pl-3">
            <!--本章概览-->
            <div class="card">
                <c:if test="${volume.title ne null}">
                <div class="card-header">${volume.title} - 课程简介</div>
                </c:if>
                <c:if test="${volume.title eq null}">
                    <div class="card-header">课程简介</div>
                </c:if>
                <div class="card-body">
                    <c:if test="${volume.title ne null}">
                        ${volume.intro}
                    </c:if>
                    <p class="card-text  mt-2">
                        <small class="text-muted">更新于 ${today}</small>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>
<c:import url="fragment/footer.jsp" charEncoding="UTF-8">
</c:import>
</body>
</html>