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

import io.smallrye.config.SmallRyeConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.as.controller.OperationContext;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;

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
        ConfigProviderResolver.setInstance(SmallRyeConfigProviderResolver.INSTANCE);
    }

    @Override
    public void stop(StopContext context) {
        ConfigProviderResolver.setInstance(null);

    }

    @Override
    public ConfigProviderResolver getValue() throws IllegalStateException, IllegalArgumentException {
        return SmallRyeConfigProviderResolver.INSTANCE;
    }
}
