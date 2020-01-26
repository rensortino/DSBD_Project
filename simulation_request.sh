#!/bin/bash
  
while :
do
 curl  $(minikube ip):$1/vms/users/

 curl  $(minikube ip):$1/vms/videos/

 curl  $(minikube ip):$1/vms/videos/111111

 curl --data "param1=value1&param2=value2"  $(minikube ip):$1/vms/videos/

 curl --data "param1=value1&param2=value2"  $(minikube ip):$1/vms/users/register

 curl --data "param1=value1&param2=value2"  $(minikube ip):$1/vms/videos/111111
done



