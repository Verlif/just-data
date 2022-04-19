# 操作项SQL格式规范

* 操作项sql需要与绑定的数据库语法一致。
* 操作项sql支持变量。

## 变量格式

操作项sql中的变量支持`#{}`、`@{}`与`&{}`。

* `#{}`表示了路径参数。例如`#{page}`就会从 __url路径__ 中寻找`page`参数，赋值到此处。
* `@{}`表示了body取值。例如`@{username}`就会从 __request荷载的json数据__ 中寻找`username`的值，赋值到此处。
  * `@{}`支持json链取值，例如`@{school.classroom.student}`就会从跟节点的`school对象`中取`classroom对象`，再从`classroom对象`中取`student对象`。
* `${}`表示了内置参数，通常与登录用户相关。当内置参数无法获取到值并且参数无默认值时，不执行sql并返回客户端失败码。可用的参数如下：
  * `${userId}`当前登录用户ID。

## 变量默认值

变量中都可以使用默认值，使用格式为`#{变量名:默认值}`，例如`#{username:Verlif}`表示了在路径中获取`username`的值，若未获取到则使用`Verlif`来填充。(注：只有第一个`:`是区隔符，后面的`:`是包括在默认值中)

## 变量值处理

### 数据加密

Just-data支持`RSA`方式的数据加密，并提供了公钥获取接口（`GET`方法`special/rsa`）。开发者需要使用加密时，只需要将公钥加密后的数据使用解密方法`@DECRYPT()`（区分大小写）即可。

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
