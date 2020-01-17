
spark-submit --master  k8s://https://192.168.39.26:8443 \
--deploy-mode cluster \
--name spark-pi \
--class org.apache.spark.examples.SparkPi \
 --conf spark.kubernetes.namespace=spark \
 --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark-sa \
--conf spark.executor.instances=2 \
--conf spark.kubernetes.container.image.pullPolicy=Always \
--conf spark.kubernetes.container.image=lightbend/spark:2.0.1-OpenShift-2.4.0-rh \
 local:///opt/spark/examples/jars/spark-examples_2.11-2.4.0.jar