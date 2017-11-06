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
 * Class that stores the {@link DotName}s of Eclipse MicroProfile Config annotations
 */
public enum MicroProfileConfigAnnotations {

    ConfigProperty("ConfigProperty");

    private final String simpleName;
    private final DotName dotName;

    private MicroProfileConfigAnnotations(String simpleName) {
        this.simpleName = simpleName;
        this.dotName = DotName.createComponentized(Constants.ORG_ECLIPSE_MICROPROFILE_CONFIG_INJECT, simpleName);
    }

    // this can't go on the enum itself
    private static class Constants {
        public static final DotName ORG = DotName.createComponentized(null, "org");
        public static final DotName ORG_ECLIPSE = DotName.createComponentized(ORG, "eclipse");
        public static final DotName ORG_ECLIPSE_MICROPROFILE = DotName.createComponentized(ORG_ECLIPSE, "microprofile");
        public static final DotName ORG_ECLIPSE_MICROPROFILE_CONFIG = DotName.createComponentized(ORG_ECLIPSE_MICROPROFILE, "config");
        public static final DotName ORG_ECLIPSE_MICROPROFILE_CONFIG_INJECT = DotName.createComponentized(ORG_ECLIPSE_MICROPROFILE_CONFIG, "inject");
    }

    public DotName getDotName() {
        return dotName;
    }

    public String getSimpleName() {
        return simpleName;
    }

}
