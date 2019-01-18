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
<title>${question.title }-QA</title>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resources/css/question.css" />
	<script src="<%=basePath%>resources/js/lib/jquery-3.3.1.min.js"></script>
	<script src="<%=basePath%>resources/js/front/question.js"></script>
	<script>
		function onComment(id) {
			window.location.href="QuestionComment?id=" + id + "&method=comment";
		}
		function like(questionId,commentId) {
			window.location.href="QuestionDetail?questionId=" + questionId + "&commentId=" + commentId + "&method=like";
		}
		function onFollow(questionId) {
			window.location.href="QuestionDetail?questionId=" + questionId + "&method=follow";
		}
	</script>

</head>
<body>
	<div class="container">
		<div class="heading2">
			<img class="logo"
				src="<%=basePath%>resources/images/yingxian/logo.png" />
			<ul class="ul">
				<li class="li">首页</li>
				<li class="li">发现</li>
				<li class="li">话题</li>
			</ul>
			<form>
				<input type="text" placeholder="这是一个晴朗的早晨" />
				<button class="button" style="cursor: pointer">
					<img src="<%=basePath%>resources/images/yingxian/search.png"
						width="34px" height="34px" />
				</button>
				<button class="button2">提问</button>
			</form>
			<button class="id" style="cursor: pointer">
				<img src="<%=basePath%>resources/images/yingxian/bell.png"
					width="90%" height="150%" />
			</button>
			<button class="id" style="cursor: pointer">
				<img src="<%=basePath%>resources/images/yingxian/user.png"
					width="90%" height="150%" />
			</button>
		</div>
		<div class="heading1">
			<div class="title1">${question.title }</div>
			<div class="question-content" >${question.content }</div>
			<div class="leading">
				<button class="bottom1" onclick="onFollow(${question.id})">
					<c:if test="${isFollow == 'unfollow'}">
			        	关注问题
			        </c:if>
					<c:if test="${isFollow == 'followed'}">
			        	已关注
			        </c:if>
				</button>
				<button class="bottom4" onclick="onComment(${question.id})">
					<img src="<%=basePath%>resources/images/yingxian/edit.png"
						width="17" height="17">评论
				</button>
				<div class="comment">
					<img src="<%=basePath%>resources/images/yingxian/message.png" />${question.comment }评论</div>
				<div class="comment">
					<img src="<%=basePath%>resources/images/yingxian/eye.png" />${question.click }浏览</div>
				<div class="comment">
					<img src="<%=basePath%>resources/images/yingxian/team.png" />${question.follow }关注</div>
			</div>
		</div>
		<div class="content">
			<div class="question"></div>
			<div class="critic">
				<c:choose>
					<c:when test="${question.comments != null}">
						<c:forEach items="${question.comments}" var="comment"
							varStatus="status">
							<div class="main">
								<div class="block">
									<div class="portrait">
										<img
											src="<%=basePath%>resources/images/yingxian/image-fill.png"
											width="80" height="80" />
									</div>
									<div class="data">发布于${comment.date }</div>
									<div class="bottom2">
										<img src="<%=basePath%>resources/images/yingxian/team.png" />${comment.respondent.follow }关注</div>
								</div>
								<div class="block1">${comment.content }</div>
								<div class="block2">
									<button class="bottom3">
										<img src="<%=basePath%>resources/images/yingxian/star.png" />67收藏
									</button>
									<button class="bottom3" onclick="like(${question.id},${comment.id})">
										<img src="<%=basePath%>resources/images/yingxian/heart.png" />${comment.like }点赞
									</button>
								</div>
							</div>
						</c:forEach>
					</c:when>
				</c:choose>

			</div>
			<div class="question"></div>
		</div>
	</div>
</body>
</html>