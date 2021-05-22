# cf-to-ocp-spring-boot
This repo gives some basic examples of migrating a CF app to OCP with no code change.

## Getting Started
In order to run through the examples, you need access to a CF and an OCP environment.

### CF Access
If you don't already have access to a CF environment, you can get started by provisioning a space on [IBM Cloud](https://cloud.ibm.com/cloudfoundry/overview). Do keep in mind that, [while inexpensive](https://www.ibm.com/cloud/cloud-foundry), this option is **not free**. IBM Cloud does have a free tier, but your Spring Boot app will likely run OOM within the free tier constraints (256MB memory).

Here's how to get set up in IBM Cloud:
1. Create an IBM Cloud account by following [this link](https://cloud.ibm.com/registration)
1. Create a sample application from the [IBM Cloud UI](https://cloud.ibm.com/catalog/starters/cloud-foundry?runtime=liberty-for-java). This will automatically create your CF org and space for you to use, while also providing you a sample workload to examine if you want.
1. Install the [ibmcloud CLI](https://cloud.ibm.com/docs/cli?topic=cli-install-ibmcloud-cli#shell_install).
1. Log into IBM Cloud from the CLI using one of the following commands:
   1. `ibmcloud login` if logging in using an IBM Cloud username and password
   1. `ibmcloud login --sso` if logging in using a federated user ID
1. Target your CF org and space by running `ibmcloud target --cf`.
1. [Optional] Create an alias from `ibmcloud cf` to `cf`. This is a convenient step so that you don't have to remember to prepend "ibmcloud" to "cf" each time you want to interact with CF
   1. In `~/.bashrc` (or similar config file based on your shell), add the following line:
      ```bash
      alias cf="ibmcloud cf"
      ```
1. Unless you want to keep your sample application running, remove the sample app by running the following command:
   ```bash
   cf apps
   cf delete <application-name>
   ```
   At the prompt, enter `yes`.

### OCP Access
I'll assume more familiarity with OCP in this demo, but here are some options to provision an OCP cluster or namespace:
* [crc](https://developers.redhat.com/products/codeready-containers/overview) (CodeReady Containers): Provision a single-node OCP cluster on your laptop
* [Developer Sandbox](https://developers.redhat.com/developer-sandbox): Provision a namespace on a multi-tenant OCP cluster

## Demos
Once your have configured CF and OCP access, you can continue by exploring each of the demos in this repo. Here is a list of each demo:
* [application-properties](./application-properties): A demo showcasing how you can migrate a CF app with service bindings, where datasources are configured using an application.properties file.
* [auto-reconfiguration](./auto-reconfiguration): Another demo showcasing the migration of a CF app with service bindings, except this time, the app does not explicitly define any datasources in application properties or code. This assumes CF's [auto-reconfiguration](https://github.com/cloudfoundry/java-buildpack-auto-reconfiguration) feature is leveraged and explains how to mock this behavior in a migration to OCP.

These demos are not included but should be added at a later date to provide a more complete migration guide:
* Migrating a CF-internal database or other service to OCP
* Migrating Spring Boot apps using spring-cloud-connector
* Migrating Spring Boot apps using spring-cloud-config-server
* Migrating Spring Boot apps using an explicitly coded datasource