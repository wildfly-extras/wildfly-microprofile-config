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

import static org.wildfly.extension.microprofile.config.ServiceNames.CONFIG_SOURCE_PROVIDER;

import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.controller.OperationContext;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class ConfigSourceProviderService implements Service<ConfigSourceProvider> {

    private final ConfigSourceProvider configSourceProvider;

    public ConfigSourceProviderService(String name, ConfigSourceProvider configSourceProvider) {
        this.configSourceProvider = configSourceProvider;
    }

    public static void install(OperationContext context, String name, ConfigSourceProvider configSourceProvider) {
        ConfigSourceProviderService service = new ConfigSourceProviderService(name, configSourceProvider);
        ServiceBuilder<ConfigSourceProvider> serviceBuilder = context.getServiceTarget().addService(CONFIG_SOURCE_PROVIDER.append(name), service);
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
