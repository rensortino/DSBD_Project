cd vms/Docker
docker build -t vms -f Dockerfile-prod .
cd ../apigateway/Docker
docker build -t -f Dockerfile-prod apigateway .
cd ../video_processor/Docker
docker build -t -f Dockerfile-prod video_processor .

