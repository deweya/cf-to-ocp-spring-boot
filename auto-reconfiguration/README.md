# Auto-Reconfiguration
This app does not contain any datasource beans or properties required to connect to a MySQL database. As a result, PCF will auto-reconfigure the Spring Boot application by injecting a datasource bean created by the java_buildpack, on `cf push`.

## Migrating to OpenShift
* Create an application.properties file:
  ```
  spring.datasource.url=jdbc:${vcap.services.testdb.credentials.uri}
  ```
* Get VCAP_SERVICES env var from PCF environment
* Store the whole json into a secret (example name: `vcap-services`)
* Mount into the Spring Boot container using:
  ```yaml
  envFrom:
    secretRef:
      name: vcap-services
  ```