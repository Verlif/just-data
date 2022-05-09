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
- 动态SQL语句暂不支持同名方法嵌套，例如`{if test="userId=1"} {if test="sex=1"} {fi} {fi}`两个`if`作为嵌套是不支持的，会出现解析错误。

### if

使用条件来控制SQL语句片段是否生效。

#### 举例

```xml
<sql>
  SELECT * FROM user
  {if test="id != null AND id > 5"}
    WHERE user_id = #{id}
  {elseif test="name != null"}
    WHERE username = #{name}
  {elseif}
    WHERE user_id > 10
  {if}
</sql>
```

上面的语句可以表示为以下描述：

```text
SELECT * FROM user
如果id参数不为空且id的值大于5时，则WHERE user_id = #{id}；
否则当name参数不为空时，则WHERE username = #{name}；
当上述条件都不成立时，则WHERE user_id > 10。
```

#### 说明

`test`参数支持`OR`与`AND`两个关键词（大小写都可以，不允许混写），但暂不支持`()`表达，`AND`的优先级高于`OR`，类似与以下方式：

- `A AND B` - A与B同时为真则为真。
- `A AND B OR C` - A与B同时为真，或者C为真则为真。
- `A OR B AND C` - A为真，或者B与C同时为真则为真。

布尔表达式可以使用 __不带参数的Java方法__ ，例如：

- `name != null AND name.length() > 2`表示name参数不为空且其值的长度大于2。

### where

简单的`where`区域，自动消除条件中的`and`和`or`（不区分大小写），当无条件时，不出现`where`关键词。

#### 举例

```xml
<sql>
    SELECT * FROM user
    {where}
        {if test="userId != null"}
            and user_id = #{userId:1}
        {elseif test="username != null and username.length() > 2"}
            AND username = #{username}
        {elseif test="nickname != null AND nickname.length() > 2"}
            and nickname = #{nickname}
        {if}
    {where}
</sql>
```

当`if`中的三个条件都不满足时，只执行`SELECT * FROM user`，当存在条件时，会消除无用的`AND`或`OR`前缀。

#### 说明

一般情况下，`where`会与`if`一起使用，便于避免SQL语句解析错误。

### trim

更全面的语句修复，通过trim的参数来消除前缀或后缀。

#### 举例

```xml
<sql>
    SELECT * FROM user
    {trim prefix="WHERE" prefixOverrides="AND|and"}
        {if test="userId != null"}
            and user_id = #{userId:1}
        {elseif test="userIds != null"}
            user_id IN
            {foreach open="(" separator="," close=")" item="userId" collection="userIds"}
                #{userId}
            {foreach}
        {elseif test="username != null and username.length() > 2"}
            AND username = #{username}
        {elseif test="nickname != null AND nickname.length() > 2"}
            and nickname = #{nickname}
        {if}
    {trim}
</sql>
```

这里的`trim`方法就替代了`where`方法，并会消除`AND`和`and`前缀词。

参数：

- `prefix` - 语句的前缀词。当方法内无内容时，前缀词不会出现。
- `prefixOverrides` - 需要消除的内容前缀，使用`|`符号进行分割，不会忽略`空格`。可以替代`where`。
- `suffixOverrides` - 需要消除的内同后缀，使用`|`符号进行分割，不会忽略`空格`。可以替代`set`。

### foreach

用于数组拆分的场景，一般情况下用在`where in`中。

#### 举例

```xml
<sql>
    SELECT * FROM user
    {trim prefix="WHERE" prefixOverrides="AND|and"}
        {if test="userId != null"}
            and user_id = #{userId:1}
        {elseif test="userIds != null"}
            user_id IN
            {foreach open="(" separator="," close=")" item="userId" collection="userIds"}
                #{userId}
            {foreach}
        {elseif test="username != null and username.length() > 2"}
            AND username = #{username}
        {elseif test="nickname != null AND nickname.length() > 2"}
            and nickname = #{nickname}
        {if}
    {trim}
</sql>
```

这里可以看出，当`userId`为空，且`userIds`不为空时，会执行`SELECT * FROM user WHERE user_id IN (?)`，这里的问号会表示为`userIds`数组。

参数：

- `open` - 左符号。
- `close` - 右符号。
- `separator` - 分隔符号，不忽略`空格`。
- `item` - 数组的每一个参数值，可以在方法内通过`#{}`来使用。
- `collection` - 需要遍历的参数，在`param`和`body`参数中获取，直接使用参数名即可。与`if`的`test`一样，允许使用参数描述，例如`user.hair`。
- `index` - 当前的序号，从0开始，在方法内通过`#{}`来使用。