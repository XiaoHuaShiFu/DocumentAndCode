<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
<title>注册QA</title>
<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/style.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/SignUp.css" />
</head>
<body>
	<div class="container">
		<div class="header">
			<div class="logoText">欢迎注册QA</div>
		</div>
		<form method="post" action="SignUp">
			<div class="main">
				<div class="logContent">
					<img class="avatar" src="<%=basePath%>resources/images/avatar.png"/>
					<div class="inputContent">
						<img src="<%=basePath%>resources/images/people_fill.png" class="inputIcon" />
						<input class="inputBox" value="${username }" name="username" type="text" placeholder="用户名" maxlength="20" />
					</div>
					<div class="inputContent">
						<img src="<%=basePath%>resources/images/people_fill.png" class="inputIcon" />
						<input class="inputBox" value="${phone }" name="phone" type="text" placeholder="手机" maxlength="11" />
					</div>
					<div class="inputContent">
						<img src="<%=basePath%>resources/images/people_fill.png" class="inputIcon" />
						<input class="inputBox" value="${email }" name="email" type="text" placeholder="邮箱" maxlength="50" />
					</div>
					<div class="inputContent">
						<img src="<%=basePath%>resources/images/lock_fill.png" class="inputIcon" /> 
						<input class="inputBox" value="${password }" name="password" type="password" placeholder="密码" maxlength="16" />
					</div>
					<input class="button" type="submit" value="注册" />
					<p class="signUpResult">${signUpResult }</p>
				</div>
			</div>
		</form>


		<div class="tail">
			<div class="tailContent1">
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
				<p class="text1">碧云嘉贤颖娴</p>
			</div>
			<div class="tailContent2">
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
				<div class="text2">碧云嘉贤颖娴</div>
			</div>
			<div class="tailContent3">
				<div class="text2">碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴</div>
				<div class="text2">|</div>
				<div class="text2">碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴</div>
				<div class="text2">|</div>
				<div class="text2">碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴碧云嘉贤颖娴</div>
			</div>
		</div>
	</div>

</body>
</html>