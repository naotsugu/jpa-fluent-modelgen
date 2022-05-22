/*
 * Copyright 2022-2023 the original author or authors.
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

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

/**
 * Metamodel attribute type argument.
 * @author Naotsugu Kobayashi
 */
public class TypeArgument {

    /** Type argument mirror. */
    private final TypeMirror typeMirror;

    /** Type argument mirror element. */
    private final Element typeMirrorElement;

    /** Persistence type. */
    private final PersistenceType persistenceType;


    /**
     * Private constructor.
     */
    private TypeArgument(TypeMirror typeMirror, Element typeMirrorElement) {
        this.typeMirror = typeMirror;
        this.typeMirrorElement = typeMirrorElement;
        this.persistenceType = asPersistenceType(typeMirrorElement);
    }


    /**
     * Create the type argument.
     * @param typeMirror Type argument mirror
     * @param typeMirrorElement Type argument mirror element
     * @return the type argument
     */
    public static TypeArgument of(TypeMirror typeMirror, Element typeMirrorElement) {
        return new TypeArgument(typeMirror, typeMirrorElement);
    }


    /**
     * Get the attribute type argument name.
     * @return the attribute type argument name
     */
    public String getName() {
        return typeMirror.toString();
    }


    /**
     * Get the persistence type.
     * @return the persistence type
     */
    public PersistenceType getPersistenceType() {
        return persistenceType;
    }


    /**
     * Gets whether {@link PersistenceType} is a structure or not.
     * @return if {@link PersistenceType} is a structure, then {@code true}
     */
    public boolean isStruct() {
        return getPersistenceType() == PersistenceType.ENTITY
            || getPersistenceType() == PersistenceType.EMBEDDABLE
            || getPersistenceType() == PersistenceType.MAPPED_SUPERCLASS;
    }


    /**
     * Gets whether {@link PersistenceType} is a basic or not.
     * @return if {@link PersistenceType} is a basic, then {@code true}
     */
    public boolean isBasic() {
        return getPersistenceType() == PersistenceType.BASIC;
    }


    /**
     * Convert the element as {@link PersistenceType}.
     * @param elm the element
     * @return {@link PersistenceType}
     */
    private static PersistenceType asPersistenceType(final Element elm) {

        if (Objects.isNull(elm)) {
            return PersistenceType.BASIC;
        }

        return elm.getAnnotationMirrors().stream()
            .map(am -> am.getAnnotationType().toString())
            .map(PersistenceType::of)
            .findFirst()
            .orElse(PersistenceType.BASIC);
    }

}
