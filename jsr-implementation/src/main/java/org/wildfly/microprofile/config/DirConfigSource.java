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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.config.spi.ConfigSource;


/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2017 Red Hat inc.
 */
public class DirConfigSource implements ConfigSource {

    private static final int DEFAULT_ORDINAL = 100;

    private final File dir;
    private final int ordinal;
    private final Map<String, String> props;

    DirConfigSource(File dir) {
        this(dir, DEFAULT_ORDINAL);
    }

    public DirConfigSource(File dir, int ordinal) {
        this.dir = dir;
        this.ordinal = ordinal;
        this.props = scan();
    }

    private Map<String, String> scan() {
        Map<String, String> props = new HashMap<>();
        if (dir == null || !dir.isDirectory()) {
            return props;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                String key = file.getName();
                String value = readContent(file);
                props.put(key, value);
            } catch (IOException e) {
                e.printStackTrace();
                // should log some errors...
            }
        }
        return props;
    }

    private String readContent(File file) throws IOException {
        String content = Files.lines(file.toPath())
                .collect(Collectors.joining());
        return content;
    }

    @Override
    public Map<String, String> getProperties() {
        return props;
    }

    @Override
    public String getValue(String key) {
        return props.get(key);
    }

    @Override
    public String getName() {
        return "DirConfigSource[dir=" + dir.getAbsolutePath() + "]";
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }
}
