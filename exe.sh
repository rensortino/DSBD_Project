eval $(minikube docker-env)
cd MongoDB
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ..
cd vms
docker build -t vms:v1 .
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ../apigateway
docker build -t apigateway:v1 .
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ../video_processor
docker build -t vps:v1 .
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ..

