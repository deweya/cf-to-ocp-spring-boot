# Auto-Reconfiguration
This app does not contain any datasource beans or properties required to connect to an external MySQL database. As a result, CF will auto-reconfigure the Spring Boot application by injecting a datasource bean created by the java_buildpack, on `cf push`.

## Understanding This Use Case
Some Spring Boot apps in CF bind to a database but do not have any datasource configurations in application.properties or in code. CF is able to detect that the datasource is missing and will inject a datasource automatically based on the VCAP_SERVICES credentials.

### Understanding The Solution
Similar to the [application-properties](../application-properties) demo, there are a couple of easy ways to migrate this app. The first way is to simply copy the VCAP_SERVICES var from CF, save it to an OCP secret, load it as an environment variable, and reference it in application.properties.

However, since an application like this is already not using VCAP_SERVICES, we may as well pick and choose only the fields we need from VCAP_SERVICES, save _those_ fields in an OCP secret, and reference them in application.properties like this:
```
spring.datasource.url=jdbc:${uri}
```
**This is the solution demonstrated in this demo.**

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
1. Save the datasource uri to a variable:
   ```bash
   export URI=$(echo $VCAP_SERVICES_JSON | jq -r '."compose-for-mysql"[0].credentials.uri')
   ```
1. Save the URI as an OCP secret
   ```bash
   oc create secret generic sample-app-uri --from-literal=URI=${URI}
   ```
1. Deploy the app
   ```bash
   oc apply -f .openshift/deploy.yml
   ```
   Notice in [.openshift/deploy.yml](./.openshift/deploy.yml) the ConfigMap that is being created, which references the URI environment variable (added by envFrom in the deployment).
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
1. The [application-properties](../application-propertoes) demo uses the same database provisioned from this demo. If you want to follow that demo next, you can save some time by leaving the database running. Otherwise, you can delete the database with the following command:
   ```bash
   cf delete-service testdb
   ```