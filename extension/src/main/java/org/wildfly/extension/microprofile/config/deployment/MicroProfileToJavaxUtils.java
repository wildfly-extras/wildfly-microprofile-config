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

package org.wildfly.extension.microprofile.config.deployment;

import java.util.Map;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
class MicroProfileToJavaxUtils {
    /**
     * Wrap a Eclipse MicroProfile ConfigSource in a javax.config ConfigSource.
     */
    static javax.config.spi.ConfigSource toJavax(final org.eclipse.microprofile.config.spi.ConfigSource microprofileConfigSource) {
        return new javax.config.spi.ConfigSource() {

            @Override
            public Map<String, String> getProperties() {
                return microprofileConfigSource.getProperties();
            }

            @Override
            public String getValue(String propertyName) {
                return microprofileConfigSource.getValue(propertyName);
            }

            @Override
            public String getName() {
                return microprofileConfigSource.getName();
            }
        };
    }
}
