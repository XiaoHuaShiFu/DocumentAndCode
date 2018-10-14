<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html lang="en">
<head>
<title>QA，寻求问题的真相</title>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resources/css/SignIn.css" />
<style>
	.logo{
    width: 170px;
    height: 55px;
}
</style>
</head>

<body>
	<div class="container">
		<div class="header">
			<div class="logoContent">
				<img class="logo" src="<%=basePath%>resources/images/yingxian/logo.png" />
			</div>
			<div class="logoText">注册QA，寻求问题的真相</div>
		</div>
		<div class="main">
			<div class="logContent">
				<form method="post" action="SignIn">
					<div class="inputContent">
						<img src="<%=basePath%>resources/images/people_fill.png" class="inputIcon" /> 
						<input class="inputBox" name="phone" value="${phone }" type="text" placeholder="手机号码" maxlength="20" />
					</div>
					<div class="inputContent">
						<img src="<%=basePath%>resources/images/lock_fill.png" class="inputIcon" /> 
						<input class="inputBox" name="password" value="${password }" type="password" placeholder="密码" maxlength="16" />
					</div>
					<input class="button" type="submit" value="登录" /> 
				</form>
				<form method="post" action="ToSignUp">
					<input class="button" type="submit" value="没有账号？注册一个" />
				</form>
				<p class="signInResult" >${signInResult }</p>
			</div>
		</div>
		
		

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