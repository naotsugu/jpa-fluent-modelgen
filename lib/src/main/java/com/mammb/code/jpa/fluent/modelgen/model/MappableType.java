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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Representation of mappable annotated class.
 * @author Naotsugu Kobayashi
 */
public class MappableType {

    /** Annotation type. */
    public static final String ANNOTATION_TYPE = "com.mammb.code.jpa.core.Mappable";

    /** Context of processing. */
    private final Context context;

    /** The type element of MappableType. */
    private final TypeElement element;


    /**
     * Private constructor.
     * @param context the context of processing
     * @param element the type element of MappableType
     */
    protected MappableType(Context context, TypeElement element) {
        this.context = context;
        this.element = element;
    }


    /**
     * Create a MappableType.
     * @param context the context of processing
     * @param element the static metamodel type element
     * @return RepositoryRootType
     */
    public static Optional<MappableType> of(final Context context, final Element element) {
        return (isMappableType(element) && element instanceof TypeElement typeElement)
            ? Optional.of(new MappableType(context, typeElement))
            : Optional.empty();
    }


    /**
     * Get the qualified name of the mappable type class.
     * @return the qualified name of the mappable type class
     */
    public String getQualifiedName() {
        return element.getQualifiedName().toString();
    }


    /**
     * Get name of the mappable type class.
     * @return name of the mappable type class
     */
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }


    /**
     * Get the constructor arg names.
     * @return the constructor arg names
     */
    public List<String> getConstructorArgTypeNames() {
        return getConstructorArgTypes().stream().map(Objects::toString).toList();
    }


    /**
     * Get the constructor argument types.
     * @return the constructor argument types
     */
    private List<TypeMirror> getConstructorArgTypes() {
        return getPrimaryConstructor().map(ExecutableElement::getParameters)
            .orElse(Collections.emptyList()).stream()
            .map(VariableElement::asType).toList();
    }


    /**
     * Get the target element constructor.
     * @return the target element constructor
     */
    private Optional<ExecutableElement> getPrimaryConstructor() {
        return element.getEnclosedElements().stream()
            .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR)
            .filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
            .map(ExecutableElement.class::cast)
            .max(Comparator.comparingInt(e -> e.getParameters().size()));
    }


    /**
     * Gets whether the target element has a Mappable annotation.
     * @param element the target element
     * @return If the target element has a Mappable annotation, then return {@code true}.
     */
    private static boolean isMappableType(Element element) {
        return (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.RECORD)
            && annotationTypes(element).stream().anyMatch(ANNOTATION_TYPE::equals);
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

}
