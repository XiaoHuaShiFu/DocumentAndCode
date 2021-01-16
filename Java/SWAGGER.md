1、**@Api()**：用在请求的类上，表示对类的说明，也代表了这个类是swagger2的资源

参数：

```text
tags：说明该类的作用，参数是个数组，可以填多个。
value="该参数没什么意义，在UI界面上不显示，所以不用配置"
description = "用户基本信息操作"
```

2、**@ApiOperation()**：用于方法，表示一个http请求访问该方法的操作

参数：

```text
value="方法的用途和作用"    
notes="方法的注意事项和备注"    
tags：说明该方法的作用，参数是个数组，可以填多个。
格式：tags={"作用1","作用2"} 
（在这里建议不使用这个参数，会使界面看上去有点乱，前两个常用）
```

3、**@ApiModel()**：用于响应实体类上，用于说明实体作用

参数：

```text
description="描述实体的作用"  
```

4、**@ApiModelProperty**：用在属性上，描述实体类的属性

参数：

```text
value="用户名"  描述参数的意义
name="name"    参数的变量名
required=true     参数是否必选
```

5、**@ApiImplicitParams**：用在请求的方法上，包含多@ApiImplicitParam

6、**@ApiImplicitParam**：用于方法，表示单独的请求参数

参数：

```text
name="参数ming" 
value="参数说明" 
dataType="数据类型" 
paramType="query" 表示参数放在哪里
    · header 请求参数的获取：@RequestHeader
    · query   请求参数的获取：@RequestParam
    · path（用于restful接口） 请求参数的获取：@PathVariable
    · body（不常用）
    · form（不常用） 
defaultValue="参数的默认值"
required="true" 表示参数是否必须传
```

7、**@ApiParam()**：用于方法，参数，字段说明 表示对参数的要求和说明

参数：

```text
name="参数名称"
value="参数的简要说明"
defaultValue="参数默认值"
required="true" 表示属性是否必填，默认为false
```

8、**@ApiResponses**：用于请求的方法上，根据响应码表示不同响应

一个@ApiResponses包含多个@ApiResponse

9、**@ApiResponse**：用在请求的方法上，表示不同的响应

**参数**：

```text
code="404"    表示响应码(int型)，可自定义
message="状态码对应的响应信息"   
```

10、**@ApiIgnore()**：用于类或者方法上，不被显示在页面上

11、**@Profile({"dev", "test"})**：用于配置类上，表示只对开发和测试环境有用



