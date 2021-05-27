# CF Setup
The following steps will help you prepare a cloud foundry application for whichever sample you are working on application-properties, autoreconfiguration, etc.

These instructions assume you have access to CF and OCP environments and are logged into each. 

## Provisioning a MySQL database
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

## Deploying the CF Application
CF applications are configured in code using an [App Manifest](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html). The app manifest for this application is [manifest.yml](./manifest.yml). One of the important fields in this file is `services`, which lists the service that we created, `testdb`.

In whichever tutorial folder you are working on (application-properties, autoreconfiguration, etc), Run the following commands to deploy your application:
1. Build the code locally to produce a Jar
   ```
   mvn clean install
   ```
2. Push your CF application
   ```
   cf push
   ```
3. Confirm your application was deployed successfully
   ```
   cf apps
   ```