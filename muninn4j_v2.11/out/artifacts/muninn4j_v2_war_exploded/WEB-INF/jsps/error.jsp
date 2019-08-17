<%@ page import="java.util.Optional" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core_1_1" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
    <title>Runtime Exception</title>
</head>
<body>
    <h1>发生了运行时异常，错误信息如下：</h1>
    <c:if test="${err_info eq null}">
    <p>无法处理的错误。</p>
    </c:if>
    <c:if test="${err_info ne null}">
        <%
            RuntimeException err_info = (RuntimeException) request.getAttribute("err_info");
            String info = Optional.ofNullable(err_info).map(err -> {
                StringWriter writer = new StringWriter();
                err.printStackTrace(new PrintWriter(writer));
                return writer.toString();
            }).orElse("无法解析的错误。");
            request.setAttribute("info",info);
        %>
        <p>${info}</p>
    </c:if>
</body>
</html>
