Prereqs:
1. Make sure you are logged in as a cluster admin or else you will see the following error messages:

```
Error from server (Forbidden): error when retrieving current configuration of:
Resource: "/v1, Resource=namespaces", GroupVersionKind: "/v1, Kind=Namespace"
Name: "rabbitmq-system", Namespace: ""
...
from server for: "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml": namespaces "rabbitmq-system" is forbidden: User "developer" cannot get resource "namespaces" in API group "" in the namespace "rabbitmq-system"
```

```

Error from server (Forbidden): error when creating "rabbitmq-server.yaml": rabbitmqclusters.rabbitmq.com is forbidden: User "developer" cannot create resource "rabbitmqclusters" in API group "rabbitmq.com" in the namespace "rabbitmq-servers"
```

These instructions are just a consolodated/tweaked version of below references with oc cli instead of kubectl.


Deploy an operator instance
```
cd rabbitmq-server-cluster-via-customized-kubernetes-operator/
oc create -f cluster-operator.yml
```

Then deploy the actual rabbit mq instance
```
oc project rabbitmq-servers
oc create -f rabbitmq-server.yaml
```

Get generated credentials for the client from secret rabbitmq-server-default-user in the namespace rabbitmq-servers.

## References:
1. https://www.rabbitmq.com/kubernetes/operator/install-operator.html
1. https://www.rabbitmq.com/kubernetes/operator/using-operator.html#openshift