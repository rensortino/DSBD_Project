cd vms
sudo docker build -t vms .
cd ..
cd vps
sudo docker build -t vps .
cd ..
cd apigateway
sudo docker build -t apigateway .
cd ..
sudo docker-compose up

