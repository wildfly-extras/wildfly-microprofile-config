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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.PROPERTIES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REMOVE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.io.IOException;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.dmr.ModelNode;

/**
 * Add a config-source with a property class in the microprofile-config subsystem.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class SubsystemConfigSourceTask implements ServerSetupTask {
    public static final String MY_PROP_FROM_SUBSYSTEM_PROP_NAME = "my.prop.from.subsystem";
    public static final String MY_PROP_FROM_SUBSYSTEM_PROP_VALUE = "I'm configured in the subsystem";

    @Override
    public void setup(ManagementClient managementClient, String containerId) throws Exception {
        addConfigSource(managementClient.getControllerClient(), MY_PROP_FROM_SUBSYSTEM_PROP_NAME, MY_PROP_FROM_SUBSYSTEM_PROP_VALUE);
    }

    @Override
    public void tearDown(ManagementClient managementClient, String containerId) throws Exception {
        removeConfigSource(managementClient.getControllerClient());
    }


    private void addConfigSource(ModelControllerClient client, String propName, String propValue) throws IOException {
        ModelNode op;
        op = new ModelNode();
        op.get(OP_ADDR).add(SUBSYSTEM, "microprofile-config");
        op.get(OP_ADDR).add("config-source", "test");
        op.get(OP).set(ADD);
        op.get(PROPERTIES).add(propName, propValue);
        client.execute(op);
    }

    private void removeConfigSource(ModelControllerClient client) throws IOException {
        ModelNode op;
        op = new ModelNode();
        op.get(OP_ADDR).add(SUBSYSTEM, "microprofile-config");
        op.get(OP_ADDR).add("config-source", "test");
        op.get(OP).set(REMOVE);
        client.execute(op);
    }
}
