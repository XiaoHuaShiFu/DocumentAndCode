<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'My.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  
    <form id="mainForm" method="post" action="SearchQuestion">
    	<input class="" value="${keyword }" name="keyword" type="text" placeholder="如何评价滴滴打车事件?" maxlength="50" />
    	<input type="submit" value="搜索" ></input>
    	
    	<a href="javascript:getQuestions();" class="btn">问题</a>
    	<a href="javascript:getArticles();" class="btn">文章</a>
    	<a href="javascript:getUsers();" class="btn">用户</a>
    	
		<c:choose>
		    <c:when test="${questions != null}">
		        <c:forEach items="${questions}" var="question" varStatus="status">
					<td>${status.index + 1 }</td>
					<td>${question.title }</td>
					<td>${question.content }</td>
					<td>${question.authorId }</td>
					<td>${question.follow }</td>
					<td>${question.date }</td>
					<td>${question.click }</td>
					<br>
				</c:forEach>
		    </c:when>
		    <c:when test="${articles != null}">
		        <c:forEach items="${articles}" var="article" varStatus="status">
					<td>${status.index + 1 }</td>
					<td>${article.title }</td>
					<td>${article.content }</td>
					<td>${article.authorId }</td>
					<td>${article.collection }</td>
					<td>${article.date }</td>
					<td>${article.click }</td>
					<br>
				</c:forEach>
		    </c:when>
		    <c:when test="${users != null}">
		        <c:forEach items="${users}" var="user" varStatus="status">
					<td>${status.index + 1 }</td>
					<td>${user.username }</td>
					<td>${user.phone }</td>
					<td>${user.password }</td>
					<td>${user.email }</td>
					<td>${user.sex }</td>
					<td>${user.introduction }</td>
					<td>${user.follower }</td>
					<td>${user.follow }</td>
					<br>
				</c:forEach>
		    </c:when>
		    <c:otherwise>
		    </c:otherwise>
		</c:choose>
		
		<c:choose>
	    	<c:when test="${searchs != null}">
			    <c:forEach items="${searchs}" var="search" varStatus="status">
					<td>${status.index + 1 }</td>
					<td>${search.keyword }</td>
					<td>${search.numberSearch }</td>
					<br>
				</c:forEach>
			</c:when>
    	</c:choose>
    	
    </form>
    
  </body>
</html>
