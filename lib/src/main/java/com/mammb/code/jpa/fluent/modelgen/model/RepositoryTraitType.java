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
import com.mammb.code.jpa.fluent.modelgen.writer.ImportBuilder;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    private final List<TypeParameterElement> typeParameters;


    /**
     * Private constructor.
     * @param context the context of processing
     * @param element the repository root type element
     */
    protected RepositoryTraitType(Context context, TypeElement element) {
        this.context = context;
        this.element = element;
        this.typeParameters = List.copyOf(element.getTypeParameters());
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


    /**
     * Get the qualified name.
     * @return the qualified name
     */
    public String getQualifiedName() {
        return element.toString();
    }


    /**
     * Create the extends clause.
     * @param entity {@link StaticMetamodelEntity}
     * @param imports {@link ImportBuilder}
     * @return the extends clause
     */
    public String createExtendsClause(StaticMetamodelEntity entity, ImportBuilder imports) {
        if (typeParameters.isEmpty()) {
            return "";
        }

        var names = targetEntityQualifiedNames(element);
        if (!names.isEmpty() && !names.contains(entity.getTargetEntityQualifiedName())) {
            return "";
        }

        var name = imports.add(element.toString());
        return name + typeParametersString()
            .replace("PK", "%1$s")
            .replace("E", "%2$s")
            .replace("R", "%2$s_Root_<%2$s>");
    }


    private static boolean isRepositoryTraitType(Element element) {
        return element.getKind().isInterface() &&
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


    /**
     * Get the type parameters as string.
     * e.g. {@code <PK, E, R>}.
     * @return the type parameters as string
     */
    private String typeParametersString() {
        if (element.asType() instanceof DeclaredType declaredType) {
            return declaredType.toString().replace(element.toString(), "");
        }
        return "";
    }


    /**
     * RepositoryTrait annotation value class name.
     * FIXME use AnnotationValueVisitor
     * @param element The type element of repository trait
     * @return the target class names
     */
    private static List<String> targetEntityQualifiedNames(Element element) {
        // FIXME use AnnotationValueVisitor
        return element.getAnnotationMirrors().stream()
            .filter(annotationMirror -> ANNOTATION_TYPE.equals(annotationMirror.getAnnotationType().toString()))
            .map(annotationMirror -> annotationMirror.getElementValues().entrySet())
            .flatMap(Collection::stream)
            .filter(e -> e.getKey().getSimpleName().toString().equals("value"))
            .map(Map.Entry::getValue)
            .map(AnnotationValue::getValue)
            .map(List.class::cast)
            .map(Object::toString)
            .map(s -> List.of(s.split(",")))
            .flatMap(Collection::stream)
            .map(s -> s.substring(0, s.lastIndexOf(".")))
            .toList();
    }

}
