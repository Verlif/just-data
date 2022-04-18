# Just Data

__数据库接口映射服务__

只需要通过xml即可添加访问数据库的后端接口，只需要会写SQL即可完成后端接口搭建。

## 可以做什么

* 只要会SQL，不需要Java知识都可以搭建后台服务。
* 搭建对数据库的数据映服务。
* 完善的内置开发接口，方便二次开发。
* 能搭建基础博客、商城后台服务。

## 工作原理

基本原理就是解析xml文件，获取需要连接的数据库和需要创建的接口服务。通过服务内置的解析器与映射器来完成接口生成工作。

### 工作流程

1. 通过配置的操作项文件路径加载操作项与数据库信息。
2. 创建对应的数据库连接池（这里使用的是Druid）。
3. 加载操作项，并映射到对应数据库。
4. 根据加载的操作项生成操作项接口。
5. 完成。

## TODO

* [x] 基础操作项加载
* [ ] 权限控制
* [x] 接口日志
* [ ] 支持多个数据库

  | 数据库 | 支持状态 |
  |:-----| :----: |
  | MySql     | 支持   |
  | Sql Server |      |
  | Oracle    |      |
  | PostgreSQL |      |
  | sqlite |      |

* [x] 支持多个数据源
* [x] RSA加密支持
* [ ] 动态更新操作项
* [ ] api列表，包括了api需要的参数与参数是否必填等信息
* [ ] 支持外置jar包拓展
* [ ] 数据库操作项自动生成
* [ ] 更自由的文本自定义
* [ ] 内置指令
* [ ] api指令

## 配置

### Station配置
延续了station的配置，包括了以下组件配置：

* api-log

### JustData配置

```yaml
just-data:
  # 操作项配置
  items:
    # 操作项文件夹读取路径（推荐相对路径）
    path: src\test\java\resources\envo
  # 接口异常配置
  exception:
    # 异常信息写入文件路径（自动创建）（推荐相对路径）
    file: log\exception.log
```