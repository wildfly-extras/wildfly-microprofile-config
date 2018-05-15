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

package org.wildfly.extension.microprofile.config.app.microprofile;

import static org.wildfly.extension.microprofile.config.SubsystemConfigSourceTask.MY_PROP_FROM_SUBSYSTEM_PROP_NAME;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
@ApplicationPath("/microprofile")
public class TestApplication extends Application {

    @Path("/test")
    public static class Resource {

        @Inject
        Config config;

        @Inject
        @ConfigProperty(name = "my.prop", defaultValue = "BAR")
        String prop1;

        @Inject
        @ConfigProperty(name = "my.other.prop", defaultValue = "no")
        boolean prop2;

        @Inject
        @ConfigProperty(name = MY_PROP_FROM_SUBSYSTEM_PROP_NAME)
        String prop3;

        @Inject
        @ConfigProperty(name = "optional.injected.prop.that.is.not.configured")
        Optional<String> optionalProp;

        @GET
        @Produces("text/plain")
        public Response doGet() {
            Optional<String> foo = config.getOptionalValue("my.prop.never.defined", String.class);
            StringBuilder text = new StringBuilder();
            text.append("my.prop.never.defined = " + foo + "\n");
            text.append("my.prop = " + prop1 + "\n");
            text.append("my.other.prop = " + prop2 + "\n");
            text.append("optional.injected.prop.that.is.not.configured = " + optionalProp + "\n");
            text.append(MY_PROP_FROM_SUBSYSTEM_PROP_NAME + " = " + prop3 + "\n");
            return Response.ok(text).build();
        }
    }
}
