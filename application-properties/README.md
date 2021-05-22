# Application Properties
This is an example of migrating an application that is bound to an external database, where the CF application declaratively creates a datasource in application.properties.

## Understanding This Use Case
Many Spring Boot applications contain an application.properties file. Of those Spring Boot apps that bind to a database, you will often find the database binding details in application.properties, referenced like this:
```
spring.datasource.url=${vcap.services.testdb.credentials.uri}
spring.datasource.username=${vcap.services.testdb.credentials.username}
spring.datasource.password=${vcap.services.testdb.credentials.password}
```
Or:
```
spring.datasource.url=${vcap.services.testdb.credentials.uri}
```
Or:
```
spring.datasource.url=jdbc:${vcap.services.testdb.credentials.uri}
```

Ultimately, this reference depends on the [VCAP_SERVICES](https://docs.cloudfoundry.org/devguide/deploy-apps/environment-variable.html#VCAP-SERVICES) environment variable. This is a JSON value that is automatically injected into a CF application that has at least one service binding defined in `manifest.yml`, or that has had the command `cf bind-service` invoked upon. The exact VCAP_SERVICES json format depends on the service being bound to, so one likely cannot assume a standard format.

### Understanding The Solution
Since applications like this are referencing the VCAP_SERVICES var in application.properties, the easiest way to migrate this type of app is to copy the VCAP_SERVICES variable into an OCP secret and consume this secret in OCP as an environment variable. **This is the solution demonstrated in this demo.**

You can also overwrite the application.properties file altogether. In this case, you could look at the VCAP_SERVICES var in CF, find the username/password/jdbc, save each in an OCP secret, and load each as environment variables. Then rewrite the application.properties file to look like this, for example:
```
spring.datasource.uri=${uri}
spring.datasource.username=${username}
spring.datasource.password=${password}
```

## CF Setup
See the [CF Setup](../common/cf-setup.md) doc.

## Migrating to OpenShift
First, in order to deploy the CF app into OpenShift, you need to create a container image. This can be done easily by using OpenShift's native build capabilities.

### Building the Application Image
See the [Building the Application Image](../common/build-application-image.md) doc.

### Deploying the Application
Run the following commands to deploy the CF application to OCP:
1. Get the CF application GUID
   ```bash
   export GUID=$(cf app sample-app --guid -q)
   ```
1. Save the VCAP_SERVICES json to a variable. The tool [jq](https://github.com/stedolan/jq) comes in handy here.
   ```bash
   export VCAP_SERVICES_JSON=$(cf curl /v2/apps/$GUID/env -q | jq -r .system_env_json.VCAP_SERVICES)
   ```
1. Create an OCP secret containing the VCAP_SERVICES json
   ```bash
   oc create secret generic vcap-services --from-literal=VCAP_SERVICES="$VCAP_SERVICES_JSON"
   ```
1. Deploy the app
   ```bash
   oc apply -f .openshift/deploy.yml
   ```
   Notice in [.openshift/deploy.yml](./.openshift/deploy.yml) the usage of `envFrom` to provide the VCAP_SERVICES environment variable
1. Ensure the app is connected to the database
   ```bash
   curl $(oc get route sample-app -o jsonpath='{.spec.host}')/findall
   ```
   This should return a JSON of customer names.

## Cleaning Up
Here are instructions for cleanup up your CF and OCP environments after running this demo:
1. Delete the sample-app from OCP
   ```bash
   oc delete -f .openshift/deploy.yml
   ```
1. Delete the sample-app from CF
   ```bash
   cf delete java-test
   ```
1. The [auto-reconfiguration](../auto-reconfiguration) demo uses the same database provisioned from this demo. If you want to follow that demo next, you can save some time by leaving the database running. Otherwise, you can delete the database with the following command:
   ```bash
   cf delete-service testdb
   ```