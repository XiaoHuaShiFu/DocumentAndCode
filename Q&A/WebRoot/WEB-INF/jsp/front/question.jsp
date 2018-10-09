<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
 %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>${question.title } -QA</title>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>resources/css/question.css"/>
    <script src="<%=basePath %>resources/js/lib/jquery-3.3.1.min.js"></script>
	<script src="<%=basePath %>resources/js/front/question.js"></script>
</head>
<body>
    <div class="container">
        <div class="heading2">
                <img class="logo" src="../æ å¿.png" width="30%" height="360%" />
        <ul class="ul">
            <li class="li">é¦é¡µ</li>
            <li class="li">åç°</li>
            <li class="li">è¯é¢</li>
        </ul>
            <form>
                <input type="text" placeholder="è¾å¥è¯é¢" />
                <button class="button" style="cursor: pointer">
                    <img src="../search.png" width="80%" height="70%" /></button>
                    <button class="button2"> <a href="../ask/ask.html">æé®</a></button>
            </form>
        <button class="id" style="cursor: pointer">
            <img src="../bell.png" width="90%" height="150%" />
        </button>
        <button class="id" style="cursor: pointer">
            <a href="../personal/personal.html"> <img src="../user.png" width="90%" height="150%" /></a>
        </button>
        </div>
        <div class="heading1">
    <div class="title1">æ é¢</div>
        <div class="leading">
        <button class="bottom1" >å³æ³¨é®é¢</button>
        <button class="bottom4">
            <img src="../edit.png" width="17" height="17">åç­</button>
            <div class="comment">
                <img src="../message.png" \>è¯è®ºæ°</div>
            <div class="comment">
                <img src="../eye.png" \> æµè§é</div>
            <div class="comment">
                <img src="../team.png" \> å³æ³¨æ°</div>
        </div>
    </div>
        <div class="content">
        <div class="question"></div>
            <div class="critic">
                <div class="main">
                   <div class="block">
                       <div class="portrait" style="cursor: pointer">
                           <img src="../image-fill.png" width="80" height="80" \>
                       </div>
                       <div class="data">æ¥æ</div>
                       <div class="bottom2">
                           <img src="../team.png" \> å³æ³¨é</div>
                   </div>
                    <div class="block1">åå®¹</div>
                    <div class="block2">
                        <button class="bottom3">
                            <img src="../star.png" \> æ¶è</button>
                        <button class="bottom3">
                            <img src="../heart.png" \>ç¹èµ</button>
                    </div>
                </div>
                <div class="main">
                    <div class="block">
                        <div class="portrait" style="cursor: pointer">
                            <img src="../image-fill.png" width="80" height="80" \>
                        </div>
                        <div class="data">æ¥æ</div>
                        <div class="bottom2">
                            <img src="../team.png" \> å³æ³¨é</div>
                    </div>
                    <div class="block1">åå®¹</div>
                    <div class="block2">
                        <button class="bottom3">
                            <img src="../star.png" \> æ¶è</button>
                        <button class="bottom3">
                            <img src="../heart.png" \>ç¹èµ</button>
                    </div>
                </div>
                <div class="main">
                    <div class="block">
                        <div class="portrait">
                            <img src="../image-fill.png" width="80" height="80" \>
                        </div>
                        <div class="data">æ¥æ</div>
                        <div class="bottom2">
                            <img src="../team.png" \> å³æ³¨é</div>
                    </div>
                    <div class="block1">åå®¹</div>
                    <div class="block2">
                        <button class="bottom3">
                            <img src="../star.png" \> æ¶è</button>
                        <button class="bottom3">
                            <img src="../heart.png" \>ç¹èµ</button>
                    </div>
                </div>
            </div>
            <div class="question">
            </div>
        </div>
        <div class="base">
            <div class="base-content">å°è±å¸åÂ·QAæåÂ·QAåè®®Â·éç§æ¿ç­</div>
            <div class="base-content">åºç¨Â·å·¥ä½Â·ç³è¯·å¼éQAæºæå·</div>
            <div class="base-content">ä¾µæä¸¾æ¥Â·ç½ä¸ä¿¡æ¯æå®³ä¸¾æ¥ä¸åº</div>
            <div class="base-content">è¿æ³åä¸è¯ä¿¡æ¯ä¸¾æ¥</div>
            <div class="base-content">ç½ç»æåç»è¥è®¸å¯è¯</div>
            <div class="base-content">èç³»æä»¬Â·QA</div>
        </div>
    </div>
</body>
</html>