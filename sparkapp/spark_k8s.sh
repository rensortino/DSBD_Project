eval $(minikube docker-env)
/usr/local/spark/bin/spark-submit \
    --master k8s://https://192.168.39.26:8443 \
    --deploy-mode cluster \
    --name stats_elaboration \
    --class org.example.App \
    --conf spark.kubernetes.authenticate.driver.serviceAccountName=spark \
    --conf spark.kubernetes.driverEnv.KAFKA_ADDRESS=kafkaa:9092 \
    --conf spark.kubernetes.driverEnv.TOPIC=stats \
    --conf spark.kubernetes.driverEnv.BATCH_SIZE=30 \
    --conf spark.kubernetes.container.image=spark:v1     \
    --conf spark.kubernetes.driver.pod.name=sparkapp  \
    local:///opt/spark/work-dir/sparkapp-1.0-SNAPSHOT-jar-with-dependencies.jar
