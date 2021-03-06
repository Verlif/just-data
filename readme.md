# Just Data

__数据库接口映射服务__

本质上是通过`SQL`来生成后台接口。与代码生成不同，Just-data不会生成任何代码，只会解析`SQL`语句来生成接口对象。

并且Just-data的目标是成为一个中间服务，以加载XML配置的方式来生成接口，服务于

只需要配置`XML文件`即可添加访问数据库的后端接口，只需要会写`SQL`即可完成后端接口搭建。

- [一般流程创建演示](docs/一般流程说明.md)
- [XML配置模板](docs/template/template.xml)
- [操作项SQL格式规范](docs/操作项SQL格式规范.md)
- [登录与权限配置](docs/登录与权限.md)
- [内置接口文档](docs/内置接口文档.md)
- [文件操作](docs/文件操作.md)
- [项目结构说明](docs/项目结构.md)
- [API接口日志自定义](docs/Api日志.md)

## 特点

* 通过XML文件生成后台接口，无需重启即可动态修改接口。
* 通过配置完成登录、登出与接口权限控制。
* 数据库无关性，登录与权限都由使用者自己决定。
* 支持多数据源同时连接。
* 轻量，不需要安装、占用资源小。
* 快速，启动快、接口响应快。

## 工作原理

基本原理就是解析XML文件，获取需要连接的数据库和需要创建的接口信息。通过服务内置的解析器与映射器来完成接口生成工作。

### 快速开始

1. 根据 [模板](docs/template/template.xml) 创建操作项 __XML配置文件__ 。
2. 添加所使用的数据库驱动文件，并在`application.yml`的`drivers`中配置 __驱动名__ 及 __驱动文件路径__ 。
3. 修改`application.yml`中的`items.path`，指向创建的 __XML配置文件__ 或其 __文件夹__ 。
4. 启动`just-data.jar`。
5. 通过`IP:PORT/api/{label}/{api}`访问创建的接口。

## TODO

* [x] 基础操作项加载
* [x] 接口权限控制
* [x] 接口日志
* [x] 支持多种数据库

  *目前使用的是`jdbc`方式加载外置驱动文件，所以理论上支持所有的`jdbc`方式的数据库。*

* [x] 同时连接多个数据源
* [x] 外置驱动文件，减少程序大小
* [x] RSA加密支持
* [x] 动态更新操作项配置
* [x] API列表展示更多的信息
* [x] 文件上传与下载
* [x] 支持SQL语句流（开启事务）
* [x] 支持动态SQL语句
  * [x] `if`
  * [x] `where`
  * [x] `foreach`
  * [x] `trim`
* [x] SQL变量参数防注入
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

* [FileService](https://github.com/Verlif/file-spring-boot-starter)
* [TaskService](https://github.com/Verlif/task-spring-boot-starter)

### JustData配置

```yaml
just-data:
  macro:
    # 全局变量文件路径
    file: config\macro.properties
    # 是否在文件变动后自动重载文件内容
    autoReload: true
    # 自动重载判定间隔时长，单位毫秒
    period: 2000
  # 需要加载的数据库驱动列表
  drivers:
    # 驱动名
    - driverName: com.mysql.cj.jdbc.Driver
      # 驱动文件地址
      driverFile: drivers\mysql-connector-java-8.0.27.jar
  # SQL配置
  sql:
    # 是否输出到控制台（true - 输出到控制台；false - 不输出）
    print: true
  # 接口日志处理
  api-log:
    # 日志类型（FILE - 写入文件；CONSOLE - 输出到控制台；XML - 通过xml配置写入数据库）（启用多个类型以“,”隔开，置为空则不启用接口日志）
    type: FILE, XML
    # 日志文件配置
    file:
      # 当日志类型为FILE时生效，表示写入的文件路径
      log: log\api-log.log
      # 当日志类型为XML时生效，表示读取的XML配置文件的路径
      xml: src\test\java\resources\log\log.xml
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
    # 操作项配置文件路径
    path: src\test\java\resources\one
    # 是否在文件变动后自动重载操作项文件
    autoReload: true
    # 自动重载判定间隔时长，单位毫秒
    period: 5000
  # 接口异常配置
  exception:
    # 接口异常输出方式（CONSOLE - 控制台；FILE - 独立文件；CLIENT - 客户端），多个方式以“,”隔开
    output: CONSOLE
    # 当output中存在FILE时生效
    file: log\exception.log
  # 文件系统配置
  file:
    # 是否开启文件系统
    enabled: true
    # 使用文件系统时是否需要登录
    needOnline: false
    # 文件上传模式，决定了上传文件的存储模式（DAY - 按照日期建立文件夹；MONTH - 按照月份建立文件夹；ID - 根据登录用户ID建立文件夹）
    uploadType: ID
    # 文件操作模式，控制用户的文件管理权限（ID - 只允许用户操作自己的ID文件域文件，配合uploadType(ID)使用；ALL - 无限制）
    downloadType: ID
  # 登录配置
  login:
    # 登录配置的xml地址
    file: src\test\java\resources\login.xml
  # 权限服务配置
  permission:
    # 权限配置的xml地址
    file: src\test\java\resources\permission.xml
```

注：

* 可以通过`spring.redis.enabled=true`将内存缓存模式替换为`redis`缓存。
