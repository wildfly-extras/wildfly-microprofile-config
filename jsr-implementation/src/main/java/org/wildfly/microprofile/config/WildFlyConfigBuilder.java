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

package org.wildfly.microprofile.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import javax.config.Config;
import javax.config.spi.ConfigBuilder;
import javax.config.spi.ConfigSource;
import javax.config.spi.ConfigSourceProvider;
import javax.config.spi.Converter;


/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class WildFlyConfigBuilder implements ConfigBuilder {

    private static final String META_INF_MICROPROFILE_CONFIG_PROPERTIES = "META-INF/microprofile-config.properties";
    private static final String WEB_INF_MICROPROFILE_CONFIG_PROPERTIES = "WEB-INF/classes/META-INF/microprofile-config.properties";

    // sources are not sorted by their ordinals
    private List<ConfigSource> sources = new ArrayList<>();
    private List<Converter> converters = new ArrayList<>();
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private boolean addDefaultSources = false;
    private boolean addDiscoveredSources = false;
    private boolean addDiscoveredConverters = false;

    public WildFlyConfigBuilder() {
    }

    @Override
    public ConfigBuilder addDiscoveredSources() {
        addDiscoveredSources = true;
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredConverters() {
        addDiscoveredConverters = true;
        return this;
    }

    private List<ConfigSource> discoverSources() {
        List<ConfigSource> discoveredSources = new ArrayList<>();
        ServiceLoader<ConfigSource> configSourceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        configSourceLoader.forEach(configSource -> {
            discoveredSources.add(configSource);
        });

        // load all ConfigSources from ConfigSourceProviders
        ServiceLoader<ConfigSourceProvider> configSourceProviderLoader = ServiceLoader.load(ConfigSourceProvider.class, classLoader);
        configSourceProviderLoader.forEach(configSourceProvider -> {
            configSourceProvider.getConfigSources(classLoader)
                    .forEach(configSource -> {
                        discoveredSources.add(configSource);
                    });
        });
        return discoveredSources;
    }

    private List<Converter> discoverConverters() {
        List<Converter> converters = new ArrayList<>();
        ServiceLoader<Converter> converterLoader = ServiceLoader.load(Converter.class, classLoader);
        converterLoader.forEach(converter -> {
            converters.add(converter);
        });
        return converters;
    }

    @Override
    public ConfigBuilder addDefaultSources() {
        addDefaultSources = true;
        return this;
    }

    private List<ConfigSource> getDefaultSources() {
        List<ConfigSource> defaultSources = new ArrayList<>();

        defaultSources.add(new EnvConfigSource());
        defaultSources.add(new SysPropConfigSource());
        defaultSources.addAll(new PropertiesConfigSourceProvider(META_INF_MICROPROFILE_CONFIG_PROPERTIES, true, classLoader).getConfigSources(classLoader));
        defaultSources.addAll(new PropertiesConfigSourceProvider(WEB_INF_MICROPROFILE_CONFIG_PROPERTIES, true, classLoader).getConfigSources(classLoader));

        return defaultSources;
    }

    @Override
    public ConfigBuilder forClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    @Override
    public ConfigBuilder withSources(ConfigSource... configSources) {
        for (ConfigSource source: configSources) {
            this.sources.add(source);
        }
        return this;
    }

    @Override
    public ConfigBuilder withConverters(Converter<?>[] converters) {
        for (Converter<?> converter: converters) {
            this.converters.add(converter);
        }
        return this;
    }

    @Override
    public Config build() {
        if (addDiscoveredSources) {
            sources.addAll(discoverSources());
        }
        if (addDefaultSources) {
            sources.addAll(getDefaultSources());
        }

        if (addDiscoveredConverters) {
            converters.addAll(discoverConverters());
        }

        Collections.sort(sources, new Comparator<ConfigSource>() {
            @Override
            public int compare(ConfigSource o1, ConfigSource o2) {
                return o2.getOrdinal() -  o1.getOrdinal();
            }
        });

        return new WildFlyConfig(sources, converters);
    }
}
