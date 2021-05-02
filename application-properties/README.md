# Application Properties
This shows an example of configuring service bindings declaratively in application.properties. The VCAP_SERVICES environment variable is referenced to explicitly declare the service binding.

## Migrating to OpenShift
* Get VCAP_SERVICES env var from PCF environment
* Store the whole json into a secret (example name: `vcap-services`)
* Mount into the Spring Boot container using:
  ```yaml
  envFrom:
    secretRef:
      name: vcap-services
  ```