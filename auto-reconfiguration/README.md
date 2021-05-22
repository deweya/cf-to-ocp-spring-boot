# Auto-Reconfiguration
This app does not contain any datasource beans or properties required to connect to an external MySQL database. As a result, PCF will auto-reconfigure the Spring Boot application by injecting a datasource bean created by the java_buildpack, on `cf push`.

## Understanding This Use Case
Some Spring Boot apps in CF bind to a database but do not have any datasource configurations in application.properties or in code. CF is able to detect that the datasource is missing and will inject a datasource automatically based on the VCAP_SERVICES credentials.

### Understanding The Solution
Similar to the [application-properties](../application-properties) demo, there are a couple of easy ways to migrate this app. The first way is to simply copy the VCAP_SERVICES var from CF, save it to an OCP secret, load it as an environment variable, and reference it in application.properties.

However, since an application like this is already not using VCAP_SERVICES, we may as well pick and choose only the fields we need from VCAP_SERVICES, save _those_ fields in an OCP secret, and reference them in application.properties like this:
```
spring.datasource.uri=${uri}
spring.datasource.username=${username}
spring.datasource.password=${password}
```
**This is the solution demonstrated in this demo.**

## CF Setup
See the [CF Setup](../common/cf-setup.md) doc.

## Migrating to OpenShift
First, in order to deploy the CF app into OpenShift, you need to create a container image. This can be done easily by using OpenShift's native build capabilities.

### Building the Application Image
See the [Building the Application Image](../common/build-application-image.md) doc.

### Deploying the Application
