eval $(minikube docker-env)
kubectl apply -f ./kafka.yml
cd MongoDB/k8s
kubectl delete -f ./
kubectl apply -f .
cd ../../vms/k8s
kubectl delete -f ./
docker build -t vms:v1 -f Dockerfile-prod ../Docker
kubectl apply -f .
cd ../../apigateway/k8s
kubectl delete -f ./
docker build -t apigateway:v1 -f Dockerfile-prod ../Docker
kubectl apply -f .
cd ../../video_processor/k8s
kubectl delete -f ./
docker build -t vps:v1 -f Dockerfile-prod ../Docker
kubectl apply -f .
cd ../../spout/k8s
kubectl delete -f ./
docker build -t spout:v1 -f Dockerfile-prod ../Docker
kubectl apply -f .
