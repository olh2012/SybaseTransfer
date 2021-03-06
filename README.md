## Sybase 数据库迁移

本项目基于Java语言实现了一个Sybase数据库迁移解决方案，并提供了跨版本迁移特性（已验证的版本是Sybase IQ 12.5 -> Sybase IQ 15.4）。

完整的数据库迁移步骤如下图：
![迁移步骤](https://raw.githubusercontent.com/olh2012/SybaseTransfer/master/doc/image/%E8%BF%81%E7%A7%BB%E6%AD%A5%E9%AA%A4.png "Sybase迁移步骤")

上面4个步骤之间是顺序执行的，在某个具体步骤里面，比如表数据迁移，则允许以并发的方式执行来提高效率。

同时，程序还提供了一些额外的操作支持，比如数据库对象初始化（会统计源库的对象数目并在目标库创建统计表），数据库对象清理（一般用于重复调度），迁移中断后继续执行等。

在代码实现上，抽象了以下三种概念模型：

* <strong>迁移对象</strong> 迁移对象与数据库对象对应，在本项目中迁移任务具体就是指`表`、`视图`和`存储过程`。
* <strong>过滤器</strong> 过滤器实现对目标迁移对象的一种筛选，比如要迁移指定用户模式的对象，就可以使用模式过滤器。目前程序中已经实现的过滤器有：`排除过滤器`、`名称列表过滤器`、`名称过滤器`、`OGNL表达式过滤器`、`正则表达式过滤器`、`名称前缀过滤器`、`用户模式过滤器`、`对象类型过滤器`、`排除用户模式过滤`。具体说明可直接参看代码，这里不作过多解释。
* <strong>迁移任务</strong> 迁移任务代表着在具体迁移过程中的一种操作，多种迁移任务可以形成一个任务组。目前代码中定义的任务类型有：`清除对象任务`、`初始化统计任务`、`对象迁移任务`、`数据迁移任务`。

程序的入口是 `src/com/forms/task/core/TaskExecute.java` 类中的 `main` 方法。支持的参数选项如下：

|短选项|长选项|包含参数|描述|
|---|---|---|---|
|h|help|否|打印帮助信息|
|tn|threadNumber|是|提交多个任务时，并发执行的线程数，小于或等于1时表示串行执行|
|cf|configFile|是|Spring配置文件，不能有空格|

在程序执行时，先要注册任务，这里提供了两种方式，一是通过XML配置实现，即通过`-cf 任务配置文件` 来实现（文件样例参考`resources/spring/spring-task.xml` ），二是通过命令行参数实现。

用于实现任务调度的额外参数选项如下：

|短选项|长选项|包含参数|描述|
|---|---|---|---|
|c|clear|否|执行清除对象任务|
|o|object|否|执行迁移对象任务|
|d|data|否|执行迁移数据任务|
|u|user|是|用户名称|
|n|name|是|单个对象名称|
|p|prefix|是|根据对象前缀过滤迁移对象|
|in|including|是|根据对象名称满足的正则表达式过滤迁移对象|
|ex|excluding|是|根据对象名称不满足的正则表达式过滤迁移对象|
|i|ignoreCase|是|名称比较时是否忽略大小写|

在实际执行时，一般是打成jar包用后台进程的的方式运行，以下为Linux环境下运行的参考命令：

```shell
nohup java -jar SybaseTransfer.jar -tn 10 -cf spring-task.xml >/dev/null 2>&1 &
```

建议或反馈： franka907@126.com
