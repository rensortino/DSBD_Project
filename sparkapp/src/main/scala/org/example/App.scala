  package org.example

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
 * @author ${user.name}
 */
object App {

  case class Stats(name: String, uri: String, value: String)
  def main(args : Array[String]) {
    /*
    // Create context with 30 second batch interval


    /* Configure Kafka */
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> sys.env("KAFKA_ADDRESS"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test_group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array(sys.env("KAFKA_STATS_TOPIC"))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent, //Location strategy
      Subscribe[String, String](topics, kafkaParams)
    )


    val conf = new SparkConf().setAppName("spark-kafka")
    val ssc = new StreamingContext(conf, Seconds(30))

    val lines = ssc.t
    Console.print(lines)
    //val count = lines.flatMap(line => line.split(" ")).filter(word => word.contains("#")).map(word => (word, 1))
    //val reduced = count.reduceByKey(_ + _)
    //reduced.saveAsTextFiles("hdfs://resources/tweet_hashtags")

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
    */
    val sparkConf = new SparkConf().setAppName("word-count")
    val sc = new SparkContext(sparkConf)

    //val input = sc.textFile("file:///home/my_user/input.txt")
    val input = sc.textFile("file:///home/simoneonesta/DSBD_Project/storage/stats/stats.txt")
    /* TRANSFORM the inputRDD into countRDD */
    val count = input.flatMap(line =>line.split(",")).filter(stats => stats.length > 1)
      .map(stats => {
        val stat = stats.split("""\|""")
        (stat(0).split(":")(1), stat(2).split(":")(1).toDouble)
      }).reduceByKey((a,b) => b) // estrae l'ultimo valore di ciascuna statistica
      .map(stat => (if(stat._1.contains("sum")){
      "Response_time_sum"
    }
      else{"Request_Per_Seconds"},
      stat._2)).reduceByKey((a,b) => a+b)
    
    count.

      //.flatMap(keyvalues => keyvalues.split(":"))
    /* saveAsTextFile method is an ACTION that effects on the RDD */
    count.saveAsTextFile("file:///home/simoneonesta/DSBD_Project/storage/stats/stats1")
    //reduced.saveAsTextFile("file:///home/my_user/output_dir")

  }


}

