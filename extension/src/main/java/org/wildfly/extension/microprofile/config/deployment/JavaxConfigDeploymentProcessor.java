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

import static org.wildfly.extension.microprofile.config.deployment.MicroProfileConfigDeploymentProcessor.getMicroProfileConfigSourceFromServices;

import javax.config.Config;
import javax.config.ConfigProvider;
import javax.config.spi.ConfigSource;

import org.jboss.as.ee.weld.WeldDeploymentMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.Phase;
import org.jboss.as.weld.deployment.WeldPortableExtensions;
import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceRegistry;
import org.wildfly.microprofile.config.jsr.WildFlyConfigBuilder;
import org.wildfly.microprofile.config.jsr.WildFlyConfigProviderResolver;
import org.wildfly.microprofile.config.jsr.inject.ConfigExtension;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class JavaxConfigDeploymentProcessor implements DeploymentUnitProcessor {
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

        if (ConfigMarkers.isJavaxConfigDeployment(deploymentUnit)) {
            Module module = deploymentUnit.getAttachment(Attachments.MODULE);
            ServiceRegistry serviceRegistry = deploymentUnit.getServiceRegistry();

            WildFlyConfigBuilder builder = new WildFlyConfigBuilder();
            builder.forClassLoader(module.getClassLoader())
                    .addDefaultSources()
                    .addDiscoveredSources()
                    .addDiscoveredConverters();
            // convert MicroProfile ConfigSources from the subsystem to javax.config ConfigSource
            for (org.eclipse.microprofile.config.spi.ConfigSource microProfileConfigSource : getMicroProfileConfigSourceFromServices(serviceRegistry, module.getClassLoader())) {
                ConfigSource javaxConfigSource = MicroProfileToJavaxUtils.toJavax(microProfileConfigSource);
                builder.withSources(javaxConfigSource);
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
        if (ConfigMarkers.isJavaxConfigDeployment(context)) {
            Module module = context.getAttachment(Attachments.MODULE);
            WildFlyConfigProviderResolver.INSTANCE.releaseConfig(ConfigProvider.getConfig(module.getClassLoader()));
        }

    }
}
