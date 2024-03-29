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
package com.mammb.code.jpa.fluent.modelgen.model;

import javax.lang.model.element.Element;
import java.util.Arrays;

/**
 * JPA static metamodel attribute type.
 *
 * @author Naotsugu Kobayashi
 */
public enum AttributeType {

    /** SingularAttribute. */
    SINGULAR_ATTRIBUTE("SingularAttribute"),

    /** ListAttribute. */
    LIST_ATTRIBUTE("ListAttribute"),

    /** SetAttribute. */
    SET_ATTRIBUTE("SetAttribute"),

    /** CollectionAttribute. */
    COLLECTION_ATTRIBUTE("CollectionAttribute"),

    /** MapAttribute. */
    MAP_ATTRIBUTE("MapAttribute"),
    ;

    /** Attribute package name. */
    static final String PACKAGE_NAME = "jakarta.persistence.metamodel.";

    /** Legacy attribute package name. */
    static final String PACKAGE_NAME_LEGACY = "javax.persistence.metamodel.";

    /** Attribute type name. */
    private final String name;


    /**
     * Create AttributeType by given name.
     * @param name the Attribute name
     */
    AttributeType(String name) {
        this.name = name;
    }


    /**
     * Determines if it is SingularAttribute or not.
     * @return {@code true} if it is SingularAttribute
     */
    public boolean isSingular() {
        return this == SINGULAR_ATTRIBUTE;
    }


    /**
     * Determines if it is ListAttribute or not.
     * @return {@code true} if it is ListAttribute
     */
    public boolean isList() {
        return this == LIST_ATTRIBUTE;
    }


    /**
     * Determines if it is SetAttribute or not.
     * @return {@code true} if it is SetAttribute
     */
    public boolean isSet() {
        return this == SET_ATTRIBUTE;
    }


    /**
     * Determines if it is CollectionAttribute or not.
     * @return {@code true} if it is CollectionAttribute
     */
    public boolean isCollection() {
        return this == COLLECTION_ATTRIBUTE;
    }


    /**
     * Determines if it is MapAttribute or not.
     * @return {@code true} if it is MapAttribute
     */
    public boolean isMap() {
        return this == MAP_ATTRIBUTE;
    }


    /**
     * Select attribute type by FQCN.
     * @param fqcn FQCN
     * @return the Attribute
     */
    public static AttributeType of(String fqcn) {
        return Arrays.stream(values())
            .filter(e -> fqcn.startsWith(PACKAGE_NAME) || fqcn.startsWith(PACKAGE_NAME_LEGACY))
            .filter(e -> fqcn.replace(PACKAGE_NAME, "").replace(PACKAGE_NAME_LEGACY, "").equals(e.name))
            .findFirst()
            .orElseThrow();
    }


    /**
     * Select attribute type by FQCN.
     * @param element the element
     * @return the Attribute
     */
    public static AttributeType of(Element element) {
        return of(element.toString());
    }


    /**
     * Get attribute simple name.
     * @return the attribute simple name
     */
    public String getSimpleName() {
        return name;
    }

}
