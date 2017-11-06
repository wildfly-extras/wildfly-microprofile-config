/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

import org.jboss.jandex.DotName;

/**
 * Class that stores the {@link DotName}s of JSR #382 Config annotations
 */
public enum JavaxConfigAnnotations {

    ConfigProperty("ConfigProperty");

    private final String simpleName;
    private final DotName dotName;

    private JavaxConfigAnnotations(String simpleName) {
        this.simpleName = simpleName;
        this.dotName = DotName.createComponentized(Constants.JAVAX_CONFIG_INJECT, simpleName);
    }

    // this can't go on the enum itself
    private static class Constants {
        public static final DotName JAVAX = DotName.createComponentized(null, "javax");
        public static final DotName JAVAX_CONFIG = DotName.createComponentized(JAVAX, "config");
        public static final DotName JAVAX_CONFIG_INJECT = DotName.createComponentized(JAVAX_CONFIG, "inject");
    }

    public DotName getDotName() {
        return dotName;
    }

    public String getSimpleName() {
        return simpleName;
    }

}
