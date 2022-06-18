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
package com.mammb.code.jpa.fluent.modelgen.model;

import com.mammb.code.jpa.fluent.modelgen.Context;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

/**
 * Metamodel attribute type argument.
 * @author Naotsugu Kobayashi
 */
public class TypeArgument {

    /** Context of processing. */
    private final Context context;

    /** Type argument mirror. */
    private final TypeMirror typeMirror;

    /** Type argument mirror element. */
    private final Element typeMirrorElement;

    /** Persistence type. */
    private final PersistenceType persistenceType;


    /**
     * Private constructor.
     */
    private TypeArgument(Context context, TypeMirror typeMirror) {
        this.context = context;
        this.typeMirror = typeMirror;
        this.typeMirrorElement = context.getTypeUtils().asElement(typeMirror);
        this.persistenceType = asPersistenceType(typeMirrorElement);
    }


    /**
     * Create the type argument.
     * @param context context of processing
     * @param typeMirror type argument mirror
     * @return the type argument
     */
    public static TypeArgument of(Context context, TypeMirror typeMirror) {
        return new TypeArgument(context, typeMirror);
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
     * Gets whether {@link PersistenceType} is a string.
     * @return if {@link PersistenceType} is a string, then {@code true}
     */
    public boolean isString() {
        return "java.lang.String".equals(getName());
    }


    /**
     * Gets whether {@link PersistenceType} is a boolean.
     * @return if {@link PersistenceType} is a boolean, then {@code true}
     */
    public boolean isBoolean() {
        return "java.lang.Boolean".equals(getName()) || "boolean".equals(getName());
    }


    /**
     * Gets whether {@link PersistenceType} is a number.
     * @return if {@link PersistenceType} is a number, then {@code true}
     */
    public boolean isNumber() {
        return isAssignable(Number.class);
    }


    /**
     * Gets whether {@link PersistenceType} is a comparable.
     * @return if {@link PersistenceType} is a comparable, then {@code true}
     */
    public boolean isComparable() {
        return isAssignable(Comparable.class);
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


    private boolean isAssignable(Class<?> clazz) {
        return typeMirrorElement.getKind().isClass() &&
            context.getTypeUtils().isAssignable(
                typeMirrorElement.asType(),
                context.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType());
    }

}
