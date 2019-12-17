from flask import Flask
app = Flask(__name__)

import os

@app.route('/')
def execute_script():
    os.system('bash process_video.sh')


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1')
