<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${question.title } -QA</title>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/answer.css">
    <script src="<%=basePath%>resources/js/lib/jquery-3.3.1.min.js"></script>
	<script src="<%=basePath%>resources/js/front/home.js"></script>
	<script>
		function tosubmit(id) {
			var content = $("#content").val();
			window.location.href="QuestionComment?id=" + id + "&content=" + content + "&method=submit";
		}
	</script>
</head>
<body>
   <div class="container">
    <div class="heading">
       <img class="logo" src="<%=basePath%>resources/images/yingxian/logo.png" width="30%" height="360%" />
        <ul class="ul">
            <li class="li">首页</li>
            <li class="li">发现</li>
            <li class="li">话题</li>
        </ul>
        <form>
        <input type="text" placeholder="输入话题" />
            <button class="button">
                <img src="<%=basePath%>resources/images/yingxian/search.png" width="80%" height="70%" />
            </button>
            <button class="button2"><a href="../ask/ask.html"> 提问</a></button>
        </form>
        <button class="id" style="cursor:pointer;">
            <img src="<%=basePath%>resources/images/yingxian/bell.png" width="90%" height="150%" />
        </button>
        <button class="id" style="cursor:pointer;">
          <a href="../personal/personal.html"> <img src="<%=basePath%>resources/images/yingxian/user.png" width="90%" height="150%" /></a>
        </button>
    </div>
       <div class="question">${question.title }</div>
       <form>
           <textarea id="content" rows="700" cols="1000" class="answer" placeholder="回答"></textarea>
       </form>
       <button class="submit" onclick="tosubmit(${question.id })">提交</button>
   </div>
</body>
</html>