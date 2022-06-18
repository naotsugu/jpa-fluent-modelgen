/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import java.util.Collection;
import java.util.List;

/**
 * Package name helper.
 * @author Naotsugu Kobayashi
 */
public abstract class PackageNames {

    /** the default package name. */
    public static final String DEFAULT_PACKAGE_NAME = "com.mammb.code.jpa.core";


    /**
     * Get the common package name.
     * @param modelEntities the metamodel entities
     * @return the common package name
     */
    public static String createCommonPackageName(Collection<StaticMetamodelEntity> modelEntities) {
        return createCommonPackageName(modelEntities.stream()
            .map(StaticMetamodelEntity::getQualifiedName).toList());
    }


    /**
     * Get the common package name.
     * @param modelEntities the metamodel entities
     * @return the common package name
     */
    public static String createCommonPackageName(List<String> modelEntities) {
        var name = modelEntities.stream()
            .reduce(modelEntities.get(0), PackageNames::getCommonPrefix);
        if (name.isBlank()) {
            name = modelEntities.stream().reduce("", (s1, s2) -> {
                if (s1.isBlank()) {
                    return s2;
                } else {
                    return (s1.split("\\.").length > s2.split("\\.").length) ? s2 : s1;
                }
            });
        }
        name = name.substring(0, name.lastIndexOf('.'));
        return name.isBlank() ? DEFAULT_PACKAGE_NAME : name;
    }


    /**
     * Get the common prefix string for a given string.
     * @param s1 the string to be compared
     * @param s2 the string to be compared
     * @return the common prefix string
     */
    private static String getCommonPrefix(String s1, String s2) {
        var minLength = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return s1.substring(0, i);
            }
        }
        return s1.substring(0, minLength);
    }

}