![img](https://pic1.zhimg.com/80/v2-40968ef1594067ded8c69af24cfd9dc4_1440w.jpg)

## **总结：**

这些就是常用的注解，以及常用的参数。例举的不全面根据网上的介绍和自己的理解写的，如果有不对的地方欢迎指出。下面一篇文章直接举例子将这些注解使用到代码中，展示在页面上是什么效果；在页面上如何测试接口等操作，敬请期待。



[原文地址](https://link.zhihu.com/?target=https%3A//mp.weixin.qq.com/s/ZD0i1-lcqRHtgYL-HW1Xpg)



**了解更多欢迎关注公众号：快乐学习与分享**

主要分享百科教程，软件教程，技术干货，实用工具



发布于 2018-11-15



# 1、swagger2 注解整体说明

**用于controller类上：**

| 注解 | 说明           |
| ---- | -------------- |
| @Api | 对请求类的说明 |

**用于方法上面（说明参数的含义）：**

| 注解                                  | 说明                                                        |
| ------------------------------------- | ----------------------------------------------------------- |
| @ApiOperation                         | 方法的说明                                                  |
| @ApiImplicitParams、@ApiImplicitParam | 方法的参数的说明；@ApiImplicitParams 用于指定单个参数的说明 |

**用于方法上面（返回参数或对象的说明）：**

| 注解                        | 说明                                                    |
| --------------------------- | ------------------------------------------------------- |
| @ApiResponses、@ApiResponse | 方法返回值的说明 ；@ApiResponses 用于指定单个参数的说明 |

**对象类：**

| 注解              | 说明                                         |
| ----------------- | -------------------------------------------- |
| @ApiModel         | 用在JavaBean类上，说明JavaBean的 用途        |
| @ApiModelProperty | 用在JavaBean类的属性上面，说明此属性的的含议 |

# 2、@Api：请求类的说明

```java
@Api：放在 请求的类上，与 @Controller 并列，说明类的作用，如用户模块，订单类等。
	tags="说明该类的作用"
	value="该参数没什么意义，所以不需要配置"
123
```

示例：

```java
@Api(tags="订单模块")
@Controller
public class OrderController {

}
12345
```

`@Api` 其它属性配置：

| 属性名称       | 备注                                    |
| -------------- | --------------------------------------- |
| value          | url的路径值                             |
| tags           | 如果设置这个值、value的值会被覆盖       |
| description    | 对api资源的描述                         |
| basePath       | 基本路径                                |
| position       | 如果配置多个Api 想改变显示的顺序位置    |
| produces       | 如, “application/json, application/xml” |
| consumes       | 如, “application/json, application/xml” |
| protocols      | 协议类型，如: http, https, ws, wss.     |
| authorizations | 高级特性认证时配置                      |
| hidden         | 配置为true ，将在文档中隐藏             |

# 3、@ApiOperation：方法的说明

```java
@ApiOperation："用在请求的方法上，说明方法的作用"
	value="说明方法的作用"
	notes="方法的备注说明"
123
```

### 3.1、@ApiImplicitParams、@ApiImplicitParam：方法参数的说明

```java
@ApiImplicitParams：用在请求的方法上，包含一组参数说明
	@ApiImplicitParam：对单个参数的说明	    
	    name：参数名
	    value：参数的说明、描述
	    required：参数是否必须必填
	    paramType：参数放在哪个地方
	        · query --> 请求参数的获取：@RequestParam
	        · header --> 请求参数的获取：@RequestHeader	      
	        · path（用于restful接口）--> 请求参数的获取：@PathVariable
	        · body（请求体）-->  @RequestBody User user
	        · form（普通表单提交）	   
	    dataType：参数类型，默认String，其它值dataType="Integer"	   
	    defaultValue：参数的默认值
12345678910111213
```

示列：

```java
@Api(tags="用户模块")
@Controller
public class UserController {

	@ApiOperation(value="用户登录",notes="随边说点啥")
	@ApiImplicitParams({
		@ApiImplicitParam(name="mobile",value="手机号",required=true,paramType="form"),
		@ApiImplicitParam(name="password",value="密码",required=true,paramType="form"),
		@ApiImplicitParam(name="age",value="年龄",required=true,paramType="form",dataType="Integer")
	})
	@PostMapping("/login")
	public JsonResult login(@RequestParam String mobile, @RequestParam String password,
	@RequestParam Integer age){
		//...
	    return JsonResult.ok(map);
	}
}
1234567891011121314151617
```

# 4、@ApiResponses、@ApiResponse：方法返回值的状态码说明

```java
@ApiResponses：方法返回对象的说明
	@ApiResponse：每个参数的说明
	    code：数字，例如400
	    message：信息，例如"请求参数没填好"
	    response：抛出异常的类
12345
```

示例：

```java
@Api(tags="用户模块")
@Controller
public class UserController {

	@ApiOperation("获取用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query", name="userId", dataType="String", required=true, value="用户Id")
	}) 
	@ApiResponses({
		@ApiResponse(code = 200, message = "请求成功"),
		@ApiResponse(code = 400, message = "请求参数没填好"),
		@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对")
	}) 
	@ResponseBody
	@RequestMapping("/list")
	public JsonResult list(@RequestParam String userId) {
		...
		return JsonResult.ok().put("page", pageUtil);
	}
}
1234567891011121314151617181920
```

# 5、@ApiModel：用于JavaBean上面，表示对JavaBean 的功能描述

`@ApiModel`的用途有2个：

1. 当请求数据描述，即 `@RequestBody` 时， 用于封装请求（包括数据的各种校验）数据；
2. 当响应值是对象时，即 `@ResponseBody` 时，用于返回值对象的描述。

## 5.1、当请求数据描述时， `@RequestBody` 时的使用

```java
@ApiModel(description = "用户登录")
public class UserLoginVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户名",required=true)	
	private String username;

	@ApiModelProperty(value = "密码",required=true)	
	private String password;
	
	// getter/setter省略
}

1234567891011121314
@Api(tags="用户模块")
@Controller
public class UserController {

	@ApiOperation(value = "用户登录", notes = "")	
	@PostMapping(value = "/login")
	public R login(@RequestBody UserLoginVO userLoginVO) {
		User user=userSerivce.login(userLoginVO);
		return R.okData(user);
	}
}
1234567891011
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191227165035627.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3hpYW9qaW4yMWNlbg==,size_16,color_FFFFFF,t_70)

## 5.2、@ApiModelProperty：用在JavaBean类的属性上面，说明属性的含义

示例:

```java
@ApiModel(description= "返回响应数据")
public class RestMessage implements Serializable{

	@ApiModelProperty(value = "是否成功",required=true)
	private boolean success=true;	
	
	@ApiModelProperty(value = "错误码")
	private Integer errCode;
	
	@ApiModelProperty(value = "提示信息")
	private String message;
	
    @ApiModelProperty(value = "数据")
	private Object data;
		
	/* getter/setter 略*/
}
1234567891011121314151617
```

http://localhost:5680/zxmall/swagger-ui.html

![这里写图片描述](https://imgconvert.csdnimg.cn/aHR0cDovL2ltZy5ibG9nLmNzZG4ubmV0LzIwMTcxMTI4MTUyNDA5NjIy)