# Auto-Reconfiguration
This app does not contain any datasource beans or properties required to connect to a MySQL database. As a result, PCF will auto-reconfigure the Spring Boot application by injecting a datasource bean created by the java_buildpack, on `cf push`.

## Migrating to OpenShift
TBD