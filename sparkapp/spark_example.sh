
/usr/local/spark/bin/spark-submit --master spark://localhost:7077  \
--deploy-mode cluster \
--name spark-pi \
--class org.example.App \
--conf spark.kubernetes.namespace=spark \
 ./target/sparkapp-1.0-SNAPSHOT-jar-with-dependencies.jar