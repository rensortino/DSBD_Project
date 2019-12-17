cd vms
sudo docker build -t vms .
cd ..
cd vps
sudo docker build -t vps .
cd ..
sudo docker-compose up

