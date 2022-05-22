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
package com.mammb.code.jpa.fluent.modelgen;

import java.util.Arrays;
import java.util.Objects;

/**
 * Persistence Type.
 * @author Naotsugu Kobayashi
 */
public enum PersistenceType {

    /** Entity. */
    ENTITY("Entity"),

    /** Embeddable class. */
    EMBEDDABLE("Embeddable"),

    /** Mapped superclass. */
    MAPPED_SUPERCLASS("MappedSuperclass"),

    /** Basic type. */
    BASIC(""),
    ;

    /** Attribute package name. */
    static final String PACKAGE_NAME = "jakarta.persistence.";

    /** Legacy attribute package name. */
    static final String PACKAGE_NAME_LEGACY = "javax.persistence.";

    /** Name of Persistence Type. */
    private final String name;


    /**
     * private Constructor.
     */
    PersistenceType(String name) {
        this.name = name;
    }


    /**
     * Get the persistence type from given fqcn.
     * @param fqcn FQCN
     * @return the persistence type
     */
    public static PersistenceType of(String fqcn) {
        if (Objects.isNull(fqcn) || fqcn.isEmpty()) {
            return BASIC;
        }
        var str = fqcn.replace(PACKAGE_NAME, "").replace(PACKAGE_NAME_LEGACY, "");
        return Arrays.stream(PersistenceType.values())
            .filter(value -> value.name.equals(str))
            .findFirst()
            .orElse(BASIC);
    }

}
