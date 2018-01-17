/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.Test;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2018 Red Hat inc.
 */
public class EnvConfigSourceTestCase {

        @Test
        public void testConversionOfEnvVariableNames() {
                String envProp = System.getenv("WILDFLY_MP_CONFIG_PROP");
                assertNotNull(envProp);

                ConfigSource cs = new EnvConfigSource();
                assertEquals(envProp, cs.getValue("WILDFLY_MP_CONFIG_PROP"));
                // the config source returns only the name of the actual env variable
                assertTrue(cs.getPropertyNames().contains("WILDFLY_MP_CONFIG_PROP"));

                assertEquals(envProp, cs.getValue("wildfly_mp_config_prop"));
                assertFalse(cs.getPropertyNames().contains("wildfly_mp_config_prop"));

                assertEquals(envProp, cs.getValue("wildfly.mp.config.prop"));
                assertFalse(cs.getPropertyNames().contains("wildfly.mp.config.prop"));

                assertEquals(envProp, cs.getValue("WILDFLY.MP.CONFIG.PROP"));
                // the config source returns only the name of the actual env variable
                assertFalse(cs.getPropertyNames().contains("WILDFLY.MP.CONFIG.PROP"));
        }
}
