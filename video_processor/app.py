#from flask import Flask
#from flask import request, jsonify
from kafka import KafkaConsumer,KafkaProducer 	
import json
import logging
import time
import os

#app = Flask(__name__)


#@app.route('/', methods=["POST"])
def execute_script(videoId):
    #videoId = request.json['videoId']
    status = os.system('npm start ' + videoId)
    if status == 0:
        return 0
    return 1

def produce_message(producer,message):
	
	
	future = producer.send(os.environ['KAFKA_PROCESSED_TOPIC'], message)
	try:
		record_metadata = future.get(timeout=10)
	except Exception:
		log.exception()
	logging.info (record_metadata.topic)
	logging.info (record_metadata.partition)
	logging.info (record_metadata.offset)


		


if __name__ == '__main__':
	#app.run(debug=True, host='127.0.0.1')
	logging.basicConfig(level=logging.INFO)
	producer = KafkaProducer(bootstrap_servers=os.environ['KAFKA_ADDRESS'],value_serializer=lambda x: x.encode('utf-8'))
	

	consumer = KafkaConsumer(
		os.environ['KAFKA_PROCESS_TOPIC'],
		bootstrap_servers=[os.environ['KAFKA_ADDRESS']],
		group_id= None,
		auto_offset_reset='earliest',
		value_deserializer=lambda x: x.decode('utf-8'))
	
	logging.debug('consumer initialized ')
	for msg in consumer:
		message_parts = msg.value.split('|')
		logging.info(message_parts)
		time.sleep(15)
		if execute_script(message_parts[1]) == 0:
			produce_message(producer,"processed|" + message_parts[1])
		else:
			produce_message(producer,"processingFailed|" + message_parts[1])
		
		
     
    
