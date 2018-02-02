## 一、开发环境
> IDEA
### 1、Scala插件
打开IDEA -> Configure -> Plugins -> 选择Scala -> Install

### 2、Scala的Jar包
File -> Project Structure -> Global Libraries -> "+" -> Java -> 选择本地Scala的lib库文件
![spark.png](http://upload-images.jianshu.io/upload_images/688387-20d5447d27d36877.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 3、Spark的Jar包
> 解决问题 "找不到或无法加载主类"
File -> Project Structure -> Libraries -> "+" -> Java -> 选择本地Spark的jar文件
![Spark.png](http://upload-images.jianshu.io/upload_images/688387-76e5fabf668fb1b7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



## 二、代码

### 1、build.sbt
```
name := "worldCount"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.1"
```

### 2、在main/scala/下新建README.md
```
suse lusy
hhhjjj
suse lusy
tom jerry
suse lusy
henry jim
suse lusy
```


### 3、在main/scala/下新建WordCount.scala
```
import org.apache.spark.{SparkContext, SparkConf}

/*
 *  Created by sage_wang on 2018/2/2.
 */
object WordCount {
        def main(args: Array[String]) {
        /**
         * 第1步；创建Spark的配置对象SparkConf，设置Spark程序运行时的配置信息
         * 例如 setAppName用来设置应用程序的名称，在程序运行的监控界面可以看到该名称，
         * setMaster设置程序运行在本地还是运行在集群中，运行在本地可是使用local参数，也可以使用local[K]/local[*],
         * 可以去spark官网查看它们不同的意义。 如果要运行在集群中，以Standalone模式运行的话，需要使用spark://HOST:PORT
         * 的形式指定master的IP和端口号，默认是7077
         */
        val conf = new SparkConf().setAppName("WordCount").setMaster("local")
        //  val conf = new SparkConf().setAppName("WordCount").setMaster("spark://master:7077")  // 运行在集群中

        /**
         * 第2步：创建SparkContext 对象
         * SparkContext是Spark程序所有功能的唯一入口
         * SparkContext核心作用： 初始化Spark应用程序运行所需要的核心组件，包括DAGScheduler、TaskScheduler、SchedulerBackend
         * 同时还会负责Spark程序往Master注册程序
         *
         * 通过传入SparkConf实例来定制Spark运行的具体参数和配置信息
         */
        val sc = new SparkContext(conf)

        /**
         * 第3步： 根据具体的数据来源(HDFS、 HBase、Local FS、DB、 S3等)通过SparkContext来创建RDD
         * RDD 的创建基本有三种方式： 根据外部的数据来源(例如HDFS)、根据Scala集合使用SparkContext的parallelize方法、
         * 由其他的RDD操作产生
         * 数据会被RDD划分成为一系列的Partitions，分配到每个Partition的数据属于一个Task的处理范畴
         */

//        val lines = sc.textFile("D:/resources/README.md")   // 读取本地文件
        val lines = sc.textFile("/Users/shiqingwang/Desktop/worldCount/src/main/scala/README.md")

          //  val lines = sc.textFile("/library/wordcount/input")   // 读取HDFS文件，并切分成不同的Partition
        //  val lines = sc.textFile("hdfs://master:9000/libarary/wordcount/input")  // 或者明确指明是从HDFS上获取数据

        /**
         * 第4步： 对初始的RDD进行Transformation级别的处理，例如 map、filter等高阶函数来进行具体的数据计算
         */
        val words = lines.flatMap(_.split(" ")).filter(word => word != " ")  // 拆分单词，并过滤掉空格，当然还可以继续进行过滤，如去掉标点符号

        val pairs = words.map(word => (word, 1))  // 在单词拆分的基础上对每个单词实例计数为1, 也就是 word => (word, 1)

        val wordscount = pairs.reduceByKey(_ + _)  // 在每个单词实例计数为1的基础之上统计每个单词在文件中出现的总次数, 即key相同的value相加
        //  val wordscount = pairs.reduceByKey((v1, v2) => v1 + v2)  // 等同于

        wordscount.collect.foreach(println)  // 打印结果，使用collect会将集群中的数据收集到当前运行drive的机器上，需要保证单台机器能放得下所有数据

        sc.stop()   // 释放资源

        }
}
```

### 4、选中wordCount -> 右击运行wordCount

![wordCount.png](http://upload-images.jianshu.io/upload_images/688387-fc973631c317d87f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)




## Q & A

### 1、spark编译问题解决 object apache is not a member of package org
> 解决方案：File -> Project Structure -> Libraries -> "+" -> Java -> 选择本地Spark的jar文件

根本原因，缺少相应的spark包。老版本的spark直接导入spark-assembly_2.10-0.9.0-incubating-hadoop2.2.0.jar。
但2.0版的没有assembly的包了，在jar里面，是很多小jar包，所有JAR包都导进去即可
1.6以前的应该都有。


### 2、java.lang.ClassNotFoundException: scala.Product$class
原因是scala版本问题，将最新版本2.12.4切换到低版本 2.11.X 即可。
注意如下：
`
groupId: org.apache.spark
artifactId: spark-core_2.11
version: 2.2.1
`
官网上已有说明
![spark.png](http://upload-images.jianshu.io/upload_images/688387-7ea79b167457fd79.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
故我的build.sbt如下
```
name := "worldCount"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.1"
```


### 3、解决SBT下载慢的问题
sbt运行时经常需要下载大量的jar包，默认连接到maven官网，速度通常比较慢。我想把国内的一些maven库添加到sbt的全局设置里，并且先尝试它们，这样就不必修改每个sbt项目。
在老猪的帮助下，最终无比郁闷的知道了解决方法：
在`~/.sbt/`下添加一个`repositories`文件，里面内容如下：
```
[repositories]
local
osc: http://maven.oschina.net/content/groups/public/
typesafe: http://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
sonatype-oss-releases
maven-central
sonatype-oss-snapshots
```

## 附录
github地址：https://github.com/wsqat/Spark/worldCount