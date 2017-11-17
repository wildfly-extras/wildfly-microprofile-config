package org.wildfly.extension.microprofile.config;

import org.jboss.msc.service.ServiceName;

/**
 * Service Names for Eclipse MicroProfile Config objects.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public interface ServiceNames {
    ServiceName MICROPROFILE_CONFIG = ServiceName.JBOSS.append("eclipse", "microprofile", "config");

    ServiceName CONFIG_PROVIDER = MICROPROFILE_CONFIG.append("config-provider");
    ServiceName CONFIG_SOURCE = MICROPROFILE_CONFIG.append("config-source");
    ServiceName CONFIG_SOURCE_PROVIDER = MICROPROFILE_CONFIG.append("config-source-provider");

    ServiceName JSR_CONFIG = MICROPROFILE_CONFIG.append("jsr");
    ServiceName JSR_CONFIG_PROVIDER = JSR_CONFIG.append("config-provider");

}
