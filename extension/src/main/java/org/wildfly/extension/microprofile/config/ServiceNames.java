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

import org.jboss.msc.service.ServiceName;

/**
 * Service Names for Eclipse MicroProfile Config objects.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public interface ServiceNames {
    ServiceName MICROPROFILE_CONFIG = ServiceName.JBOSS.append("eclipse", "microprofile", "config");

    ServiceName CONFIG_PROVIDER = MICROPROFILE_CONFIG.append("config-provider");
    ServiceName CONFIG_SOURCE = MICROPROFILE_CONFIG.append("config-source");
    ServiceName CONFIG_SOURCE_PROVIDER = MICROPROFILE_CONFIG.append("config-source-provider");
}
