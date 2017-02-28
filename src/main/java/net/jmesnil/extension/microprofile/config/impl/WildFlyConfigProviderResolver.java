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

package net.jmesnil.extension.microprofile.config.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class WildFlyConfigProviderResolver extends ConfigProviderResolver {

    public static final WildFlyConfigProviderResolver INSTANCE = new WildFlyConfigProviderResolver();

    private Config defaultConfig = getBuilder()
            .addDefaultSources()
            .build();

    private Map<ClassLoader,Config> configsForClassLoader = new HashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {
        Config config = configsForClassLoader.get(classLoader);
        if (config != null) {
            return config;
        } else {
            return defaultConfig;
        }
    }

    @Override
    public ConfigProvider.ConfigBuilder getBuilder() {
        return new WildFlyConfigBuilder();
    }

    @Override
    public void setConfig(Config config, ClassLoader classLoader) {
        configsForClassLoader.put(classLoader, config);

    }

    @Override
    public void releaseConfig(Config config) {
        Iterator<Map.Entry<ClassLoader, Config>> iterator = configsForClassLoader.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ClassLoader, Config> entry = iterator.next();
            if (entry.getValue() == config) {
                iterator.remove();
                return;
            }
        }
    }
}
