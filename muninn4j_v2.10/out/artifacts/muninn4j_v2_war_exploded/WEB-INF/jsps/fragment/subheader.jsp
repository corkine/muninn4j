<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--二级标题--%>
<nav class="nav nav-tabs bg-light">
    <li class="nav-item">
        <small><a class="nav-link">${param.title}</a></small>
    </li>
    <c:if test="${param.isInfo eq true}">
    <li class="nav-item">
        <small><a class="nav-link" href="#">Overview</a></small>
    </li>
    <li class="nav-item">
        <small><a class="nav-link  active" href="#">Course Info</a></small>
    </li>
    <li class="nav-item">
        <small><a class="nav-link disabled" href="#">Notebook</a></small>
    </li>
    </c:if>
    <c:if test="${param.isOverview eq true}">
        <li class="nav-item">
            <small><a class="nav-link  active" href="#">Overview</a></small>
        </li>
        <li class="nav-item">
            <small><a class="nav-link" href="#">Course Info</a></small>
        </li>
        <li class="nav-item">
            <small><a class="nav-link disabled" href="#">Notebook</a></small>
        </li>
    </c:if>
</nav>