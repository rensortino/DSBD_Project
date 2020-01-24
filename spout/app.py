import os
import time
import requests
import json
import logging
from kafka import KafkaProducer




http_address = os.environ['PROMETHEUS_ADDRESS']+"/api/v1/query"
params = [{'query':'time_request_seconds_sum'},{'query':'time_request_seconds_count'}]
producer = KafkaProducer(bootstrap_servers=os.environ['KAFKA_ADDRESS'],value_serializer=lambda x:x.encode('utf-8'))
logging.basicConfig(level=logging.INFO)
kafkastatsprev = ""
while 1:
    kafkaStats = ""
    for param in params:
        r = requests.get(http_address,params = param)

        for result in r.json()['data']['result']:
            logging.info(result)    
            name = result["metric"]["__name__"]
            kafkaStats += 'name;'+name+'|uri;'+result['metric']['URI']+'|value;'+result['value'][1] + '|method;'+result['metric']['method'] + ','
        logging.info(kafkaStats)
    if(kafkaStats != kafkastatsprev): 
        with open("./stats/stats.txt","a") as out:
            out.write(kafkaStats)
        producer.send(os.environ['KAFKA_STATS_TOPIC'], kafkaStats)
        logging.info(kafkaStats)
    kafkastatsprev = kafkaStats
    logging.info(kafkaStats)
    time.sleep(10)


    
