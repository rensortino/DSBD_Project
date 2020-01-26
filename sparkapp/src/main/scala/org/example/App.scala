    package org.example


import java.util.Properties

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}



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

    val props:Properties = new Properties()
    props.put("bootstrap.servers",sys.env("KAFKA_ADDRESS"))
    props.put("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks","all")
    val producer = new KafkaProducer[String, String](props)



    /* TRANSFORM the inputRDD into countRDD */
    val lines = stream.map(_.value)
    val stats = lines.flatMap(line =>line.split(","))
    // Takes only the valid fields of the statistics
    .filter(stats => stats.length > 1)
      .map(stats => {
      	// Reformat the stats string in a more manageable format
        //Dato che le statistiche offerte da Prometheus sono incrementali per elaborare statistiche relative al singolo batch, per ogni statistica dovremmo levare il valore della medesima del batch precendente
        // prima però dividiamo le statistiche in 2 parti: request_count e response_time_sum
        val stat = stats.split("""\|""")
        (stat(0).split(";")(1)+ "_" + stat(1).split(";")(1)+ "_" + stat(3).split(";")(1),  stat(2).split(";")(1).toDouble)
      }).reduceByKey((a,b)=> b)  // estrae l'ultimo valore di ciascuna statistica
      .map(stats => (
        if(stats._1.contains("sum")){"response_time_sum"}
        else if(stats._1.contains("count")){"request_count"}
        else {stats._1}
        ,stats._2)
      ).reduceByKey((a,b) => a+b).reduceByKeyAndWindow((a:Double,b:Double) => (b-a), Seconds(60), Seconds(30))

    val request_per_seconds = stats.filter(line => line._1.equals("request_count")).map(a => ("request_per_seconds", a._2/30))


      val elaboration = stats.reduce((a,b) => if(a._1.equals("response_time_sum") && b._1.equals("request_count")){("response_time_average",a._2 / b._2)} else {("response_time_average",b._2 / a._2)}).union(request_per_seconds)
        .window(Seconds(330), Seconds(30)).foreachRDD(rdd => {
        rdd.collect().foreach(x => println(x))
        if (rdd.collect().length == 22) {
          var time_avarage_media: Double = 0
          var request_per_seconds_media: Double = 0
          for (i <- 0 to rdd.collect().length - 2) {
            println(rdd.collect()(i)._1 + ":" + rdd.collect()(i)._2)
            if (rdd.collect()(i)._1.equals("response_time_average")) {
              time_avarage_media = time_avarage_media + rdd.collect()(i)._2
            }
            if (rdd.collect()(i)._1.equals("request_per_seconds")) {
              println("incremento media richieste")
              request_per_seconds_media = request_per_seconds_media + rdd.collect()(i)._2
            }

          }
          time_avarage_media = time_avarage_media / 10
          println("media tempo calcolata: " + time_avarage_media)
          request_per_seconds_media = request_per_seconds_media / 10
          println("media richieste calcolata: " + request_per_seconds_media)
          println("ultimo tempo di risposta medio calcolato: " + rdd.collect()(20)._2)
          println("ultime richieste medie calcolate:" + rdd.collect()(21)._2)

          if (rdd.collect()(20)._2 > (6 / 5) * time_avarage_media && rdd.collect()(21)._2 > (6 / 5) * request_per_seconds_media) {
            var aumento_perc_response_time: Double = 0
            var aumento_perc_request_per_seconds: Double = 0

            aumento_perc_request_per_seconds = ((rdd.collect()(21)._2 * 100) / request_per_seconds_media) - 100

            aumento_perc_response_time = ((rdd.collect()(20)._2 * 100) / time_avarage_media) - 100

            alert_function("tempo medio di risposta", aumento_perc_response_time, aumento_perc_request_per_seconds,producer)
          }
          else if (rdd.collect()(20)._2 > (6 / 5) * time_avarage_media) {
            var aumento_perc_response_time: Double = 0


            aumento_perc_response_time = ((rdd.collect()(20)._2 * 100) / time_avarage_media) - 100

            alert_function1("tempo medio di risposta", aumento_perc_response_time, producer)
          }
          else
            println("nulla da segnalare")


        }
        else println("non ho raccolto abbastanza metrice")
      })

    // Start the computation
    ssc.start()
    ssc.awaitTermination()

  }

  def alert_function (metrica : String, incremento_time : Double, incremento_request : Double, producer : KafkaProducer[String, String]) = {

    println("incremento del " + metrica + " pari a " + incremento_time +  "%" + ". Registrato anche un incremento del numero medio di richieste pari a: " + incremento_request + "%" )
    try {
      val record = new ProducerRecord[String, String]("alert", "incremento del " + metrica + " pari a " + incremento_time + "%" + ". Registrato anche un incremento del numero medio di richieste pari a: " + incremento_request + "%")
      val metadata = producer.send(record)
      println("sent record(key=%s value=%s) " +
        "meta(partition=%d, offset=%d)",
        record.key(), record.value(),
        metadata.get().partition(),
        metadata.get().offset())
    }
      catch{
        case e:Exception => e.printStackTrace()
      }



  }

  def alert_function1 (metrica : String, incremento_time : Double, producer : KafkaProducer[String, String]) = {

    println("incremento del " + metrica + " pari a " + incremento_time +  "%" + ". Non è stato registrato un incremento considerevole del numero medio di richieste." )
    try {
      val record = new ProducerRecord[String, String]("alert", "incremento del " + metrica + " pari a " + incremento_time +  "%" + ". Non è stato registrato un incremento considerevole del numero medio di richieste." )
      val metadata = producer.send(record)
      println("sent record(key=%s value=%s) " +
        "meta(partition=%d, offset=%d)",
        record.key(), record.value(),
        metadata.get().partition(),
        metadata.get().offset())
    }

    catch{
      case e:Exception => e.printStackTrace()
    }


  }



}

