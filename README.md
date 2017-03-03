# microprofile-config-extension

[WildFly][wildfly] Extension for [Eclipse MicroProfile Config][microprofile-config].

# Instructions

* Compile and install the [Eclipse MicroProfile Config][microprofile-config] project.
* Compile and package this project:

```
mvn clean package
```

* copy the JBoss Modules for both this extension and the Eclipse MicroProfile Config API:

```
# the extension module built by Maven
cp -r target/module/ $WILDFLY_HOME/modules/
# the  MicroProfile Config API
cp -r src/main/resources/modules/ $WILDFLY_HOME/modules/
```

* Start WildFly (using its default standalone configuration) and use its CLI to add the extension and subsystem:

```
/extension=net.jmesnil.microprofile-config-extension:add()
/subsystem=microprofile-config:add()
```

Any application that is now deployed to WildFly can use the Eclipse MicroProfile Config API by simply calling `ConfigProvider.getConfig()`.

# Management API

## Add a config source

```
/subsystem=microprofile-config/config-source=myConfigSource:add(ordinal=200)
```

## Add a property to a config source

```
/subsystem=microprofile-config/config-source=myConfigSource:map-put(name=properties, key=foo, value=12345)
```

The properties of the config source is stored in WildFly configuration:

```
<subsystem xmlns="urn:net.jmesnil:microprofile-config:1.0">
    <config-source name="myConfigSource" ordinal="200">
        <property name="foo" value="12345"/>
    </config-source>
</subsystem>
```

# Features

Applications deployed to WildFly are able to access configuration from 4 different sources:

* System environment (backed by `System.getEnv()`)
* System properties (backed by `System.getProperties`)
* Application properties (backed by `META-INF/microprofile-config.properties` file)
* config-source resources (backed by the `/subsystem=microprofile-config/config-source` resources)

The Config can be injected using CDI:

````
@Inject
Config config;
````

or created programmatically:

````
Config config = ConfigProvider.getConfig();
````


# Example

A Web App that can be deployed to WildFly and uses the Config API can be found
in the [microprofile-config-example repository](https://github.com/jmesnil/microprofile-config-example/).


# Links

* [WildFly][wildfly]
* [WildFly Swarm][swarm]
* [Eclipse MicroProfile Config][microprofile-config]


[wildfly]: https://github.com/microprofile/microprofile-config
[swarm]: http://wildfly-swarm.io
[microprofile-config]: https://github.com/microprofile/microprofile-config
