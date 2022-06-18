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

import com.mammb.code.jpa.fluent.modelgen.Context;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Representation of repository trait.
 *
 * @author Naotsugu Kobayashi
 */
public class RepositoryTraitType {

    /** Annotation type. */
    public static final String ANNOTATION_TYPE = "com.mammb.code.jpa.core.RepositoryTrait";

    /** Context of processing. */
    private final Context context;

    /** The type element of repository trait. */
    private final TypeElement element;

    /** The type arguments. */
    private final List<TypeArgument> typeArguments;


    /**
     * Private constructor.
     * @param context the context of processing
     * @param element the repository root type element
     */
    protected RepositoryTraitType(Context context, TypeElement element) {
        this.context = context;
        this.element = element;
        this.typeArguments = typeArgumentsOf(context, element);
    }


    /**
     * Create a RepositoryTraitType.
     * @param context the context of processing
     * @param element the static metamodel type element
     * @return RepositoryRootType
     */
    public static Optional<RepositoryTraitType> of(final Context context, final Element element) {
        return (isRepositoryTraitType(element) && element instanceof TypeElement typeElement)
            ? Optional.of(new RepositoryTraitType(context, typeElement))
            : Optional.empty();
    }


    private static boolean isRepositoryTraitType(Element element) {
        return element.getKind().isClass() &&
            annotationTypes(element).stream().anyMatch(ANNOTATION_TYPE::equals);
    }


    /**
     * Get the annotation types of target element.
     * @param element the target element
     * @return the annotation types
     */
    private static List<String> annotationTypes(Element element) {
        return element.getAnnotationMirrors().stream()
            .map(AnnotationMirror::getAnnotationType)
            .map(Object::toString)
            .toList();
    }


    private static List<TypeArgument> typeArgumentsOf(Context context, TypeElement element) {
        element.getTypeParameters().stream().forEach(e -> context.logInfo(e.toString()));
        return new ArrayList<>();
    }

}
