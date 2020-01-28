cd ..
cd vms/k8s/ConfigMap/secret/
kubectl create secret generic vms-secret-file --from-env-file=./secret --save-config
kubectl get secret vms-secret-file -o yaml > vms-secret-file.yml
cd ../env
kubectl create configmap vms-env-file --from-env-file=./envVariable --save-config
kubectl get configmap vms-env-file -o yaml > vms-configmap.yml

cd ../../../../video_processor/k8s/ConfigMap
kubectl create configmap vps-env-file --from-env-file=./envVariable --save-config
kubectl get configmap vps-env-file -o yaml > vps-configmap.yml

cd ../../../apigateway/k8s/ConfigMap/
kubectl create configmap apigateway-env-file --from-env-file=./envVariable --save-config
kubectl get configmap apigateway-env-file -o yaml > apigateway-configmap.yml

cd ../../../alertManagement/k8s/ConfigMap/
kubectl create configmap alertmanagement-env-file --from-env-file=./envVariable --save-config
kubectl get configmap alertmanagement-env-file -o yaml > alertmanagement-configmap.yml

cd ../../../spout/k8s/ConfigMap/
kubectl create configmap spout-env-file --from-env-file=./envVariable --save-config
kubectl get configmap spout-env-file -o yaml > spout-configmap.yml
cd ../../../MongoDB/k8s/ConfigMap/secret/
kubectl create secret generic mongodb-secret-file --from-env-file=./secret --save-config
kubectl get secret mongodb-secret-file -o yaml > mongodb-secret-file.yml