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

import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.as.controller.PersistentResourceXMLParser;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class SubsytemParser_1_0  extends PersistentResourceXMLParser {
    /**
     * The name space used for the {@code subsystem} element
     */
    public static final String NAMESPACE = "urn:wildfly:microprofile-config:1.0";

    static final PersistentResourceXMLParser INSTANCE = new SubsytemParser_1_0();

    private static final PersistentResourceXMLDescription xmlDescription;

    static {
        xmlDescription = builder(new SubsystemDefinition(), NAMESPACE)
                .addChild(builder(new ConfigSourceDefinition())
                        .addAttributes(
                                ConfigSourceDefinition.ORDINAL,
                                ConfigSourceDefinition.PROPERTIES,
                                ConfigSourceDefinition.CLASS,
                                ConfigSourceDefinition.DIR))
                .addChild(builder(new ConfigSourceProviderDefinition())
                        .addAttributes(
                                ConfigSourceProviderDefinition.CLASS))
                .build();
    }

    @Override
    public PersistentResourceXMLDescription getParserDescription() {
        return xmlDescription;
    }
}
