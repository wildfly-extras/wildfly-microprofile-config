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

package net.jmesnil.microprofile.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class PropertiesConfigSourceProvider implements ConfigSourceProvider {

    private List<ConfigSource> configSources = new ArrayList<>();

    public PropertiesConfigSourceProvider(String propertyFileName, boolean optional, ClassLoader classLoader) {
        try {
            Enumeration<URL> propertyFileUrls = classLoader.getResources(propertyFileName);

            if (!optional && !propertyFileUrls.hasMoreElements()) {
                throw new IllegalStateException(propertyFileName + " wasn't found.");
            }

            while (propertyFileUrls.hasMoreElements()) {
                URL propertyFileUrl = propertyFileUrls.nextElement();
                configSources.add(new PropertiesConfigSource(propertyFileUrl));
            }
        }
        catch (IOException ioe) {
            throw new IllegalStateException("problem while loading microprofile-config.properties files", ioe);
        }

    }

    @Override
    public List<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        return configSources;
    }
}
