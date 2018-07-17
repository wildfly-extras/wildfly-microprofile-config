# wildfly-microprofile-config

[WildFly][wildfly]/[Thorntail][thorntail] Extension for [Eclipse MicroProfile Config][microprofile-config].

[![Build Status](https://ci.wildfly.org/app/rest/builds/buildType:(id:MicroProfileJsr_WildflyMicroprofileConfig_MpConfig1xBranch)/statusIcon)](https://ci.wildfly.org/viewType.html?buildTypeId=MicroProfileJsr_WildflyMicroprofileConfig_MpConfig1xBranch&guest=1)

# Instructions

* Compile and install the [Eclipse MicroProfile Config][microprofile-config] project.
* Compile and install this project:

```
mvn clean install
```

# Project structure

* [extension](extension/) - WildFly Extension that provides the `microprofile-config` subsystem. It also allows to define ConfigSources that are stored in the subsystem configuration.
* [feature-pack](feature-pack/) - Feature pack that bundles the extension with the JBoss Modules required to run it in WildFly and Thorntail.
* [dist](dist/) - A distribution of WildFly with the microprofile-config extension installed (in its standalone-microprofile.xml configuration)
* [config-api](config-api/) - Generation of Thorntail Config API that provides a Java API to manage the `microprofile-config` subsystem.

# Example

Once this project has been installed, go to the `examples/simple` directory to run the simple example.


The Web endpoint is using the Eclipse MicroProfile Config to read the value of the some properties:

```
@Inject
Config config;

@Inject
@ConfigProperty(name = "BAR", defaultValue = "my BAR property comes from the code")
String bar;

@Inject
@ConfigProperty(name = "BOOL_PROP", defaultValue = "no")
boolean boolProp;

...
Optional<String> foo = config.getOptionalValue("FOO", String.class);
...

```

The Eclipse MicroProfile Config can be used by the application by adding the corresponding Thorntail fractions:

```
<dependency>
  <groupId>io.thorntail</groupId>
  <artifactId>microprofile-config</artifactId>
  <version>${project.version}</version>
</dependency>
```

First, run the `simple` example:

```
$ cd examples/simple/
$ mvn thorntail:run
...
2017-04-14 10:35:24,416 WARN  [org.wildfly.swarm] (main) WFSWARM0013: Installed fraction: Eclipse MicroProfile Config - UNSTABLE        io.thorntail:microprofile-config:1.0-SNAPSHOT
...
2017-04-14 10:35:30,676 INFO  [org.wildfly.swarm] (main) WFSWARM99999: Thorntail is Ready
```


If you go to [http://localhost:8080/hello](http://localhost:8080/hello), you will see the message:

```
$ curl http://localhost:8080/hello
FOO property = Optional[My FOO property comes from the microprofile-config.properties file]
BAR property = my BAR property comes from the code
BOOL_PROP property = false
```

The application has configured its `FOO` property in its [microprofile-config.properties](example/src/main/resources/META-INF/microprofile-config.properties) file.
The `BAR` and `BOOL_PROP` properties are configured with `defaultValue` using the `@ConfigProperty` annotation.

Let's now restart the application with the `FOO`, `BAR`, and `BOOL_PROP` environment variables set:

```
$ BOOL_PROP="yes" FOO="my FOO property comes from the env" BAR="my BAR property comes from the env" mvn wildfly-swarm:run
...
2017-04-14 10:35:30,676 INFO  [org.wildfly.swarm] (main) WFSWARM99999: WildFly Swarm is Ready
```

If you now go again to [http://localhost:8080/hello](http://localhost:8080/hello), you will see the message:

```
$ curl http://localhost:8080/hello
FOO property = Optional[my FOO property comes from the env]
BAR property = my BAR property comes from the env
BOOL_PROP property = true
```

# Links

* [WildFly][wildfly]
* [Thorntail][thorntail]
* [Eclipse MicroProfile Config][microprofile-config]


[wildfly]: https://wildlfy.org/
[thorntail]: http://wildfly-swarm.io/
[microprofile-config]: https://github.com/eclipse/microprofile-config/
