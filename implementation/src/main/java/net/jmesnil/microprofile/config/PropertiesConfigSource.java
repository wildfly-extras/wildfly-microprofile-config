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
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class PropertiesConfigSource implements ConfigSource {

    private static final String CONFIG_ORDINAL_KEY = "config_ordinal";
    private static final String CONFIG_ORDINAL_DEFAULT_VALUE = "100";

    private final Map<String, String> properties;
    private final String source;
    private final int ordinal;

    public PropertiesConfigSource(URL url) throws IOException {
        this.source = url.toString();
        try (InputStream in = url.openStream()) {
            Properties p = new Properties();
            p.load(in);
            properties = new HashMap(p);
        }
        this.ordinal = Integer.valueOf(properties.getOrDefault(CONFIG_ORDINAL_KEY, CONFIG_ORDINAL_DEFAULT_VALUE));
    }
    public PropertiesConfigSource(Properties properties, String source) {
        this.properties = new HashMap(properties);
        this.source = source;
        this.ordinal = Integer.valueOf(properties.getProperty(CONFIG_ORDINAL_KEY, CONFIG_ORDINAL_DEFAULT_VALUE));
    }

    public PropertiesConfigSource(Map<String, String> properties, String source, int ordinal) {
        this.properties = new HashMap(properties);
        this.source = source;
        if (properties.containsKey(CONFIG_ORDINAL_KEY)) {
            this.ordinal = Integer.valueOf(properties.getOrDefault(CONFIG_ORDINAL_KEY, CONFIG_ORDINAL_DEFAULT_VALUE));
        } else {
            this.ordinal = ordinal;
        }
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getValue(String s) {
        return properties.get(s);
    }

    @Override
    public String getName() {
        return "PropertiesConfigSource[source=" + source + "]";
    }

    @Override
    public String toString() {
        return getName();
    }
}
