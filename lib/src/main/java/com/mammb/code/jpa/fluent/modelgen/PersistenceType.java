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
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Persistence Type.
 * @author Naotsugu Kobayashi
 */
public enum PersistenceType {

    /** Entity. */
    ENTITY("jakarta.persistence.Entity"),

    /** Embeddable class. */
    EMBEDDABLE("jakarta.persistence.Embeddable"),

    /** Mapped superclass. */
    MAPPED_SUPERCLASS("jakarta.persistence.MappedSuperclass"),

    /** Basic type. */
    BASIC(""),
    ;

    /** FQCN mapped persistence type. */
    public static final Map<String, PersistenceType> mappedFqcn = Map.copyOf(
        Arrays.stream(PersistenceType.values())
            .collect(Collectors.toMap(PersistenceType::getFqcn, UnaryOperator.identity())));

    /** FQCN of Persistence Type. */
    private final String fqcn;


    /**
     * private Constructor.
     */
    PersistenceType(String fqcn) {
        this.fqcn = fqcn;
    }


    /**
     * Get the fqcn of persistence type name.
     * @return the fqcn of persistence type name
     */
    public String getFqcn() {
        return fqcn;
    }

}
