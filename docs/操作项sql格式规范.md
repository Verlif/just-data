# 操作项SQL格式规范

* 操作项sql需要与绑定的数据库语法一致。
* 操作项sql支持变量。

## 变量格式

操作项sql中的变量支持`#{}`与`@{}`。

* `#{}`表示了路径参数。例如`#{page}`就会从 __url路径__ 中寻找`page`参数，赋值到此处。
* `@{}`表示了body取值。例如`@{username}`就会从 __request荷载的json数据__ 中寻找`username`的值，赋值到此处。
  * `@{}`支持json链取值，例如`@{school.classroom.student}`就会从跟节点的`school对象`中取`classroom对象`，再从`classroom对象`中取`student对象`。

变量中都可以使用默认值，使用格式为`#{变量名:默认值}`，例如`#{username:Verlif}`表示了在路径中获取`username`的值，若未获取到则使用`Verlif`来填充。