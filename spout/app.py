import os
import time
import requests
import json
import logging
from kafka import KafkaProducer




http_address = os.environ['PROMETHEUS_ADDRESS']+"/api/v1/query"
# Parameters of the query on Prometheus
params = [{'query':'time_request_seconds_sum'},{'query':'time_request_seconds_count'}]
producer = KafkaProducer(bootstrap_servers=os.environ['KAFKA_ADDRESS'],value_serializer=lambda x:x.encode('utf-8'))
logging.basicConfig(level=logging.INFO)
# Support variable to hold the last statistics values, in order to publsh on Kafka
# only the latest values
kafkastatsprev = ""
while 1:
    kafkaStats = ""
    for param in params:
        r = requests.get(http_address,params = param)

        for result in r.json()['data']['result']:
            name = result["metric"]["__name__"]
            kafkaStats += 'name;'+name+'|uri;'+result['metric']['URI']+'|value;'+result['value'][1] + '|method;'+result['metric']['method'] + ','
    if(kafkaStats != kafkastatsprev): 
        with open("./stats/stats.txt","a") as out:
            out.write(kafkaStats)
        producer.send(os.environ['KAFKA_STATS_TOPIC'], kafkaStats)
    kafkastatsprev = kafkaStats
    time.sleep(10)


    
