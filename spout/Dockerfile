FROM python:3
COPY ./ ./app/
WORKDIR /app
#ENV FLASK_APP=app.py
#ENV FLASK_DEBUG=1
RUN pip install requests && \
	pip install simplejson && \
	pip install kafka-python

#CMD ["python", "-m", "flask", "run", "--host", "0.0.0.0"]
CMD ["python", "app.py"]
