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

package org.wildfly.extension.microprofile.config;

import io.undertow.server.handlers.PathHandler;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.as.controller.OperationContext;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class ConfigSourceService implements Service<ConfigSource> {

    private final InjectedValue<PathHandler> pathHandlerInjectedValue = new InjectedValue<>();

    private final String name;
    private final ConfigSource configSource;

    ConfigSourceService(String name, ConfigSource configSource) {
        this.name = name;
        this.configSource = configSource;
    }

    static void install(OperationContext context, String name, ConfigSource configSource) {
        ConfigSourceService service = new ConfigSourceService(name, configSource);
        ServiceBuilder<ConfigSource> serviceBuilder = context.getServiceTarget().addService(ServiceNames.CONFIG_SOURCE.append(name), service);
        serviceBuilder.install();
    }
    @Override
    public void start(StartContext startContext) throws StartException {
    }

    @Override
    public void stop(StopContext stopContext) {
    }

    @Override
    public ConfigSource getValue() throws IllegalStateException, IllegalArgumentException {
        return configSource;
    }
}
