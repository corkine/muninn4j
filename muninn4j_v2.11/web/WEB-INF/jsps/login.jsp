<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Sign in</title>

    <!-- Bootstrap core CSS -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <!-- Custom styles for this template -->
    <link href="/css/signin.css" rel="stylesheet">
</head>

<body class="text-center">
<form class="form-signin" method="post" action="">
    <%--<img class="mb-4" src="https://getbootstrap.com/assets/brand/bootstrap-solid.svg" alt="" width="72" height="72">--%>
    <h1 class="h3 mb-5 font-weight-normal">Please sign in</h1>
    <c:if test="${param.user_callback eq 'no_auth'}">
    <div class="alert alert-danger">用户凭证无效，请重试！</div>
    </c:if>
    <c:if test="${param.user_callback eq 'no_login'}">
    <div class="alert alert-danger">请登陆后再试！</div>
    </c:if>
    <label for="inputEmail" class="sr-only">User Name</label>
    <input name="name" type="name" id="inputEmail" class="form-control" placeholder="User Name" value="${param.name eq null ? '' : param.name}" required autofocus>
    <label for="inputPassword" class="sr-only">Password</label>
    <input name="passWord" type="password" id="inputPassword" class="form-control" placeholder="Password" required>
    <p> &nbsp;</p>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    <p class="mt-5 mb-3 text-muted">&copy; 2018 Marvin Studio</p>
</form>
</body>
</html>