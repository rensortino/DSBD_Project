import os
import time
import requests
import simplejson
import logging
from kafka import KafkaProducer




http_address = os.environ['PROMETHEUS_ADDRESS']+"/api/v1/query"
params = [{'query':'gateway_requests_seconds_count'},{'query':'gateway_requests_seconds_max'},{'query':'gateway_requests_seconds_sum'},{'query':'rate(gateway_requests_seconds_count[10s])'}]
producer = KafkaProducer(bootstrap_servers=os.environ['KAFKA_ADDRESS'],value_serializer=lambda x:x.encode('utf-8'))
logging.basicConfig(level=logging.INFO)
while 1:
    statsFile = open("./stats/stats.json", "w")
    for param in params:
        r = requests.get(http_address,params = param)
        logging.info(type(r.json()))
        statsFile.write(simplejson.dumps(r.json(), indent=4))
        for result in r.json()['data']['result']:
            if "__name__" not in result["metric"]:
                name = "requestPerSeconds"
            else:
                name = result["metric"]["__name__"]
            kafkaStats = "{'name':'"+name+"', 'uri':'"+result["metric"]["routeUri"]+"' , 'value': '"+result["value"][1]+"' }"
            logging.info(kafkaStats)
            producer.send(os.environ['KAFKA_STATS_TOPIC'], kafkaStats)
    
    time.sleep(10)


    
