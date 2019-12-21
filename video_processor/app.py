from flask import Flask
from flask import request, jsonify
import json
app = Flask(__name__)

import os

@app.route('/', methods=["POST"])
def execute_script():
    videoId = request.json['videoId']
    status = os.system('npm start ' + videoId)
    if status == 0:
        return jsonify({"error": "Invalid email"}), 201
    return jsonify({"error": "Invalid email"}), 500


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1')
