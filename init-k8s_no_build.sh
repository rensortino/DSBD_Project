eval $(minikube docker-env)
kubectl apply -f ./kafka.yml
cd MongoDB/k8s
kubectl delete -f ./
kubectl apply -f .
cd ../../vms/
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f .
cd ../../apigateway/
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f .
cd ../../video_processor/
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f .
cd ../../spout/
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f ./
cd ../../alert/
cd ./k8s
kubectl delete -f ./ 
kubectl apply -f ./
