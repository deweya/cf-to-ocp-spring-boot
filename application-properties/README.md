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
These instructions assume you have access to CF and OCP environments and are logged into each.

### Provisioning a MySQL database
This demo attempts to simulate a situation where you are migrating a CF app that is bound to an external database. Often, this is done using [User Provided Services](https://docs.cloudfoundry.org/devguide/services/user-provided.html). Both external and internal services appear the same to a CF app, and both are displayed when running `cf services`. So, for this demo, we will provision a MySQL database from the CF Marketplace. This will create an internal database, but since a CF app doesn't know the difference between internal or external, this will work for the demo.

Follow these steps to provision a MySQL database:
1. Create the MySQL database
   ```bash
   cf create-service compose-for-mysql Standard testdb
   ```
1. Confirm the database was created
   ```bash
   cf services
   ```

### Deploying the CF Application
CF applications are configured in code using an [App Manifest](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html). The app manifest for this application is [manifest.yml](./manifest.yml). One of the important fields in this file is `services`, which lists the service that we created, `testdb`.

Run the following commands (from this directory) to deploy your application:
1. Build the code locally to produce a Jar
   ```
   mvn clean install
   ```
1. Push your CF application
   ```
   cf push
   ```
1. Confirm your application was deployed successfully
   ```
   cf apps
   ```

## Migrating to OpenShift
First, in order to deploy the CF app into OpenShift, you need to create a container image. This can be done easily by using OpenShift's native build capabilities.

### Building the Application Image
Run the following commands to build your application image:
1. Create a new build
   ```bash
   oc new-build --name=sample-app --binary --image-stream=java:11
   ```
1. Run the build
   ```bash
   oc start-build sample-app --from-dir=target --follow
   ```

Now that the image is built, you can continue with the deployment.

### Deploying the Application
Run the following commands to deploy the CF application to OCP:
1. Get the CF application GUID
   ```bash
   export GUID=$(cf app <app-name> --guid -q)
   ```
1. Save the VCAP_SERVICES json to a variable. The tool [jq](https://github.com/stedolan/jq) comes in handy here.
   ```bash
   export VCAP_SERVICES_JSON=$(cf curl /v2/apps/$GUID/env -q | jq -r .system_env_json.VCAP_SERVICES)
   ```
1. Create an OCP secret containing the VCAP_SERVICES json
   ```bash
   oc create secret generic vcap-services --from-literal=VCAP_SERVICES="$VCAP_SERVICES_JSON"
   ```