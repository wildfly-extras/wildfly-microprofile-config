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

package org.wildfly.microprofile.config.inject;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import javax.config.Config;
import javax.config.ConfigProvider;
import javax.config.inject.ConfigProperty;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.wildfly.microprofile.config.WildFlyConfig;

/**
 * CDI producer for {@link Config} bean.
 *
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
@ApplicationScoped
public class ConfigProducer implements Serializable{

    @Produces
    Config getConfig(InjectionPoint injectionPoint) {
        // return the Config for the TCCL
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return ConfigProvider.getConfig(tccl);
    }

    @Dependent
    @Produces @ConfigProperty
    String produceStringConfigProperty(InjectionPoint ip) {
        return getValue(ip, String.class);
    }

    @Dependent
    @Produces @ConfigProperty
    Long getLongValue(InjectionPoint ip) {
        return getValue(ip, Long.class);
    }

    @Dependent
    @Produces @ConfigProperty
    Integer getIntegerValue(InjectionPoint ip) {
        return getValue(ip, Integer.class);
    }

    @Dependent
    @Produces @ConfigProperty
    Float produceFloatConfigProperty(InjectionPoint ip) {
        return getValue(ip, Float.class);
    }

    @Dependent
    @Produces @ConfigProperty
    Double produceDoubleConfigProperty(InjectionPoint ip) {
        return getValue(ip, Double.class);
    }

    @Dependent
    @Produces @ConfigProperty
    Boolean produceBooleanConfigProperty(InjectionPoint ip) {
        return getValue(ip, Boolean.class);
    }

    @SuppressWarnings("unchecked")
    @Dependent
    @Produces @ConfigProperty
    <T> Optional<T> produceOptionalConfigValue(InjectionPoint injectionPoint) {
        Type type = injectionPoint.getAnnotated().getBaseType();
        final Class<T> valueType;
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            valueType = unwrapType(typeArguments[0]);
        } else {
            valueType = (Class<T>) String.class;
        }
        return Optional.ofNullable(getValue(injectionPoint, valueType));
    }

    private <T> Class<T> unwrapType(Type type) {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return (Class<T>) type;
    }

    private <T> T getValue
            (InjectionPoint injectionPoint, Class<T> target) {
        Config config = getConfig(injectionPoint);
        String name = getName(injectionPoint);
        try {
            if (name == null) {
                return null;
            }
            Optional<T> optionalValue = config.getOptionalValue(name, target);
            if (optionalValue.isPresent()) {
                return optionalValue.get();
            } else {
                String defaultValue = getDefaultValue(injectionPoint);
                if (defaultValue != null) {
                    return ((WildFlyConfig)config).convert(defaultValue, target);
                } else {
                    return null;
                }
            }
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String getName(InjectionPoint injectionPoint) {
        for (Annotation qualifier : injectionPoint.getQualifiers()) {
            if (qualifier.annotationType().equals(ConfigProperty.class)) {
                ConfigProperty configProperty = ((ConfigProperty)qualifier);
                return ConfigExtension.getConfigKey(injectionPoint, configProperty);
            }
        }
        return null;
    }

    private String getDefaultValue(InjectionPoint injectionPoint) {
        for (Annotation qualifier : injectionPoint.getQualifiers()) {
            if (qualifier.annotationType().equals(ConfigProperty.class)) {
                return ((ConfigProperty) qualifier).defaultValue();
            }
        }
        return null;
    }
}
