## JavaApp Deployment To OpenShift Demo 

We have plenty of options to deploy Java Application to OpenShift, in this demo we will see some of these deployment options, this repo is fork from the following Git repo: https://github.com/osa-ora/simple_java_maven

### Using S2I from the Console

Go to OpenShift Developer Console, Select Java from the catalog and click on Create: 

<img width="335" alt="Screenshot 2024-07-09 at 12 17 58 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/0298e324-e05e-48e2-9077-c8201ae4f486">

Select Java version, fill in the Git Repo location "https://github.com/osa-ora/java-demo" and application name:

<img width="686" alt="Screenshot 2024-07-09 at 12 18 57 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/c0cd2c0b-2c1a-426e-b668-5d6c061c2f4c">


Select Build options as "Builds" and click on create.

<img width="678" alt="Screenshot 2024-07-09 at 12 19 44 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/7cade55d-b0ad-4e68-9540-d793ed8331d0">

Note: If you are using private Maven repository, then you can just add an environment variable; MAVEN_MIRROR_URL to point to this private artifact repository.
It can be added as an entry to the .s2i/environment file or as environment variable to the build object.

The application will built and deployed into OpenShift:

<img width="729" alt="Screenshot 2024-07-09 at 12 22 32 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/af7dddf8-ed90-4b18-9b51-949083dac369">

And you can just test it by using the route:

<img width="391" alt="Screenshot 2024-07-09 at 12 23 05 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/dbc66dbb-9403-4128-b07b-6d04c0fb708f">

You can use that balance URL to get some dummy data from that service.

### Using Tekton Pipeline from the Console

Follow the same process but during the selection of Build Options select "Pipeline" option as following:

<img width="695" alt="Screenshot 2024-07-09 at 12 24 45 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/35b5efe4-af4d-421f-b1ec-2468ad997eb8">

Follow the progress on the pipeline execution and once successfully finished, check the application deployment status.

<img width="764" alt="Screenshot 2024-07-09 at 12 30 07 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/ef121a35-46c9-4f26-ac39-79ff05d454b2">

### Using Binary Build

Execute the following commands from your local machine 

Optionally you can install the dependencies, and in that case it will be a pure binary build, otherwise the builder image will install the dependencies for you.
```
mvn clean package 
```

Deploy the application as a binary build.
```
oc new-project dev
oc new-app --image-stream=openshift/java:11 --name=my-java-app .
mkdir deploy
cp ./target/demo-0.0.1-SNAPSHOT.jar ./deploy/.
cd deploy
oc start-build my-java-app --from-dir=.
oc expose service/my-java-app
```

### Using Builds for OpenShift:

First you need to install the Builds for OpenShift Operator:

<img width="276" alt="Screenshot 2024-07-08 at 11 54 49 AM" src="https://github.com/osa-ora/angular-demo/assets/18471537/f2f5d78d-e95a-43f6-a9bf-a5fb4a70f6df">

Once, installed create an instance of "Shipwright Build", keep the default

<img width="696" alt="Screenshot 2024-07-08 at 12 32 03 PM" src="https://github.com/osa-ora/angular-demo/assets/18471537/ea1641f3-df9d-4dfd-86bd-9c27ade1e177">

Now, you can deploy the application by creating a Shipwright build either from the console or from the command line:

```
//create an openshift project
oc new-project dev

//create shipwright build for our application in the 'dev' project
//our project code is in the root of the Git repo, otherwise we could have used '--source-context-dir="docker-build"' flag to specify the context folder of our application.
shp build create java-build --strategy-name="source-to-image" --source-url="https://github.com/osa-ora/java-demo" --output-image="image-registry.openshift-image-registry.svc:5000/dev/java-app" --builder-image="image-registry.openshift-image-registry.svc:5000/openshift/java:11"

//start the build and follow the output
shp build run java-build --follow

//create an application from the container image
oc new-app java-app

//expose our application
oc expose service/java-app

//test our application is deployed ..
curl $(oc get route java-app -o jsonpath='{.spec.host}')/

```

You can do the same from the Dev Console

Go to "Builds" section and click on Create and select "Shipwright Build"

<img width="1189" alt="Screenshot 2024-07-08 at 12 38 53 PM" src="https://github.com/osa-ora/angular-demo/assets/18471537/9eb76e9b-1dc0-41a2-992e-ea49985ba513">

Post the following content:

```
apiVersion: shipwright.io/v1beta1
kind: Build
metadata:
  name: java-build
  namespace: dev
spec:
  output:
    image: 'image-registry.openshift-image-registry.svc:5000/dev/java-app'
  paramValues:
    - name: builder-image
      value: 'image-registry.openshift-image-registry.svc:5000/openshift/java:11'
  source:
    git:
      url: 'https://github.com/osa-ora/simple_java_maven'
    type: Git
  strategy:
    kind: ClusterBuildStrategy
    name: source-to-image
```

Click on Create and then click on "Start Build", follow the logs:

<img width="813" alt="Screenshot 2024-07-09 at 12 44 10 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/1bfe4d5f-5c4d-4ae0-bf6e-8f4842f88236">

Now, you can deploy the appliation using "oc new-app java-app" or from the console using container image option:

<img width="681" alt="Screenshot 2024-07-09 at 12 45 00 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/b111f1e7-6d4b-411a-8387-17a232a06a25">


Test the application route and we are done!

If you are building this using private repository artifact, just add to the file ".s2i/environment" the following entry:
```
MAVEN_MIRROR_URL={the private repository artifact URL}
```

<img width="626" alt="Screenshot 2024-07-09 at 12 46 14 PM" src="https://github.com/osa-ora/java-demo/assets/18471537/e6f8f223-d813-41b8-aed6-75aa2e0194b1">

---

