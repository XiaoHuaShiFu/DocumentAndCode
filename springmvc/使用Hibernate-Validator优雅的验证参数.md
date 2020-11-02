# 使用Hibernate-Validator优雅的验证参数

![img](https://csdnimg.cn/release/blogv2/dist/pc/img/original.png)

[冲奶粉的奶爸](https://me.csdn.net/qq_32258777) 2019-03-25 17:33:19 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/articleReadEyes.png) 10604 ![img](https://csdnimg.cn/release/blogv2/dist/pc/img/tobarCollect.png) 收藏 14

分类专栏： [开发实用技巧](https://blog.csdn.net/qq_32258777/category_7751979.html) 文章标签： [Hibernate-Validator](https://so.csdn.net/so/search/s.do?q=Hibernate-Validator&t=blog&o=vip&s=&l=&f=&viparticle=) [代码优化](https://www.csdn.net/gather_20/MtTaEg1sMjM5MDMtYmxvZwO0O0OO0O0O.html) [校验](https://so.csdn.net/so/search/s.do?q=校验&t=blog&o=vip&s=&l=&f=&viparticle=) [springboot](https://www.csdn.net/gather_2c/MtTaEg0sMDg2NDYtYmxvZwO0O0OO0O0O.html)

版权



### 文章目录

- [背景](https://blog.csdn.net/qq_32258777/article/details/86743416#_1)
- [校验步骤](https://blog.csdn.net/qq_32258777/article/details/86743416#_6)
- [利于spring中的@ControllerAdvice定制优雅的返回信息](https://blog.csdn.net/qq_32258777/article/details/86743416#springControllerAdvice_103)
- [常用注解](https://blog.csdn.net/qq_32258777/article/details/86743416#_139)



# 背景

在开发中经常需要写一些字段校验的代码，比如字段非空，字段长度限制，邮箱格式验证等等，写这些与业务逻辑关系不大的代码个人感觉有两个麻烦：

- 验证代码繁琐，重复劳动
- 方法内代码显得冗长

# 校验步骤

1. **添加 Hibernate-Validator 依赖，如果使用了springboot，则不需要引用任何依赖，因为spring-boot-starter-web包中已经包含了Hibernate-Validator 依赖**

```javascript
  <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>6.0.7.Final</version>
        </dependency>
12345
```

1. **在类的属性上加上对应的注解，set，get方法采用了[lombok](https://blog.csdn.net/qq_32258777/article/details/80780078)框架**

```javascript
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestDto {
    @NotBlank(message = "姓名不能为空")
    private String name;
}
12345678
```

1. **Post方法中应用，需要注解** @RequestBody @Valid

```javascript
 /**
     * 测试校验框架返回结果格式
     */
    @PostMapping(value = "/validator")
    public String testValidator(@RequestBody @Valid TestDto testDto){
        return "校验成功...";
    }
1234567
```

4.**利于postman调用接口，入参和出参如下**

```javascript
//入参
{
	"name":""
}

// 返回结果
{
    "timestamp": "2019-02-01T09:03:16.350+0000",
    "status": 400,
    "error": "Bad Request",
    "errors": [
        {
            "codes": [
                "NotBlank.testDto.name",
                "NotBlank.name",
                "NotBlank.java.lang.String",
                "NotBlank"
            ],
            "arguments": [
                {
                    "codes": [
                        "testDto.name",
                        "name"
                    ],
                    "arguments": null,
                    "defaultMessage": "name",
                    "code": "name"
                }
            ],
            "defaultMessage": "姓名不能为空",
            "objectName": "testDto",
            "field": "name",
            "rejectedValue": "",
            "bindingFailure": false,
            "code": "NotBlank"
        }
    ],
    "message": "Validation failed for object='testDto'. Error count: 1",
    "path": "/test/validator"
}
12345678910111213141516171819202122232425262728293031323334353637383940
```

**若想使用Get请求，需要在类上加入注解*@Validated，**然后再加入其它对应注解，代码如下**：

```javascript
@RestController
@RequestMapping("/test")
@Validated
public class TestController {

    /**
     * 测试校验框架返回结果格式
     */
    @GetMapping(value = "/validator2")
    public String testValidator2(@NotBlank(message = "姓名不能为空") String name){
        return "校验成功...";
    }

}
1234567891011121314
```

# 利于spring中的@ControllerAdvice定制优雅的返回信息

相信看到这里会有很多的小伙伴会忍不住飙脏话了，这是个什么鬼？ 这谁能看懂呀！ 对的，对于这中返回格式我也无力吐槽了，怎么办？ 既然我们懒的手写判断，就得忍受该框架的返回样式了。方法总比困难多，既然它统一返回这种样式，那我就解析它吧，怎么解析，aop，拦截器等都可以，但是这里我用到了另外的一种方式，spring自带的[全局异常处理机制](https://blog.csdn.net/qq_32258777/article/details/86743416)，具体代码如下

```javascript
/**
 * @Description: 全局的异常处理
 * @Author: chenmingjian
 * @Date: 18-10-31 19:00
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
 /**
     * 参数校验统一异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        log.warn("参数校验异常:{}({})", fieldError.getDefaultMessage(),fieldError.getField());
        return ResponseEntity.ok(Response.fail(211,fieldError.getDefaultMessage(),fieldError.getDefaultMessage()));
    }

}
1234567891011121314151617181920
```

**重新调用无参的接口后，返回结果**：

```javascript
{
    "code": 211,
    "message": "姓名不能为空",
    "detail": "姓名不能为空",
    "data": null
}
123456
```

# 常用注解

| 注解                        | 释义                                                         |
| --------------------------- | ------------------------------------------------------------ |
| @Nul                        | 被注释的元素必须为 null                                      |
| @NotNull                    | 被注释的元素必须不为 null                                    |
| @AssertTrue                 | 被注释的元素必须为 true                                      |
| @AssertFalse                | 被注释的元素必须为 false                                     |
| @Min(value)                 | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值     |
| @Max(value)                 | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值     |
| @DecimalMin(value)          | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值     |
| @DecimalMax(value)          | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值     |
| @Size(max, min)             | 被注释的元素的大小必须在指定的范围内，元素必须为集合，代表集合个数 |
| @Digits (integer, fraction) | 被注释的元素必须是一个数字，其值必须在可接受的范围内         |
| @Past                       | 被注释的元素必须是一个过去的日期                             |
| @Future                     | 被注释的元素必须是一个将来的日期                             |
| @Email                      | 被注释的元素必须是电子邮箱地址                               |
| @Length(min=, max=)         | 被注释的字符串的大小必须在指定的范围内，必须为数组或者字符串，若微数组则表示为数组长度，字符串则表示为字符串长度 |
| @NotEmpty                   | 被注释的字符串的必须非空                                     |
| @Range(min=, max=)          | 被注释的元素必须在合适的范围内                               |
| @NotBlank                   | 被注释的字符串的必须非空                                     |
| @Pattern(regexp = )         | 正则表达式校验                                               |
| @Valid                      | 对象级联校验,即校验对象中对象的属性                          |