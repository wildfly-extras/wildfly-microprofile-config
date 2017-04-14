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

package net.jmesnil.microprofile.config.impl.converter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class SimpleConverters {

    static final Converter<Boolean> BOOLEAN_CONVERTER = value -> {
        if (value != null) {
            return "TRUE".equalsIgnoreCase(value)
                    || "1".equalsIgnoreCase(value)
                    || "YES".equalsIgnoreCase(value)
                    || "Y".equalsIgnoreCase(value)
                    || "ON".equalsIgnoreCase(value)
                    || "JA".equalsIgnoreCase(value)
                    || "J".equalsIgnoreCase(value)
                    || "OUI".equalsIgnoreCase(value);
        }
        return null;
    };

    static final Converter<Double> DOUBLE_CONVERTER = value -> value != null ? Double.valueOf(value) : null;

    static final Converter<Float> FLOAT_CONVERTER = value -> value != null ? Float.valueOf(value) : null;

    public static final Set<Converter<?>> ALL_CONVERTERS = new HashSet<>();

    static {
        ALL_CONVERTERS.add(BOOLEAN_CONVERTER);
        ALL_CONVERTERS.add(DOUBLE_CONVERTER);
        ALL_CONVERTERS.add(FLOAT_CONVERTER);
    }
}
