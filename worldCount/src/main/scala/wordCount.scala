import org.apache.spark.{SparkContext, SparkConf}

/*
 *  Created by sage_wang on 2018/2/2.
 */
object WordCount {
        def main(args: Array[String]) {
        /**
         * ��1��������Spark�����ö���SparkConf������Spark��������ʱ��������Ϣ
         * ���� setAppName��������Ӧ�ó�������ƣ��ڳ������еļ�ؽ�����Կ��������ƣ�
         * setMaster���ó��������ڱ��ػ��������ڼ�Ⱥ�У������ڱ��ؿ���ʹ��local������Ҳ����ʹ��local[K]/local[*],
         * ����ȥspark�����鿴���ǲ�ͬ�����塣 ���Ҫ�����ڼ�Ⱥ�У���Standaloneģʽ���еĻ�����Ҫʹ��spark://HOST:PORT
         * ����ʽָ��master��IP�Ͷ˿ںţ�Ĭ����7077
         */
        val conf = new SparkConf().setAppName("WordCount").setMaster("local")
        //  val conf = new SparkConf().setAppName("WordCount").setMaster("spark://master:7077")  // �����ڼ�Ⱥ��

        /**
         * ��2��������SparkContext ����
         * SparkContext��Spark�������й��ܵ�Ψһ���
         * SparkContext�������ã� ��ʼ��SparkӦ�ó�����������Ҫ�ĺ������������DAGScheduler��TaskScheduler��SchedulerBackend
         * ͬʱ���Ḻ��Spark������Masterע�����
         *
         * ͨ������SparkConfʵ��������Spark���еľ��������������Ϣ
         */
        val sc = new SparkContext(conf)

        /**
         * ��3���� ���ݾ����������Դ(HDFS�� HBase��Local FS��DB�� S3��)ͨ��SparkContext������RDD
         * RDD �Ĵ������������ַ�ʽ�� �����ⲿ��������Դ(����HDFS)������Scala����ʹ��SparkContext��parallelize������
         * ��������RDD��������
         * ���ݻᱻRDD���ֳ�Ϊһϵ�е�Partitions�����䵽ÿ��Partition����������һ��Task�Ĵ�����
         */

//        val lines = sc.textFile("D:/resources/README.md")   // ��ȡ�����ļ�
        val lines = sc.textFile("/Users/shiqingwang/Desktop/worldCount/src/main/scala/README.md")

          //  val lines = sc.textFile("/library/wordcount/input")   // ��ȡHDFS�ļ������зֳɲ�ͬ��Partition
        //  val lines = sc.textFile("hdfs://master:9000/libarary/wordcount/input")  // ������ȷָ���Ǵ�HDFS�ϻ�ȡ����

        /**
         * ��4���� �Գ�ʼ��RDD����Transformation����Ĵ������� map��filter�ȸ߽׺��������о�������ݼ���
         */
        val words = lines.flatMap(_.split(" ")).filter(word => word != " ")  // ��ֵ��ʣ������˵��ո񣬵�Ȼ�����Լ������й��ˣ���ȥ��������

        val pairs = words.map(word => (word, 1))  // �ڵ��ʲ�ֵĻ����϶�ÿ������ʵ������Ϊ1, Ҳ���� word => (word, 1)

        val wordscount = pairs.reduceByKey(_ + _)  // ��ÿ������ʵ������Ϊ1�Ļ���֮��ͳ��ÿ���������ļ��г��ֵ��ܴ���, ��key��ͬ��value���
        //  val wordscount = pairs.reduceByKey((v1, v2) => v1 + v2)  // ��ͬ��

        wordscount.collect.foreach(println)  // ��ӡ�����ʹ��collect�Ὣ��Ⱥ�е������ռ�����ǰ����drive�Ļ����ϣ���Ҫ��֤��̨�����ܷŵ�����������

        sc.stop()   // �ͷ���Դ

        }
}