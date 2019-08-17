<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand navbar-dark bg-dark">
    <a class="navbar-brand" href="/">MUNINN <small></small></a>
    <div class="navbar-collapse collapse justify-content-between">
        <ul class="navbar-nav">
            <c:forEach items="${head}" var="cate" varStatus="s">
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
    <div class="collapse navbar-collapse justify-content-end">
        <c:if test="${sessionScope.user ne null}">
            <c:if test="${sessionScope.user.nickName ne null and sessionScope.user.nickName ne ''}">
            <span class="navbar-text">${sessionScope.user.nickName} | </span>
            <span class="navbar-text ml-2"><a href="/logout">Logout</a></span>
            </c:if>
            <c:if test="${(sessionScope.user.nickName eq null) or (sessionScope.user.nickName eq '')}">
                <span class="navbar-text ml-2"><a href="/logout">Logout</a></span>
            </c:if>
        </c:if>
        <c:if test="${sessionScope.user eq null}">
            <span class="navbar-text ml-2"><a href="/login">Login</a></span>
        </c:if>

    </div>
</nav>
