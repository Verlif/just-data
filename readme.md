# Just Data

__数据库接口映射服务__

本质上是通过`SQL`来生成后台接口。与代码生成不同，Just-data不会生成任何代码，只会解析`SQL`语句来生成接口对象。

并且Just-data的目标是成为一个中间服务，以加载XML配置的方式来生成接口，服务于

只需要配置`XML文件`即可添加访问数据库的后端接口，只需要会写`SQL`即可完成后端接口搭建。

- [一般流程创建演示](docs/一般流程说明.md)
- [XML配置模板](docs/template/template.xml)
- [SQL中使用变量参数](docs/操作项SQL格式规范.md)
- [登录与权限配置](docs/登录与权限.md)
- [内置接口文档](docs/内置接口文档.md)
- [文件操作](docs/文件操作.md)
- [项目结构说明](docs/项目结构.md)

## 特点

* 通过XML文件生成后台接口。
* 通过配置完成登录、登出与接口权限控制。
* 数据库无关性，登录与权限都由使用者自己决定。
* 支持多数据源同时连接。
* 轻量，不需要安装、占用资源小。
* 快速，启动快、接口响应快。

## 使用

你要做的，就是配置`application.yml`来设定服务，书写`xml`来生成需要的接口。其他的事情`Just-data`会来搞定的。

1. 修改`application.yml`配置（如果需要的话）。
2. 编写`XML配置`文件（也可以在第`3`步后编辑，然后通过`/router/reload`接口重加载）。
3. 启动`just-data.jar`。
4. 访问编写的接口。

## 工作原理

基本原理就是解析XML文件，获取需要连接的数据库和需要创建的接口信息。通过服务内置的解析器与映射器来完成接口生成工作。

### 工作流程

1. 通过配置的操作项文件路径加载操作项与数据库信息。
2. 创建对应的数据库连接（这里使用的是Druid连接池）。
3. 加载操作项，并映射到对应数据库。
4. 根据加载的操作项生成操作项接口。
5. 操作项生成的接口都在`/api/{label}/{api}`下。

## TODO

* [x] 基础操作项加载
* [x] 接口权限控制
* [x] 接口日志
* [ ] 支持多种数据库

  * [x] MySql
  * [ ] Sql Server
  * [ ] Oracle
  * [ ] PostgreSQL
  * [ ] Sqlite

* [x] 同时连接多个数据源
* [ ] 外置驱动文件，减少程序大小
* [x] RSA加密支持
* [x] 动态更新操作项配置
* [x] API列表展示更多的信息
* [x] 文件上传与下载
* [ ] 支持SQL语句流（开启事务）
* [ ] 支持外置jar包拓展
* [ ] 数据库操作项自动生成
* [x] 更自由的文本自定义
* [ ] 指令API

## 配置

Just-data的配置基于`SpringBoot`，目前有两个配置文件：

- `application.yml` - 总配置文件
- `message.properties` - 语言文本文件

### 语言文件

语言文件默认是`i18n\message.properties`，可以通过`spring.messages.basename`修改。

### Station配置

延续了`Station`的配置，包括了以下组件配置：

* [ApiLogging](https://github.com/Verlif/logging-spring-boot-starter)
* [FileService](https://github.com/Verlif/file-spring-boot-starter)
* [TaskService](https://github.com/Verlif/task-spring-boot-starter)

### JustData配置

```yaml
just-data:
  # Token配置
  token:
    # 解析的请求header参数名
    header: Token
    # Token生成使用的密钥
    secret: qwertyuioplkjhgfdsazxcvbnm
    # Token有效期，也表示了登录token空闲时的存活时间。单位为秒
    expireTime: 3600
  # 操作项配置
  items:
    # 操作项文件夹读取路径（推荐相对路径）
    path: src\test\java\resources\test
  # 接口异常配置
  exception:
    # 接口异常输出方式（CONSOLE - 控制台；FILE - 独立文件；CLIENT - 客户端），多个方式以“,”隔开
    output: CONSOLE
    # 当output中存在FILE时生效
    file: log\exception.log
  # 登录配置
  login:
    # 是否开启登录
    enabled: true
    # 获取用户的密钥
    queryUserKey:
      # 数据源label
      label: demo
      # 使用了与操作项相同的语法，只是这里需要SELECT出用户的密钥。
      sql: "SELECT user_password FROM sys_user WHERE id = @{id}"
  # 权限配置
  permission:
    # 是否开启权限
    enabled: true
    # 内置接口需要的权限key（值为空则不需要权限）
    inner-permission: admin
    queryPermission:
      # 权限获取的数据源label
      label: demo
      # 权限获取sql语句。这里需要取得登录用户的所有权限key，可以使用${userId}变量来替换登录用户ID
      sql: "SELECT permission
        FROM t_permission p
        LEFT JOIN t_role_permission rp ON rp.permission_id = p.permission_id
        LEFT JOIN t_user_role ur ON ur.role_id = rp.role_id
        WHERE ur.user_id = ${userId}"
  file:
    # 是否开启文件系统
    enabled: true
    # 使用文件系统时是否需要登录
    needOnline: false
    # 文件上传模式，决定了上传文件的存储模式（DAY - 按照日期建立文件夹；MONTH - 按照月份建立文件夹；ID - 根据登录用户ID建立文件夹）
    uploadType: ID
    # 文件操作模式，控制用户的文件管理权限（ID - 只允许用户操作自己的ID文件域文件，配合uploadType(ID)使用；ALL - 无限制）
    downloadType: ID
```

注：

* 可以通过`spring.redis.enabled=true`将内存缓存模式替换为`redis`缓存。