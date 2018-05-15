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

                assertEquals(envProp, cs.getValue("wildfly-mp-config-prop"));
                assertFalse(cs.getPropertyNames().contains("wildfly-mp-config-prop"));

                assertEquals(envProp, cs.getValue("WILDFLY-MP-CONFIG-PROP"));
                assertFalse(cs.getPropertyNames().contains("WILDFLY-MP-CONFIG-PROP"));
        }
}
