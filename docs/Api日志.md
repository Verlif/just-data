# API日志

## API日志方式

API日志目前支持三种方式：

- 控制台打印
  - 输出到控制台，由系统日志系统接管。
- 日志文件写入
  - 写入到单独的接口日志文件中。
- XML方式的数据库写入
  - 通过API日志接口特定参数，将日志信息写入到数据库中。

这三种方式都可以同时配置。

## 控制台方式

在配置中将`type`参数添加`CONSOLE`即可。

例如：

```yaml
just-data:
  api-log:
    type: CONSOLE
```

## 日志文件方式

在配置中将`type`参数添加`FILE`，并在`file.log`参数中指明日志文件路径即可。

例如：

```yaml
just-data:
  api-log:
    type: FILE
    file:
      log: log\api-log.log
```

## XML配置方式

在配置中将`type`参数添加`XML`，并在`file.xml`参数中指明XML配置文件路径即可。

例如：

```yaml
just-data:
  api-log:
    type: XML
    file:
      xml: config\api-log.xml
```

XML配置格式与操作项类似：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<info>
    <label>demo</label>
    <sql>INSERT INTO t_log (content) VALUES (CONCAT('[', now(), '] [/#{log.label}/#{log.api}] - #{log.method} - params=[#{log.params}], body=[#{log.body}]'))</sql>
</info>
```

比较特殊的是，在其SQL中支持日志特有变量参数：

- `log.label` - 访问的label
- `log.api` - 访问的api
- `log.method` - 请求方法
- `log.params` - 请求路径参数。例如`id=2&username=Verlif`
- `log.body` - 请求body

## application配置

```yaml
just-data:
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
```