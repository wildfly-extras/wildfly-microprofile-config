# microprofile-config-extension

[WildFly][wildfly]/[Swarm][swarm] Extension for [Eclipse MicroProfile Config][microprofile-config].

# Instructions

* Compile and install the [Eclipse MicroProfile Config][microprofile-config] project.
* Compile and install this project:

```
mvn clean install
```


# Project structure

* [implementation](implementation/) - (__incomplete__) Implementation of the Eclipse MicroProfile Config API.
* [extension](extension/) - WildFly Extension that provides the `microprofile-config` subsystem. This subsystem will ensure that a `Config` instance
can be injected using CDI in the application. It also allows to define ConfigSources that are stored in the subsystem configuration.
* [feature-pack](feature-pack/) - Feature pack that bundles the extension with the JBoss Modules required to run it in WildFly and Swarm.
* [dist](dist/) - A distribution of WildFly with the microprofile-config extension installed (in its standalone-microprofile.xml configuration)
* [config-api](config-api/) - Generation of Swarm Config API that provides a Java API to manage the `microprofile-config` subsystem.
* [fraction](fraction/) - Swarm Fraction to use the MicroProfile Config API in your application.
* [example](example/) - a Swarm application that uses an `Config` instance injected with CDI.

# Example

Once this project has been installed, go to the `example` directory to run the example.


The Web endpoint is using the Eclipse MicroProfile Config to read the value of the `FOO_BAR` property:

```
@Inject
Config config;
...
String text = "FOO_BAR property is " + config.getOptionalValue("FOO_BAR", String.class);

```

The Eclipse MicroProfile Config can be used by the application by adding the corresponding Swarm fractions:

```
<dependency>
  <groupId>org.wildfly.swarm</groupId>
  <artifactId>cdi</artifactId>
</dependency>
<dependency>
  <groupId>net.jmesnil</groupId>
  <artifactId>microprofile-config-fraction</artifactId>
  <version>${project.version}</version>
</dependency>
```

First, run the example:

```
$ cd example
$ mvn wildfly-swarm:run
...
2017-04-14 10:35:24,416 WARN  [org.wildfly.swarm] (main) WFSWARM0013: Installed fraction: Eclipse MicroProfile Config - UNSTABLE        net.jmesnil:microprofile-config-fraction:1.0-SNAPSHOT
...
2017-04-14 10:35:30,676 INFO  [org.wildfly.swarm] (main) WFSWARM99999: WildFly Swarm is Ready
```


If you go to [http://localhost:8080/hello](http://localhost:8080/hello), you will see the message: `FOO_BAR property is Optional[My property comes from the microprofile-config.properties file]`

```
$ curl http://localhost:8080/hello
FOO_BAR property is Optional[My property comes from the microprofile-config.properties file]
```

The application has configured this property in its [microprofile-config.properties](example/src/main/resources/META-INF/microprofile-config.properties) file.

Let's now restart the application with the `FOO_BAR` environment variable set:

```
$ FOO_BAR="my property comes from the env" mvn wildfly-swarm:run
...
2017-04-14 10:35:30,676 INFO  [org.wildfly.swarm] (main) WFSWARM99999: WildFly Swarm is Ready
```

If you now go again to [http://localhost:8080/hello](http://localhost:8080/hello), you will see the message: `FOO_BAR property is Optional[my property comes from the env]`

```
$ curl http://localhost:8080/hello
$ FOO_BAR property is Optional[my property comes from the env]
```

# Links

* [WildFly][wildfly]
* [WildFly Swarm][swarm]
* [Eclipse MicroProfile Config][microprofile-config]


[wildfly]: https://wildlfy.org/
[swarm]: http://wildfly-swarm.io/
[microprofile-config]: https://github.com/eclipse/microprofile-config/
