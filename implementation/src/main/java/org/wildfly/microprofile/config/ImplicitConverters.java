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
package org.wildfly.microprofile.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * Based on GERONIMO-6595 support implicit converters.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
class ImplicitConverters {

    // TODO add support for Enum#valueOf(Class, String)
    static Converter getConverter(Class<?> clazz) {
        for (Converter converter : new Converter[] {
                getConverterFromConstructor(clazz, String.class),
                getConverterFromConstructor(clazz, CharSequence.class),
                getConverterFromStaticMethod(clazz, "valueOf", String.class),
                getConverterFromStaticMethod(clazz, "valueOf", CharSequence.class),
                getConverterFromStaticMethod(clazz, "parse", String.class),
                getConverterFromStaticMethod(clazz, "parse", CharSequence.class)
        }) {
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }

    private static Converter getConverterFromConstructor(Class<?> clazz, Class<?> paramType) {
        try {
            final Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(paramType);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return value -> {
                try {
                    return declaredConstructor.newInstance(value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Converter getConverterFromStaticMethod(Class<?> clazz, String methodName, Class<?> paramType) {
        try {
            final Method method = clazz.getMethod(methodName, paramType);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            if (Modifier.isStatic(method.getModifiers())) {
                return value -> {
                    try {
                        return method.invoke(null, value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        } catch (NoSuchMethodException e) {
        }
        return null;
    }
}
