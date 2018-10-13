<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>首页-QA</title>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resources/css/home.css" />
<script src="<%=basePath%>resources/js/lib/jquery-3.3.1.min.js"></script>
<script src="<%=basePath%>resources/js/front/home.js"></script>
</head>

<body>
	<form method="post" id="mainForm" action="GetQuestion">
		<div class="container">
			<div class="head">
				<div class="head-content">
					<div class="logo">
						<img style="cursor:pointer"
							src="<%=basePath%>resources/images/home/logo.png" width="120px"
							height="120px" />
					</div>
					<div class="nav">
						<div class="nav-other">
							<div class="nav-3" style="cursor:pointer"
								onmouseover="this.style.color='RGB(23,81,153)'"
								onmouseout="this.style.color='black'">首页</div>
							<div class="nav-3" style="cursor:pointer"
								onmouseover="this.style.color='RGB(23,81,153)'"
								onmouseout="this.style.color='black'">发现</div>
							<div class="nav-3" style="cursor:pointer"
								onmouseover="this.style.color='RGB(23,81,153)'"
								onmouseout="this.style.color='black'">话题</div>
						</div>
						<div class="search">
							<textarea class="allInput" placeholder="搜索"
								style="height:20px; width: 99%; font-size:15px; resize:none; overflow:hidden"></textarea>
							<div class="search-image" style="cursor:pointer">
								<img src="<%=basePath%>resources/images/home/search.png"
									width="20px" height="20px" />
							</div>
						</div>
						<button class="button" style="cursor:pointer"
							onmouseover="this.style.color='blue'"
							onmouseout="this.style.color='black'">搜索</button>
					</div>
					<div class="user-in">
						<div class="user-inform" style="cursor:pointer">
							<img class="user-image"
								src="<%=basePath%>resources/images/home/bell2.png" width="20px"
								height="20px" />
						</div>
						<div class="user-mine" style="cursor:pointer">
							<img class="user-image"
								src="<%=basePath%>resources/images/home/people.png" width="20px"
								height="20px" />
						</div>
					</div>
				</div>
			</div>
			<div class="main">
				<div class="main-content">
					<div class="info-flow">
						<div class="head_of_flow">
							<div class="write" onmouseover="this.style.color='blue'"
								onmouseout="this.style.color='black'" style="cursor:pointer">
								<img class="user-image"
									src="<%=basePath%>resources/images/home/write.png" width="20px"
									height="20px" /> 写文章
							</div>
							<div class="question" onmouseover="this.style.color='blue'"
								onmouseout="this.style.color='black'" style="cursor:pointer">
								<img class="user-image"
									src="<%=basePath%>resources/images/home/question.png"
									width="20px" height="20px" /> 提问
							</div>
						</div>
						<c:choose>
							<c:when test="${questions != null}">
								<c:forEach items="${questions}" var="question"
									varStatus="status">
									<div class="info">
										<div class="introduction" style="color:RGB(166,166,166)">
											评论来自：${question.comments[0].respondent.username }，${question.comments[0].respondent.introduction }
										</div>
										<a class="title" target="_blank"
											style=" color:RGB(26,26,26); font-size:22px;"
											href="QuestionDetail?id=${question.id }&method=detail">
											${question.title } </a>
										<div class="text" >
											<c:if test="${fn:length(question.comments[0].content) > '110' }">
			                    				${fn:substring(question.comments[0].content, 0, 110) }...
			                    			</c:if>
											<c:if test="${fn:length(question.comments[0].content) <= '110' }">
			                    				${question.comments[0].content }
			                    			</c:if>
										</div>
										<div class="action">
											<a class="comment"
												href="Home?method=like&commentId=${question.comments[0].id }"
												onmouseover="this.style.color='blue'"
												onmouseout="this.style.color='black'"> <img
												class="user-image2"
												src="<%=basePath%>resources/images/home/heart.png"
												width="20px" height="20px" /> ${question.comments[0].like }赞同
											</a>
											 <a class="comment" onmouseover="this.style.color='blue'"
												onmouseout="this.style.color='black'"> <img
												class="user-image2"
												src="<%=basePath%>resources/images/home/write.png"
												width="20px" height="20px" /> 563评论
											</a> 
											<%-- <a class="comment" onmouseover="this.style.color='blue'"
												onmouseout="this.style.color='black'"> <img
												class="user-image2"
												src="<%=basePath%>resources/images/home/star-fill.png"
												width="20px" height="20px" /> 732收藏
											</a> --%>
										</div>
									</div>
								</c:forEach>
							</c:when>
						</c:choose>

						<c:choose>
							<c:when test="${articles != null}">
								<c:forEach items="${articles}" var="article" varStatus="status">
									<div class="info">
										<div class="introduction" style="color:RGB(166,166,166)">
											文章来自：${article.author.username }，${article.author.introduction }
										</div>
										<a class="title" href="ArticleDetail?id=${article.id }&method=detail"
											target="_blank" style=" color:RGB(26,26,26); font-size:22px;">
											${article.title } </a>
										<div class="text">
											<c:if test="${fn:length(article.content) > '110' }">
			                    				${fn:substring(article.content, 0, 110) }...
			                    			</c:if>
											<c:if test="${fn:length(article.content) <= '110' }">
			                    				${article.content }
			                    			</c:if>
										</div>
										<div class="action">
											<a class="comment" onmouseover="this.style.color='blue'"
												onmouseout="this.style.color='black'"> <img
												class="user-image2"
												src="<%=basePath%>resources/images/home/heart.png"
												width="20px" height="20px" /> ${article.like }赞同
											</a> <a class="comment" onmouseover="this.style.color='blue'"
												onmouseout="this.style.color='black'"> <img
												class="user-image2"
												src="<%=basePath%>resources/images/home/write.png"
												width="20px" height="20px" /> ${article.comment }评论
											</a> 
											<a class="comment" onmouseover="this.style.color='blue'"
												onmouseout="this.style.color='black'"> <img
												class="user-image2"
												src="<%=basePath%>resources/images/home/star-fill.png"
												width="20px" height="20px" /> ${article.collection }收藏
											</a>
										</div>
									</div>
								</c:forEach>
							</c:when>
						</c:choose>
					</div>
					<div class="user-nav">
						<div class="user">
							<div class="user-2">
								<div class="user-image">
									<img src="<%=basePath%>resources/images/home/star-fill.png"
										width="20px" height="20px" />
								</div>
								<div class="right-nav" style="cursor:pointer">我的收藏</div>
							</div>
							<div class="user-2">
								<div class="user-image">
									<img src="<%=basePath%>resources/images/home/people.png"
										width="20px" height="20px" />
								</div>
								<div class="right-nav" style="cursor:pointer">我关注的人</div>
							</div>
							<div class="user-2">
								<div class="user-image">
									<img src="<%=basePath%>resources/images/home/question.png"
										width="20px" height="20px" />
								</div>
								<div class="right-nav" style="cursor:pointer">我关注的问题</div>
							</div>
							<div class="user-2">
								<div class="user-image">
									<img src="<%=basePath%>resources/images/home/answer.png"
										width="20px" height="20px" />
								</div>
								<div class="right-nav" style="cursor:pointer">我回答过的问题</div>
							</div>
							<div class="user-2">
								<div class="user-image">
									<img src="<%=basePath%>resources/images/home/back.png"
										width="20px" height="20px" />
								</div>
								<div class="right-nav" style="cursor:pointer">问题反馈</div>
							</div>
						</div>
						<div class="bottom" style="font-size: 13px;">
							<div class="bottom1" style="cursor:pointer">小花师傅·QA指南·QA协议·隐私政策</div>
							<div class="bottom1" style="cursor:pointer">应用·工作·申请开通QA机构号</div>
							<div class="bottom1" style="cursor:pointer">侵权举报·网上信息有害举报专区</div>
							<div class="bottom1" style="cursor:pointer">违法和不良信息举报</div>
							<div class="bottom1" style="cursor:pointer">网络文化经营许可证</div>
							<div class="bottom1" style="cursor:pointer">联系我们·QA</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</body>
</html>