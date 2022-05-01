# 操作项SQL格式规范

* 操作项sql需要与绑定的数据库语法一致（mysql下的操作项sql不能使用oracle的语法）。
* 操作项sql支持变量，变量决定了生成接口的请求参数。

## 变量格式

操作项sql中的变量支持`#{}`、`@{}`与`&{}`。

* `#{}`表示了路径参数。例如`#{page}`就会从 __url路径__ 中寻找`page`参数，赋值到此处。
* `@{}`表示了body取值。例如`@{username}`就会从 __request荷载的json数据__ 中寻找`username`的值，赋值到此处。
  * `@{}`支持json链取值，例如`@{school.classroom.student}`就会从跟节点的`school对象`中取`classroom对象`，再从`classroom对象`中取`student对象`。
* `${}`表示了内置参数，通常与登录用户相关。可用的参数如下：
  * `${userId}`当前登录用户ID。这里的用户ID表示的是登录时传递的`id`数据，例如使用用户名与密码登录，这时`${userId}`就是表示了用户名。

sql中的所有sql均会自动判断数据类型，一般情况下不需要增加诸如`''`这类字符串标识。

## 变量默认值

变量中都可以使用默认值，使用格式为`#{变量名:默认值}`，例如`#{username:Verlif}`表示了在路径中获取`username`的值，若未获取到则使用`Verlif`来填充。(注：只有第一个`:`是区隔符，后面的`:`是包括在默认值中)

当请求中无法取得某变量值且其变量无默认值时，此SQL不执行，并会返回前端参数缺失的说明。

## 变量值处理

### 数据解密

Just-data支持`RSA`方式的数据加密，并提供了公钥获取接口（`GET`方法`special/rsa`）。开发者需要使用加密时，只需要将公钥加密后的数据使用解密方法`@DECRYPT()`解密（区分大小写）即可。

例如：

```xml
<items label="user">
    <item name="注册">
        <api>register</api>
        <method>POST</method>
        <sql>INSERT INTO t_user (@{name}, @{sex}, @DECRYPT(@{key:123})) </sql>
    </item>
</items>
```

当解密失败时，会直接使用客户端传参。所以实际上使用`@DECRYPT()`时，不使用加密数据作为参数值也是可以的，不过不推荐。

### 用户密钥加密

Just-data使用了BCryptPasswordEncoder作为密钥加密工具，这也表明了这里的加密不能解密，只能做数据匹配。

密钥加密使用方法`@ENCODE()`即可，例如：

```xml
<item name="注册用户">
    <api>register</api>
    <method>POST</method>
    <sql>INSERT INTO t_user (user_name, user_password, create_time) VALUES ('@{username}', '@ENCODE(@{password})', now())</sql>
</item>
```

此时访问`/api/domo/register`，并传参：

```json
{
	"username": "Verlif",
	"password": "123"
}
```

那么数据库中就会创建类似于如下的数据（每次加密同一段字符串，产生的密文都不同）：

| id  | user_name | user_password                                                 | create_time         |
|-----|-----------|---------------------------------------------------------------|---------------------|
| 3   | Verlif    | 	$2a$10$kBMcIjsUdEi0VX5zkG2y3.rCVKu.NhejCGXIv8.IU//ihoIpczBMO | 2022-04-22 11:09:34 |

随后访问登录接口（登录配置开启时）`\special\login`，并传递通过公钥加密后密码：

```json
{
	"id": 3,
	"key": "BUx9VJ4qpN05/W01MURjJtAWoVfP1SXtAxV7ngStXGQgvxiIAWqZ5CMbJO/Wa1ppir156a8xkbGumIcYTNBd00CnD2wZ0Te5TkrG6ATmMYCMmrFt0mK8kFqv2Xt0uKLGI77M9LaKUTUDTUPw7jZcTShEaMsyv+DHwNizHsGOm8U="
}
```

当密钥正确时即完成登录。

## 动态SQL方法

XML中的SQL支持类似Mybatis方式的动态SQL语法。据体支持的方法如下：

- `if` - 推断布尔逻辑来判断是否使得其中的SQL语句出现。

注意：

- 所有的动态SQL方法中的变量请直接使用变量名，不需要类似与`url变量`与`body变量`做区分。例如在`url`中有一个`size=5`，而`body`中有一个`"name":"Verlif"`，在SQL语句中需要使用`#{size}`与`@{name}`，但是在动态SQL方法参数中就直接使用`size`与`name`即可。
- 动态SQL语句暂不支持同名方法嵌套，例如`{if test:"userId=1"} {if test:"sex=1"} {fi} {fi}`两个`if`作为嵌套是不支持的，会出现解析错误。

### if

使用格式：

```xml
<sql>
  SELECT * FROM t_user
  {if test:"id!=null"}
    WHERE user_id = #{id}
  {fi}
</sql>
```

其中`test`表示了推断逻辑，当其中的语句推断为真时，`WHERE user_id = #{id}`则会被添加到sql中，反之则不会添加。

`test`参数支持`or`与`and`两个关键词（小写），但不支持`()`表达，`and`的优先级高于`or`，类似与以下方式：

- `A and B` - A与B同时为真则为真。
- `A and B or C` - A与B同时为真，或者C为真则为真。
- `A or B and C` - A为真，或者B与C同时为真则为真。