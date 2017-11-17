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

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.as.controller.OperationContext;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.wildfly.microprofile.config.WildFlyConfigProviderResolver;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class ConfigProviderService implements Service<ConfigProviderResolver> {

    private ConfigProviderService() {

    }

    static void install(OperationContext context) {
        ConfigProviderService service = new ConfigProviderService();
        context.getServiceTarget().addService(ServiceNames.CONFIG_PROVIDER, service)
                .install();
    }

    @Override
    public void start(StartContext context) throws StartException {
        ConfigProviderResolver.setInstance(WildFlyConfigProviderResolver.INSTANCE);
    }

    @Override
    public void stop(StopContext context) {
        ConfigProviderResolver.setInstance(null);

    }

    @Override
    public ConfigProviderResolver getValue() throws IllegalStateException, IllegalArgumentException {
        return WildFlyConfigProviderResolver.INSTANCE;
    }
}
