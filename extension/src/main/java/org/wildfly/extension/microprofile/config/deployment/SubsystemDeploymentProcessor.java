/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.extension.microprofile.config.deployment;

import java.util.List;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
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
import org.jboss.logging.Logger;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.wildfly.extension.microprofile.config.ServiceNames;
import org.wildfly.microprofile.config.WildFlyConfigBuilder;
import org.wildfly.microprofile.config.WildFlyConfigProviderResolver;
import org.wildfly.microprofile.config.inject.ConfigExtension;

/**
 */
public class SubsystemDeploymentProcessor implements DeploymentUnitProcessor {

    Logger log = Logger.getLogger(SubsystemDeploymentProcessor.class);

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
        Module module = deploymentUnit.getAttachment(Attachments.MODULE);

        WildFlyConfigBuilder builder = new WildFlyConfigBuilder();
        builder.forClassLoader(module.getClassLoader())
                .addDefaultSources()
                .addDiscoveredSources()
                .addDiscoveredConverters();
        addConfigSourcesFromServices(builder, phaseContext.getServiceRegistry(), module.getClassLoader());
        Config config = builder.build();

        WildFlyConfigProviderResolver.INSTANCE.registerConfig(config, module.getClassLoader());

        if (WeldDeploymentMarker.isPartOfWeldDeployment(deploymentUnit)) {
            WeldPortableExtensions extensions = WeldPortableExtensions.getPortableExtensions(deploymentUnit);
            extensions.registerExtensionInstance(new ConfigExtension(), deploymentUnit);
        }

    }

    private void addConfigSourcesFromServices(ConfigBuilder builder, ServiceRegistry serviceRegistry, ClassLoader classloader) {
        List<ServiceName> serviceNames = serviceRegistry.getServiceNames();
        for (ServiceName serviceName: serviceNames) {
            if (ServiceNames.CONFIG_SOURCE.isParentOf(serviceName)) {
                ServiceController<?> service = serviceRegistry.getService(serviceName);
                ConfigSource configSource = ConfigSource.class.cast(service.getValue());
                builder.withSources(configSource);
            } else if (ServiceNames.CONFIG_SOURCE_PROVIDER.isParentOf(serviceName)) {
                ServiceController<?> service = serviceRegistry.getService(serviceName);
                ConfigSourceProvider configSourceProvider = ConfigSourceProvider.class.cast(service.getValue());
                for (ConfigSource configSource : configSourceProvider.getConfigSources(classloader)) {
                    builder.withSources(configSource);
                }
            }
        }
    }

    @Override
    public void undeploy(DeploymentUnit context) {
        Module module = context.getAttachment(Attachments.MODULE);
        WildFlyConfigProviderResolver.INSTANCE.releaseConfig(ConfigProvider.getConfig(module.getClassLoader()));
    }
}
