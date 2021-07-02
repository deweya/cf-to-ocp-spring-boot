## Deploying Rabbit MQ 3.6.5 to Single Pod on Openshift

### Using custom public docker image

```
oc project rabbitmq-servers
oc new-app luiscoms/openshift-rabbitmq:3.6.5-management
oc expose svc/openshift-rabbitmq --port=15672 --name management
```

### Test that the server is up and running

Option 1.  After exposing the service, click on the route and login to the management ui with `guest / guest` credentials.


Option 2. Send and Consume message with local client.

```
# rabbitmq-client project will send messages to the oc client which will then forward to the amqp client in the rabbitmq server.
oc port-forward <podname> 5672:5672 
cd rabbitmq-client/
mvn clean install -DskipTests
export SPRING_PROFILES_ACTIVE=local
java -jar target/messaging-rabbitmq-0.0.1-SNAPSHOT.jar
```

You should see the following messages in your local terminal, press Ctrl+C to break from the process:
```
...
Sending message...
Received <Hello from RabbitMQ!>
^C
```

## References:
1. https://stackoverflow.com/questions/45569931/how-to-correctly-setup-rabbitmq-on-openshift
1. https://hub.docker.com/r/luiscoms/openshift-rabbitmq/