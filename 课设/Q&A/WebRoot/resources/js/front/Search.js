/**
 * 获取Question列表
 */
function getQuestions() {
	$("#mainForm").attr("action","GetQuestion");
	$("#mainForm").submit();
}

/**
 * 获取Article列表
 */
function getArticles() {
	$("#mainForm").attr("action","GetArticle");
	$("#mainForm").submit();
}

/**
 * 获取User列表
 */
function getUsers() {
	$("#mainForm").attr("action","GetUser");
	$("#mainForm").submit();
}

/**
 * 转到My页面
 */
function toMy() {
	$("#mainForm").attr("action","My");
	$("#mainForm").submit();
}


