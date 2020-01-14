eval $(minikube docker-env)
kubectl apply -f ./kafka.yml
cd MongoDB
kubectl delete -f ./
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ..
cd vms
kubectl delete -f ./
docker build -t vms:v1 .
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ../apigateway
kubectl delete -f ./
docker build -t apigateway:v1 .
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ../video_processor
kubectl delete -f ./
docker build -t vps:v1 .
kubectl apply -f ./Service.yml
kubectl apply -f ./Deployment.yml
cd ../spout
kubectl delete -f ./
docker build -t spout:v1 .
kubectl apply -f .
cd ..

