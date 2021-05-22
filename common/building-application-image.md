# Building the Application Image
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