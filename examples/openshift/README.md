# OpenShift Example

This example builds a Java application using Swarm that generates a fixed number of random integers.
This example shows that a simple WildFly Swarm application can be deployed and configured in many different ways without having to change the actual application code:

* It can be run as a __standalone Java application using System Properties__ to be configured
* It can be run in a __Docker container using environment variables__ to be configured
* It can be run in __OpenShift application using Config Map__ to be configured

In this readme, we will describe the application and explain all the steps to:
 * build and run it
 * create a Docker image and run it in a container
 * push the image on the OpenShift registry
 * create an OpenShift application to deploy it
 * add a Config Map to configure the application

## Prerequisites

We will use [Minishift](https://docs.openshift.org/latest/minishift)) to deploy a local OpenShift container.

* Install [Minishift](https://docs.openshift.org/latest/minishift/getting-started/index.html)
* Start minishift

```
$ minishift start
```

* Configure your shell to use `docker` and `oc` commands provided by Minishift

```
$ eval $(minishift docker-env)
$ eval $(minishift oc-env)
```

* To check that Minishift is properly installed and configured, let's create a new project named 'numbers-project' that we will use
later to deploy the Java application

```
$ oc login
// By default, the user credentials are `developer` : `developer`
$ oc new-project numbers
```

* Go to the [numbers overview in OpenShift Web Console](https://192.168.64.2:8443/console/project/numbers/overview)

## Description of the application

The Java application use WildFly Swarm to provide a single Web Service that returns a list of random positive integers (in a `text/plain` response, one integer per line).

The application accepts 2 configuration properties:

* `num.size` - the number of integers to generate for each call to the Web Service
* `num.max` - the maximum value for the generated integers.

These 2 properties are configured using the Eclipse MicroProfile Config API in the [NumbersGenerator endpoint](src/main/java/org/wildfly/swarm/microprofile/config/example/openshift/NumbersGenerator.java#L20):

```
@Inject
@ConfigProperty(name = "num.size", defaultValue = "3")
int numSize;

@Inject
@ConfigProperty(name = "num.max", defaultValue = "" + Integer.MAX_VALUE)
int numMax;
```
According to their default values, each call to the application will return 3 integers that range between 0 and the maximum Integer value
supported by Java.

Since the properties are using the Eclipse MicroProfile Config API, we can provide other values specific to our deployment stategy.
We change them using System properties or environment variables for development and testing.

We also want to be able to configure them using [OpenShift/Kubernetes Config Map](https://docs.openshift.org/latest/dev_guide/configmaps.html).

To do so, we add a new type of `ConfigSource` to the application by customizing the `MicroprofileFraction` used by Swarm in the [Main class](src/main/java/org/wildfly/swarm/microprofile/config/example/openshift/Main.java#L36):

```
swarm.fraction(new MicroProfileConfigFraction()
        .configSource("numbers-dir-config-source", (cs) -> {
            cs.ordinal(600)
                    .dir("/etc/config/numbers-app/");
        }));
```

When the application is started, it scans the `/etc/config/numbers-app/` directory (if it exists) and reads each file to generate
 a property (with the file name as the key and the file content as the value).

In our case, we can create a file named `num.size` with `5` as its content to configure the application to return `5` integers instead of `3`.

## Build the application

```
$ mvn clean package
```

## Run the standalone Java application

Let's run the application as a standalone UberJar and use a System property to configure it to return integers between 0 and `10`:

```
$ java -Dnum.max=10 -jar target/microprofile-config-openshift-example-1.0-SNAPSHOT-swarm.jar
```

* Got to http://localhost:8080/

```
7
2
8
```

## Create a Docker image

Let's now create a Docker image from the application using the simple Dockerfile:

```
FROM jboss/base-jdk:8

ADD target/microprofile-config-openshift-example-1.0-SNAPSHOT-swarm.jar /opt/wildfly-swarm.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/wildfly-swarm.jar"]
```

The Docker image is build by running the command:

```
$ docker build -t numbers/numbers-app .
```

## Run the Docker image

Once we have minishift up and running, we can run the application in Docker:

```
$ docker run -e "num.size=2" -p 8080:8080 numbers/numbers-app
```

Note that we pass the enviroment variable `num.size=2` to Docker to configure the application to return `2` integers (instead of `3`).

We can check that the application is available from Docker (on my computer, the `DOCKER_HOST` value corresponds to the `192.168.64.2`
address that I'm using for the rest of the documentation):

* Go to http://192.168.64.2:8080/

```
1813256706
1838185934
```

## Push the Docker image to the OpenShift Image registry

Next step is to push this Docker image to the OpenShift Image registry to be able to use it with OpenShift.

```
$ docker login -u developer -p $(oc whoami -t) $(minishift openshift registry)
$ docker tag numbers/numbers-app $(minishift openshift registry)/numbers/numbers-app
$ docker push $(minishift openshift registry)/numbers/numbers-app
```

The namespace is very important as it must match the name of the OpenShift project (`numbers` in this case).

## Deploy the application on OpenShift

Now that the Docker image is in the OpenShift Image registry, we can add it to our OpenShift project.

* Go to the OpenShift Web Console [`numbers` project overview](https://192.168.64.2:8443/console/project/numbers/overview)
* Clik on `Add to Project` and select [`Deploy Image`](https://192.168.64.2:8443/console/project/numbers/create?tab=deployImage)
* Select the Image Stream Tag from the Docker image we just pushed to the registry
  * Namespace: `numbers`
  * Image Stream: `numbers-app`
  * tag: `latest`
* Click on `Create` to deploy the application
* Go back to the [`numbers` project overview](https://192.168.64.2:8443/console/project/numbers/overview) to see the deployment configuration of the `number-app`.

### Create a route to the application

In order to access the application from outside OpenShift, we must create a route for it.

* Go to [`numbers` project overview](https://192.168.64.2:8443/console/project/numbers/overview)
* In the `NUMBERS APP`, click on `Create Route`
* Keep the default values and simply click on `Create`
* The route has been created and the application can now be accessed at http://numbers-app-numbers.192.168.64.2.nip.io/ (check the actual
route URL from the [`numbers` project overview](https://192.168.64.2:8443/console/project/numbers/overview) in case the Docker IP is different):

```
1977427929
1951739266
1633626040
```

We have not provided any different values for the application configuration, so it is returning `3` integers between 0 and `Integer.MAX_VALUE`.

### Create a Config Map

We will now create a Config Map to configure our application.

* Go to the [Configuration tab for `numbers-app` deployment](https://192.168.64.2:8443/console/project/numbers/browse/dc/numbers-app?tab=configuration)
* Under Volumes, click on [`Add Config Files`](https://192.168.64.2:8443/console/project/numbers/add-config-volume?kind=DeploymentConfig&name=numbers-app)
* Under the `Source` section, click on `Create Config Map
  * Name: `numbers-config`
  * Key: `num.size`
  * Value: `5`
  * click on `Create` to create the Config Map
* For the `Source` we can now select the `numbers-config` Config Map that has just been created.
* For the `Mount Path`, enter `/etc/config/numbers-app` (that is the directory configured in the [Main class](src/main/java/org/wildfly/swarm/microprofile/config/example/openshift/Main.java#L36))
* Click on `Add`
* By default, the application will be redeployed (as its deployment configuration has changed). If it does not happen automatically, click on the `Deploy` button in the
 top right-hand corner of the [`numbers-app` deployment config page](https://192.168.64.2:8443/console/project/numbers/browse/dc/numbers-app).
* Go to http://numbers-app-numbers.192.168.64.2.nip.io/

```
609896252
268683851
27136750
1622490076
160078213
```

Note that the application now returns `5` integers instead of `3`.

## Update the Config Map

We can now update the config map to change the maximum value of the generated integers.

* Go to the [Configuration tab for `numbers-app` deployment config](https://192.168.64.2:8443/console/project/numbers/browse/dc/numbers-app?tab=configuration)
* Under Volumes, click on [`numbers-config`](https://192.168.64.2:8443/console/project/numbers/browse/config-maps/numbers-config)

We can follow the same steps than before to add the `num.max` configuration or edit directly the YAML file corresponding to the Config Map:

* On the right end corner, in the `Actions` button, choose [`Edit YAML`](https://192.168.64.2:8443/console/project/numbers/edit/yaml?kind=ConfigMap&name=numbers-config&group=&returnURL=)
* Add a new property under `data`: `num.max: '100'`

```
    apiVersion: v1
    kind: ConfigMap
    metadata:
      name: numbers-config
      namespace: numbers
      ...
    data:
      num.size: '5'
      num.max: '100'
```

* Click on `Save`
* Go to the [`numbers-app` deployment](https://192.168.64.2:8443/console/project/numbers/browse/dc/numbers-app?tab=history) and click on `Deploy`
* Go to http://numbers-app-numbers.192.168.64.2.nip.io/

```
24
41
78
86
74
```

The application is now configured to return `5` integers between `0` and `100`.