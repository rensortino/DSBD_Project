cd vms
sudo docker build -t vms .
cd ../apigateway
sudo docker build -t apigateway .
cd ../video_processor
sudo docker build -t video_processor .
cd ..
sudo docker-compose up

