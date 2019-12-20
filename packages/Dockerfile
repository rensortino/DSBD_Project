# Image gathering all the packages required by the video_processor script

FROM python:3
COPY ./ ./
RUN pip install -r requirements.txt
RUN apt-get update
RUN apt-get -y install curl gnupg
RUN curl -sL https://deb.nodesource.com/setup_11.x  | bash -
RUN apt-get -y install nodejs	
RUN npm install
RUN apt-get -y upgrade
RUN apt-get install -y ffmpeg
