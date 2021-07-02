Prereqs:
1. You setup cloudfoundry apps as described [here](../README.md)
2. Make sure you have a rabbitmq server set up on openshift as described either [here](../rabbitmq-rabbitmq-server-noncluster/README.md) or [here](../rabbitmq-server-cluster-via-customized-kubernetes-operator/README.md).

Create a build config to create a new application image similiar to these [build instructions](https://github.com/deweya/cf-to-ocp-spring-boot/blob/main/common/building-application-image.md):
```
oc project rabbitmq-clients
oc new-build --name=rabbitmq-client --binary --image-stream=java:11
cd rabbitmq-client
mvn clean install -DskipTests
oc start-build rabbitmq-client --from-dir=target --follow
```

Then copy the vcap-services over from cloud foundry similiary described [here](https://github.com/deweya/cf-to-ocp-spring-boot/tree/main/application-properties).
```
export GUID=$(cf app rabbitmq-testclient --guid -q)
export VCAP_SERVICES_JSON=$(cf curl /v2/apps/$GUID/env -q | jq -r .system_env_json.VCAP_SERVICES)
oc create secret generic vcap-services --from-literal=VCAP_SERVICES="$VCAP_SERVICES_JSON"
```

Afterwards, edit the secret named vcap-services and replace value for field compose-for-rabbitmq[0].credentials.uri in the json:
from a value that looks like this
```
...
        "uri": "amqps://admin:XXZDQ...@portal-ssl1698-49...composedb.com:29362/...",
...
```
to a value that looks like this (for the rabbitmq-server service fqdn that got stood up on Openshift earlier):
```
...
        "uri": "amqp://guest:guest@rabbitmq-server.rabbitmq-servers.svc.cluster.local:5672",
...
```
Caution: credentials could be different. If you deployed the non cluster than guest / guest is 
included. Otherwise if you deployed the cluster via operator, get generated credentials for the client from secret rabbitmq-server-default-user in the namespace rabbitmq-servers.


Finally deploy the client:
```
oc apply -f .openshift/deploy.yml
```

Look at the pod logs and you should see:
```
Sending message...
Received <Hello from RabbitMQ!>

```