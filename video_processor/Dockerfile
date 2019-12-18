FROM python:3
COPY ./ ./app/
WORKDIR /app
ENV FLASK_APP=app.py
ENV FLASK_DEBUG=1
COPY requirements.txt ./
RUN pip install -r requirements.txt
RUN apt-get update
RUN apt-get -y install curl gnupg
RUN curl -sL https://deb.nodesource.com/setup_11.x  | bash -
RUN apt-get -y install nodejs	
RUN npm install
RUN apt-get -y update
RUN apt-get -y upgrade
RUN apt-get install -y ffmpeg

CMD ["python", "-m", "flask", "run", "--host", "0.0.0.0"]
