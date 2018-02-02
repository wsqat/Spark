import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by sage_wang on 2018/2/1.
  */

object Hello {
  /* �����ҵĵ�һ�� Scala ����
   * ���³������'Hello World!'
   */
  def main(args: Array[String]) {
    println("Hello, world!") // ��� Hello World
    // /Users/shiqingwang/Desktop/worldCount/README.md
    // val logFile = "./README.md"  // Should be some file on your server.
    val logFile = "/Users/shiqingwang/Desktop/worldCount/src/main/scala/README.md"  // Should be some file on your server.
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("h")).count()
    val numBs = logData.filter(line => line.contains("j")).count()
    println("Lines with h: %s, Lines with j: %s".format(numAs, numBs))
  }
}


