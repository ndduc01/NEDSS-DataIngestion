kubectl delete configmap --all
kubectl delete service --all
kubectl delete pod --all

minikube stop
sleep 5

minikube start
sleep 2

kubectl apply -f $HOME/envfiles/configmaps/local/mac-apps-configs.yml
sleep 5

kubectl apply -f mac-apps-setups.yml
sleep 12

kubectl port-forward service/jwtgenerator 8000:8000&
sleep 2

kubectl port-forward service/phinadapter 8090:8090&
sleep 2

kubectl get pod
