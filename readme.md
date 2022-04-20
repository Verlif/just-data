# Just Data

__数据库接口映射服务__

本质上是通过`SQL`来生成后台接口。与代码生成不同，Just-data不会生成任何代码，只会解析`SQL`语句来生成接口对象。

只需要配置`XML文件`即可添加访问数据库的后端接口，只需要会写`SQL`即可完成后端接口搭建。

- [XML配置模板](docs/template.xml)
- [SQL中使用变量参数](docs/操作项SQL格式规范.md)
- [登录与权限配置](docs/登录与权限.md)
- [内置接口文档](docs/内置接口文档.md)

## 特点

* 通过XML文件生成后台接口。
* 通过配置完成登录、登出与接口权限控制。
* 数据库无关性。
* 支持多数据源。

## 工作原理

基本原理就是解析XML文件，获取需要连接的数据库和需要创建的接口信息。通过服务内置的解析器与映射器来完成接口生成工作。

### 工作流程

1. 通过配置的操作项文件路径加载操作项与数据库信息。
2. 创建对应的数据库连接（这里使用的是Druid连接池）。
3. 加载操作项，并映射到对应数据库。
4. 根据加载的操作项生成操作项接口。
5. 完成。

## TODO

* [x] 基础操作项加载
* [x] 权限控制
* [x] 接口日志
* [ ] 支持多种数据库

  | 数据库 | 支持状态 |
  |:-----| :----: |
  | MySql     | 支持   |
  | Sql Server |      |
  | Oracle    |      |
  | PostgreSQL |      |
  | sqlite |      |

* [x] 支持多个数据源
* [x] RSA加密支持
* [x] 动态更新操作项
* [x] API列表展示更多的信息
* [ ] 文件上传与下载
* [ ] 支持外置jar包拓展
* [ ] 数据库操作项自动生成
* [ ] 更自由的文本自定义
* [ ] 指令API

## 配置

### Station配置
延续了station的配置，包括了以下组件配置：

* [Api-log](https://github.com/Verlif/logging-spring-boot-starter)

### JustData配置

```yaml
just-data:
  # 操作项配置
  items:
    # 操作项文件夹读取路径（推荐相对路径）
    path: src\test\java\resources\test
  # 接口异常配置
  exception:
    # 异常信息写入文件路径（自动创建）（推荐相对路径）
    file: log\exception.log
  # 登录配置
  login:
    # 是否开启登录
    enable: true
    # 获取用户的密钥
    queryUserKey:
      # 数据源label
      label: demo
      # 使用了与操作项相同的语法，只是这里需要SELECT出用户的密钥。
      sql: "SELECT user_password FROM sys_user WHERE id = @{id}"
  # 权限配置
  permission:
    # 是否开启权限
    enable: true
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
```