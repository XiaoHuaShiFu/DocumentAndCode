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
    <title>${article.title } -QA</title>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/article.css">
    <script src="<%=basePath%>resources/js/lib/jquery-3.3.1.min.js"></script>
	<script src="<%=basePath%>resources/js/front/article.js"></script>
	<script>
		function onFollow(authorId, articleId) {
			window.location.href="ArticleDetail?authorId=" + authorId + "&articleId=" + articleId + "&method=follow";
		}
		function tosubmit(id) {
			var content = $("#content").val();
			window.location.href="ArticleDetail?id=" + id + "&content=" + content + "&method=submit";
		}
	</script>
</head>
<body>
<div class="container">
    <div class="head">
        <div class="head-content">
            <a class="logo" href="https://www.zhihu.com/">
                <img src="<%=basePath%>resources/images/article/logo.png" width="120px" height="120px"  />
            </a>
            <div class="nav">
                <a class="nav-other" target="_blank" href="https://www.zhihu.com/" onmouseover="this.style.color='RGB(23,81,153)'" onmouseout="this.style.color='black'"> 首页</a>
                <a class="nav-other" target="_blank" href="https://www.zhihu.com/"  onmouseover="this.style.color='RGB(23,81,153)'" onmouseout="this.style.color='black'"> 发现</a>
                <a class="nav-other" target="_blank" href="https://www.zhihu.com/"  onmouseover="this.style.color='RGB(23,81,153)'" onmouseout="this.style.color='black'"> 话题</a>
                <a class="search">
                    <textarea class="allInput" placeholder="搜索内容" style="height:20px; width: 99%; font-size:15px; resize:none; overflow:hidden" ></textarea>
                    <div class="search-image" style="cursor:pointer"> <img src="<%=basePath%>resources/images/article/search.png" width="20px" height="20px" /> </div>
                </a>
                <button  class="button" onmouseover="this.style.color='blue'" onmouseout="this.style.color='black'" >提问</button>
            </div>
            <div class="user-in">
                <a class="user-inform" target="_blank" href="https://www.zhihu.com/"> <img class="user-image"  src="<%=basePath%>resources/images/article/bell2.png" width="20px" height="20px"  /></a>
                <a class="user-mine" target="_blank" href="https://www.zhihu.com/"> <img class="user-image"  src="<%=basePath%>resources/images/article/people.png" width="20px" height="20px"  /></a>
            </div>
        </div>
    </div>
    <div class="main">
        <div>
            <img class="main-image" src="<%=basePath%>resources/images/article/logo.png"/>
        </div>
        <div class="main-headline">${article.title }</div>
        <div class="main-mine">
            <img class="mine-image" src="<%=basePath%>resources/images/article/logo.png" >
            <a class="mine-intro" target="_blank" href="https://www.zhihu.com/">${user.username },${user.introduction }</a>
            <button class="attention" style="cursor:pointer">
                <div class="atten-image" ><img src="<%=basePath%>resources/images/article/plus.png" width="15px" height="15px" /></div>
                <div class="atten-txt" onclick="onFollow(${article.authorId},${article.id })">
	       			<c:if test="${isFollow == 'unfollow'}">
				        	关注他
				    </c:if>
					<c:if test="${isFollow == 'followed'}">
				        	已关注
				    </c:if>         
                </div>
            </button>
        </div>
        <div class="txt">${article.content }</div>
        <div class="other-comment">
            <div class="write-comment">
                <textarea class="write-down" id="content" placeholder="请输入评论"></textarea>
                <button class="Button" onclick="tosubmit(${article.id})" onmouseover="this.style.color='blue'" >发表</button>
            </div>
			<c:choose>
				<c:when test="${article.comments != null}">
					<c:forEach items="${article.comments}" var="comment"
						varStatus="status">
				           <div class="others">
				               <a class="others-info" target="_blank" href="">${comment.respondent.username }</a>
				               <div class="others-info">${comment.content }</div>
				               <a class="others-info" href="ArticleDetail?articleId=${article.id }&commentId=${comment.id }&method=like" onmouseover="this.style.color='blue'" onmouseout="this.style.color='black'">
				                   <div class="other-img" ><img class="like-image"  src="<%=basePath%>resources/images/article/like.png" width="20px" height="20px" /></div>
				                   <div class="other-img2">${comment.like }</div>
				               </a>
				           </div>
					</c:forEach>
				</c:when>
			</c:choose>
				
				
        </div>
    </div>
    <div class="bottom">
        <div class="like">
            <a class="like1"> <img class="like-image"  src="<%=basePath%>resources/images/article/heart.png" width="20px" height="20px" />
                <div class="text" onmouseover="this.style.color='blue'" onmouseout="this.style.color='black'">1111人点赞</div>
            </a>
            <a class="like1"><img class="like-image"  src="<%=basePath%>resources/images/article/write.png" width="20px" height="20px" />
                <div class="text" onmouseover="this.style.color='blue'" onmouseout="this.style.color='black'">1111条评论</div>
            </a>
            <a class="like1"><img class="like-image"  src="<%=basePath%>resources/images/article/star-fill.png" width="20px" height="20px" />
                <div class="text" onmouseover="this.style.color='blue'" onmouseout="this.style.color='black'">555人收藏</div>
            </a>
            <a class="like1"><img class="like-image"  src="<%=basePath%>resources/images/article/share.png" width="20px" height="20px" />
                <div class="text" onmouseover="this.style.color='blue'" onmouseout="this.style.color='black'">222人分享</div>
            </a>
        </div>
    </div>
</div>
</body>
</html>

