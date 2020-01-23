    package org.example


import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf}
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
    // Create context with 30 second batch interval

    val conf = new SparkConf().setAppName("spark-kafka")
    val ssc = new StreamingContext(conf, Seconds(sys.env("BATCH_SIZE").toInt))
    /* Configure Kafka */
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> sys.env("KAFKA_ADDRESS"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test_group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array(sys.env("TOPIC"))
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent, //Location strategy
      Subscribe[String, String](topics, kafkaParams)
    )



    /* TRANSFORM the inputRDD into countRDD */
    val lines = stream.map(_.value)
    val stats = lines.flatMap(line =>line.split(",")).filter(stats => stats.length > 1)
      .map(stats => {
        val stat = stats.split("""\|""")
        (stat(0).split(":")(1), stat(2).split(":")(1).toDouble)
      }).reduceByKey((a,b) => b)//.reduceByKeyAndWindow((a,b)=> b-a , 60,30)

    // estrae l'ultimo valore di ciascuna statistica

      val time_avarage = stats.map(stat => (if(stat._1.contains("sum")){
      "Response_time_sum"
    }
      else stat._1,
      stat._2)).reduceByKey((a,b) => a+b)



    


    time_avarage.map(stat => (
      if (stat._1.equals("Response_time_sum")) {
      "numeratore"
      }
      else if (stat._1.equals("request_counter_total")) {
        "denominatore"
    }
      else
      {stat._1}
      ,stat._2))
      .filter(stat => stat._1.equals("numeratore") || stat._1.equals("denominatore"))
      .reduce((a,b) =>{
        if(a._1.equals("numeratore") && b._1.equals("denominatore"))
      {
        ("avarage",a._2 / b._2)}
        else if(a._1.equals("denominatore") && b._1.equals("numeratore"))
        {
        ("avarage",b._2 / a._2)
      }
        else {
          (a._1,a._2)
        }
      }
      ).foreachRDD(rdd => rdd.collect().foreach(x => println(x._1 + ":" + x._2)))

    val request_per_seconds = stats.filter(stat => !stat._1.contains("sum"))//.reduceByKeyAndWindow((a,b)=> b -a ,60,30)
        .map(stat => (stat._1,stat._2/30))
    













    // Start the computation
    ssc.start()
    ssc.awaitTermination()

  }


}

