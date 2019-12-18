from flask import Flask
from flask import request,Response
import json
app = Flask(__name__)

import os

@app.route('/', methods=["POST"])
def execute_script():
    videoId = request.get_json()['videoId']
    try:
        os.system('npm start ' + videoId + ' | out.txt')
    except Exception as error:
        error_string = '{"status":"error","message":"'+error+'"}'
        return Response(json.loads(error_string),mimetype='application/json',status = 500)
    response_string = '{"status":"completed","message":"ok"}'
    return Response(json.loads(response_string),mimetype='application/json',status = 201)


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1')
