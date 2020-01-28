eval $(minikube docker-env)
cd ..
kubectl apply -f ./kafka.yml
cd MongoDB/k8s
kubectl delete -f ./
kubectl apply -f .
cd ../../vms/
docker build -t vms:v1 -f Dockerfile-prod .
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f .
cd ../../apigateway/
docker build -t apigateway:v1 -f Dockerfile-prod .
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f .
cd ../../video_processor/
docker build -t vps:v1 -f Dockerfile-prod .
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f .
cd ../../spout/
docker build -t spout:v1 -f Dockerfile .
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f ./
cd ../../alert/
docker build -t alert:v1 -f Dockerfile .
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f ./
