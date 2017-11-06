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

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.as.server.deployment.DeploymentUnit;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class ConfigMarkers {

    private static final AttachmentKey<Boolean> MICROPROFILE_CONFIG_MARKER = AttachmentKey.create(Boolean.class);
    private static final AttachmentKey<Boolean> JAVAX_CONFIG_MARKER = AttachmentKey.create(Boolean.class);

    public static void markMicroProfileConfig(DeploymentUnit unit) {
        unit.putAttachment(MICROPROFILE_CONFIG_MARKER, Boolean.TRUE);
        if (unit.getParent() != null) {
            markMicroProfileConfig(unit.getParent());
        }
    }

    public static boolean isMicroProfileConfigDeployment(DeploymentUnit unit) {
        return unit.getAttachment(MICROPROFILE_CONFIG_MARKER) != null;
    }

    public static void markJavaxConfig(DeploymentUnit unit) {
        unit.putAttachment(JAVAX_CONFIG_MARKER, Boolean.TRUE);
        if (unit.getParent() != null) {
            markMicroProfileConfig(unit.getParent());
        }
    }

    public static boolean isJavaxConfigDeployment(DeploymentUnit unit) {
        return unit.getAttachment(JAVAX_CONFIG_MARKER) != null;
    }

}
