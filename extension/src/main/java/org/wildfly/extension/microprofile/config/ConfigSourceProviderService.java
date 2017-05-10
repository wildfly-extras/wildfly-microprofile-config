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

package org.wildfly.extension.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.controller.OperationContext;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class ConfigSourceProviderService implements Service<ConfigSourceProvider> {

    public static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append("eclipse", "microprofile", "config", "config-source-provider");

    private final String name;
    private final ConfigSourceProvider configSourceProvider;

    public ConfigSourceProviderService(String name, ConfigSourceProvider configSourceProvider) {
        this.name = name;
        this.configSourceProvider = configSourceProvider;
    }

    public static void install(OperationContext context, String name, ConfigSourceProvider configSourceProvider) {
        ConfigSourceProviderService service = new ConfigSourceProviderService(name, configSourceProvider);
        ServiceBuilder<ConfigSourceProvider> serviceBuilder = context.getServiceTarget().addService(SERVICE_NAME.append(name), service);
        serviceBuilder.install();
    }
    @Override
    public void start(StartContext startContext) throws StartException {
    }

    @Override
    public void stop(StopContext stopContext) {
    }

    @Override
    public ConfigSourceProvider getValue() throws IllegalStateException, IllegalArgumentException {
        return configSourceProvider;
    }

}
