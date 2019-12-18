from flask import Flask
from flask import request
app = Flask(__name__)

import os

@app.route('/', methods=["POST"])
def execute_script():
    videoId = request.get_json()['videoId'];
    os.system('bash process_video.sh ' + videoId)


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1')
