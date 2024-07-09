## JavaApp Deployment To OpenShift Demo (TBC)

We have plenty of options to deploy Java Application to OpenShift, in this demo we will see some of these deployment options, this repo is fork from the following Git repo: https://github.com/osa-ora/simple_java_maven

### Using S2I from the Console

Go to OpenShift Developer Console, Select Java from the catalog and click on Create: 


Fill in the Git Repo location "https://github.com/osa-ora/java-demo" and application name:


Select Build options as "Builds" and click on create.


Note: If you are using private NPM artifact repository, then you can just add an environment variable; MAVEN_MIRROR_URL to point to this private artifact repository.


The application will built and deployed into OpenShift and you can just test it by using the route:

### Using Tekton Pipeline from the Console

Follow the same process but during the selection of Build Options select "Pipeline" option as following:

<img width="700" alt="Screenshot 2024-07-08 at 4 17 36 PM" src="https://github.com/osa-ora/nodejs-demo/assets/18471537/94a0406e-b0f3-4dc2-861d-1004e12497f2">

Follow the progress on the pipeline execution and once successfully finished, check the application deployment status.

### Using Binary Build

Execute the following commands from your local machine 

Optionally you can install the dependencies, and in that case it will be a pure binary build, otherwise the builder image will install the dependencies for you.
```
mvn package 
```

Deploy the application as a binary build.
```
oc new-project dev
oc new-app --image-stream=openshift/java:11 --name=my-java-app .
oc start-build my-java-app --from-dir=.
oc expose service/my-java-app
```

You can also add the NPM_MIRROR environment variable to the build in case of locally configured repsository dependencies.

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
shp build create java-build --strategy-name="source-to-image" --source-url="https://github.com/osa-ora/simple_java_maven" --output-image="image-registry.openshift-image-registry.svc:5000/dev/java-app" --builder-image="image-registry.openshift-image-registry.svc:5000/openshift/java:11"

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

<img width="1186" alt="Screenshot 2024-07-08 at 5 33 38 PM" src="https://github.com/osa-ora/nodejs-demo/assets/18471537/37a92158-1fc5-4417-bbce-338609f067eb">


Now, you can deploy the appliation using "oc new-app java-app" or from the console using container image option:

<img width="688" alt="Screenshot 2024-07-08 at 5 34 24 PM" src="https://github.com/osa-ora/nodejs-demo/assets/18471537/ce98efd2-f604-4ef5-b349-6ebcc445e1bf">


Test the application route and we are done!

If you are building this using private repository artifact, just add to the file ".s2i/environment" the following entry:
```
MAVEN_MIRROR_URL={the private repository artifact URL}
```

<img width="463" alt="Screenshot 2024-07-08 at 5 40 17 PM" src="https://github.com/osa-ora/nodejs-demo/assets/18471537/3ed26c32-12e6-489f-a9b9-30804eec9142">

---

