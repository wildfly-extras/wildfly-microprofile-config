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

# Supported Config Sources

Applications deployed to WildFly are able to access configuration from 4 different sources:

* System environment (backed by `System.getEnv()`)
* System properties (backed by `System.getProperties`)
* Application properties (backed by `META-INF/microprofile-config.properties` file in deployed application)
* config-source resources (backed by the `/subsystem=microprofile-config/config-source` resources)

# Access to the Config API

The Config API can be used either with CDI:

````
@Inject
Config config;
````

or programmatically:

````
Config config = ConfigProvider.getConfig();
````

# HTTP Access to config-source Resources

Config Source that are stored in WildFly configuration can be exposed using HTTP by setting their `http-enabled` attribute to `true`.

```
# add the remoteConfigSource that can be accessed remotely
/subsystem=microprofile-config/config-source=remoteConfigSource:add(http-enabled=true)
# add the property my.super.property=123456
/subsystem=microprofile-config/config-source=remoteConfigSource:map-put(name=properties, key=my.super.property, value=123456)
# reload for the time being...
reload
```

Properties of the config source can be accessed using HTTP at the URL:

````
http://localhost:8080/wildfly-services/config-source/<config-source name>/<property name>
````

For example, to read the value of the property `my.super.property` put in the `remoteConfigSource` config source,
the URL is `http://localhost:8080/wildfly-services/config-source/remoteConfigSource/my.super.property`.

For now, the HTTP endpoint requires authentication. Let's create an application user for this:

```
./bin/add-user.sh -a -u 'alice' -p 'mypassword'
```

Let's confirm that the properties can be read using HTTP GET (with basic authentication):

````
$ curl -u alice:mypassword http://localhost:8080/wildfly-services/config-source/remoteConfigSource/my.super.property
12346
````

If the property does not exist, the request will return a 404 response:

````
$ curl -i -u alice:mypassword http://localhost:8080/wildfly-services/config-source/remoteConfigSource/property.does.not.exist
...
HTTP/1.1 404 Not Found
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
