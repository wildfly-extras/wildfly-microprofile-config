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

package org.wildfly.swarm.microprofile.config.example.complex;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.microprofile.config.fraction.MicroProfileConfigFraction;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Swarm swarm = new Swarm();

        swarm.fraction(new MicroProfileConfigFraction()
                .configSource("my-config-source", (cs) -> {
            cs.ordinal(500)
                    .property("my.prop", "Hello, World");
        }));
        // Start the swarm
        swarm.start();

        WebArchive app = ShrinkWrap.create(WebArchive.class)
                .addClass(MyApplication.class)
                .addClass(HelloWorldEndpoint.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        // Deploy your app
        swarm.deploy(app);
    }
}
