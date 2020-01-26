
from kafka import KafkaConsumer,KafkaProducer 	
import os
import logging


logging.basicConfig(level=logging.INFO)
	

consumer = KafkaConsumer(
	"alert",
	bootstrap_servers=[os.environ['KAFKA_ADDRESS']],
	group_id= None,
	auto_offset_reset='earliest',
	value_deserializer=lambda x: x.decode('utf-8'))
	
logging.debug('consumer initialized ')
for msg in consumer:
	logging.info(msg)
		
		
     
    
