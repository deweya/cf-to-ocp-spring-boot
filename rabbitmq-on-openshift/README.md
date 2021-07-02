Prereqs:
1. Make sure you follow the setup instructions for ibmcloud and cf at the root of this repo.

## Prepare rabbitmq server/client on Cloud Froundry
### Deploy a rabbitmq server to cloud foundry. 
```
cf create-service compose-for-rabbitmq Standard rabbitmq-testserver
```


###  Deploy rabbitmq client to cloud foundry. 

```
cd rabbitmq-client/
mvn clean install -DskipTests
cf push
```

Afterwards you should be able to look at the logs of the client in ibmcloud and see that it both sent a message and received a message from the rabbitmq server.
```
Sending message...
Received <Hello from RabbitMQ!>
```


## Migrate to Openshift
There are different approaches with the following as one such way. Migrate both the server and the client bottom up (or first server then client).

Prereqs: 
1. Have a Openshift projects/namespaces to house the server and client apps. 
```
oc new-project rabbitmq-servers
oc new-project rabbitmq-clients
```

### Migrate rabbitmq server
There are two approaches to the rabbitmq server on Openshift. The first way is probably easier as you don't need to work with an admin but the second way has a more recent version of rabbitmq (3.8.16 > 3.6.5). Also the second way is more scalable. Whichever way you pick please follow their respective instructions to continue the guide.

1. We can have 1 pod for the server (non cluster). Doesn't require working with an admin. Please follow [instructions](./rabbitmq-server-noncluster/README.md)
OR
1. Have a whole system of configuration in place with a set of scalable pods (cluster). Requires working with an admin. Please follow [instructions](./rabbitmq-server-cluster-via-customized-kubernetes-operator/README.md)


### Migrate rabbitmq client
Please follow [instructions](./rabbitmq-client/README.md)