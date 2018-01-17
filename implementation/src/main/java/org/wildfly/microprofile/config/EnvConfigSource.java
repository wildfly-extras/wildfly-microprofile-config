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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class EnvConfigSource implements ConfigSource, Serializable {

    EnvConfigSource() {
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(System.getenv());
    }

    @Override
    public int getOrdinal() {
        return 300;
    }

    @Override
    public String getValue(String name) {
        if (name == null) {
            return null;
        }

        String envVarName = name;
        // exact match
        if (System.getenv().containsKey(envVarName)) {
            return System.getenv(envVarName);
        }
        // replace dots by underscores
        envVarName = envVarName.replace('.', '_');
        if (System.getenv().containsKey(envVarName)) {
            return System.getenv(envVarName);
        }
        // replace dots by underscores and use upper case
        envVarName = envVarName.toUpperCase();
        if (System.getenv().containsKey(envVarName)) {
            return System.getenv(envVarName);
        }
        return null;
    }

    @Override
    public String getName() {
        return "EnvConfigSource";
    }
}
