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

package net.jmesnil.microprofile.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class WildFlyConfigBuilder implements ConfigProvider.ConfigBuilder {

    // sources are not sorted by their ordinals
    private List<ConfigSource> sources = new ArrayList<>();
    private List<Converter<?>> converters = new ArrayList<>();
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Override
    public ConfigProvider.ConfigBuilder addDefaultSources() {
        sources.add(new EnvConfigSource());
        sources.add(new SysPropConfigSource());
        return this;
    }

    @Override
    public ConfigProvider.ConfigBuilder forClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    @Override
    public ConfigProvider.ConfigBuilder withSources(ConfigSource... configSources) {
        for (ConfigSource source: configSources) {
            this.sources.add(source);
        }
        return this;
    }

    @Override
    public ConfigProvider.ConfigBuilder withConverters(Converter<?>[] converters) {
        for (Converter<?> converter: converters) {
            this.converters.add(converter);
        }
        return this;
    }

    @Override
    public Config build() {
        Collections.sort(sources, new Comparator<ConfigSource>() {
            @Override
            public int compare(ConfigSource o1, ConfigSource o2) {
                return o2.getOrdinal() -  o1.getOrdinal();
            }
        });

        return new WildFlyConfig(sources, converters, classLoader);
    }
}
