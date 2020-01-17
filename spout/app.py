import os
import time
import requests
import json
import logging
from kafka import KafkaProducer




http_address = os.environ['PROMETHEUS_ADDRESS']+"/api/v1/query"
params = [{'query':'user_get_timer_seconds_sum'},{'query':'users_register_timer_seconds_sum'},{'query':'videofiles_get_id_seconds_sum'},{'query':'videos_get_id_seconds_sum'},
{'query':'videos_get_seconds_sum'},{'query':'videos_post_id_seconds_sum'},{'query':'videos_post_seconds_sum'},{'query':'rate(request_counter_total[10s])'},]
producer = KafkaProducer(bootstrap_servers=os.environ['KAFKA_ADDRESS'],value_serializer=lambda x:x.encode('utf-8'))
logging.basicConfig(level=logging.INFO)
kafkastatsprev = ""
while 1:
    kafkaStats = ""
    for param in params:
        r = requests.get(http_address,params = param)

        for result in r.json()['data']['result']:
            if "__name__" not in result["metric"]:
                name = "requestPerSeconds"
            else:
                name = result["metric"]["__name__"]
            kafkaStats += 'name:'+name+'|uri:'+result['metric']['URI']+'|value:'+result['value'][1]+','
    if(kafkaStats != kafkastatsprev): 
        producer.send(os.environ['KAFKA_STATS_TOPIC'], kafkaStats)
        logging.info(kafkaStats)
    kafkastatsprev = kafkaStats
    logging.info(kafkaStats)
    time.sleep(10)


    
