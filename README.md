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
