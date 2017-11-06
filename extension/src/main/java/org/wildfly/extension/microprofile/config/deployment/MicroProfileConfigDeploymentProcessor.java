/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.extension.microprofile.config.deployment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.ee.weld.WeldDeploymentMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.weld.deployment.WeldPortableExtensions;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.wildfly.extension.microprofile.config.ServiceNames;
import org.wildfly.microprofile.config.WildFlyConfigBuilder;
import org.wildfly.microprofile.config.WildFlyConfigProviderResolver;
import org.wildfly.microprofile.config.inject.ConfigExtension;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class MicroProfileConfigDeploymentProcessor implements DeploymentUnitProcessor {
    /**
     * See {@link Phase} for a description of the different phases
     */
    public static final Phase PHASE = Phase.POST_MODULE;

    /**
     * The relative order of this processor within the {@link #PHASE}.
     * The current number is large enough for it to happen after all
     * the standard deployment unit processors that come with JBoss AS.
     */
    public static final int PRIORITY = 0x4000;

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        if (ConfigMarkers.isMicroProfileConfigDeployment(deploymentUnit)) {
            Module module = deploymentUnit.getAttachment(Attachments.MODULE);
            ServiceRegistry serviceRegistry = deploymentUnit.getServiceRegistry();

            WildFlyConfigBuilder builder = new WildFlyConfigBuilder();
            builder.forClassLoader(module.getClassLoader())
                    .addDefaultSources()
                    .addDiscoveredSources()
                    .addDiscoveredConverters();
            for (ConfigSource configSource : getMicroProfileConfigSourceFromServices(serviceRegistry, module.getClassLoader())) {
                builder.withSources(configSource);
            }
            Config config = builder.build();

            WildFlyConfigProviderResolver.INSTANCE.registerConfig(config, module.getClassLoader());

            if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
                WeldPortableExtensions extensions = WeldPortableExtensions.getPortableExtensions(deploymentUnit);
                extensions.registerExtensionInstance(new ConfigExtension(), deploymentUnit);
            }

        }
    }

    @Override
    public void undeploy(DeploymentUnit context) {
        if (ConfigMarkers.isMicroProfileConfigDeployment(context)) {
            Module module = context.getAttachment(Attachments.MODULE);
            WildFlyConfigProviderResolver.INSTANCE.releaseConfig(ConfigProvider.getConfig(module.getClassLoader()));
        }

    }

    static Set<ConfigSource> getMicroProfileConfigSourceFromServices(ServiceRegistry serviceRegistry, ClassLoader classloader) {
        Set<ConfigSource> configSources = new HashSet<>();
        List<ServiceName> serviceNames = serviceRegistry.getServiceNames();
        for (ServiceName serviceName: serviceNames) {
            if (ServiceNames.CONFIG_SOURCE.isParentOf(serviceName)) {
                ServiceController<?> service = serviceRegistry.getService(serviceName);
                ConfigSource configSource = ConfigSource.class.cast(service.getValue());
                configSources.add(configSource);
            } else if (ServiceNames.CONFIG_SOURCE_PROVIDER.isParentOf(serviceName)) {
                ServiceController<?> service = serviceRegistry.getService(serviceName);
                ConfigSourceProvider configSourceProvider = ConfigSourceProvider.class.cast(service.getValue());
                for (ConfigSource configSource : configSourceProvider.getConfigSources(classloader)) {
                    configSources.add(configSource);
                }
            }
        }
        return configSources;
    }
}
