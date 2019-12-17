FROM python:3
COPY ./ ./app/
WORKDIR /app
ENV FLASK_APP=app.py
ENV FLASK_DEBUG=1
COPY requirements.txt ./
RUN pip install -r requirements.txt
CMD ["python", "-m", "flask", "run", "--host", "0.0.0.0"]
