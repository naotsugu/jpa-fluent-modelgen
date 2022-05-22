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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

/**
 * Representation of static metamodel.
 *
 * @author Naotsugu Kobayashi
 */
public class StaticMetamodelEntity {

    /** Annotation type. */
    public static final String ANNOTATION_TYPE = "jakarta.persistence.metamodel.StaticMetamodel";

    /** Legacy annotation type. */
    public static final String ANNOTATION_TYPE_LEGACY = "javax.persistence.metamodel.StaticMetamodel";

    /** Context of processing. */
    private final Context context;

    /** Static metamodel type element. */
    private final TypeElement element;

    /** Static metamodel type attributes. */
    private final List<StaticMetamodelAttribute> attributes;


    /**
     * Private constructor.
     * @param context the context of processing
     * @param element the static metamodel type element
     */
    protected StaticMetamodelEntity(Context context, TypeElement element) {
        this.context = context;
        this.element = element;
        this.attributes = attributes(element.getEnclosedElements(), context.getTypeUtils());
        context.setJakarta(annotationTypes(element).contains(ANNOTATION_TYPE));
    }


    /**
     * Create the StaticMetamodelEntity.
     * @param context the context of processing
     * @param element the static metamodel type element
     * @return StaticMetamodelEntity
     */
    public static Optional<StaticMetamodelEntity> of(Context context, Element element) {
        return (isStaticMetamodel(element) && element instanceof TypeElement typeElement)
            ? Optional.of(new StaticMetamodelEntity(context, typeElement))
            : Optional.empty();
    }


    /**
     * Get name of the static metamodel class.
     * e.g. {@code FooEntity_}
     * @return name of the static metamodel class
     */
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }


    /**
     * Get qualified name of the static metamodel class.
     * e.g. {@code foo.bar.BuzEntity_}
     * @return qualified name of the static metamodel class
     */
    public String getQualifiedName() {
        return element.getQualifiedName().toString();
    }


    /**
     * Get the name of the package to which this static metamodel belongs.
     * @return the name of the package to which this static metamodel belongs
     */
    public String getPackageName() {
        PackageElement packageOf = context.getElementUtils().getPackageOf(element);
        return context.getElementUtils().getName(packageOf.getQualifiedName()).toString();
    }


    /**
     * Get the superclass name of this static metamodel.
     * @return the superclass name of this static metamodel. If the superclass is an Object, return {@code null}.
     */
    public String getSuperClass() {
        TypeMirror superClass = element.getSuperclass();
        return (Object.class.getCanonicalName().equals(superClass.toString())) ? null : superClass.toString();
    }


    /**
     * Get the static metamodel type element.
     * @return the static metamodel type element
     */
    public TypeElement getElement() {
        return element;
    }


    /**
     * Get the entity class name.
     * Static metamodel class with _ removed from the end of the name.
     * @return the entity class name
     */
    public String getTargetEntityName() {
        return getSimpleName().substring(0, getSimpleName().length() - 1);
    }


    /**
     * Get the static metamodel attribute list.
     * @return the static metamodel attribute list
     */
    public List<StaticMetamodelAttribute> getAttributes() {
        return attributes;
    }


    /**
     * Gets whether the target element has a static metamodel annotation.
     * @param element the target element
     * @return If the target element has a static metamodel annotation, return {@code true}.
     */
    private static boolean isStaticMetamodel(Element element) {
        return ElementKind.CLASS == element.getKind() && annotationTypes(element).stream()
                .anyMatch(ann -> ANNOTATION_TYPE.equals(ann) || ANNOTATION_TYPE_LEGACY.equals(ann));
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
     * Create StaticMetamodelAttributes from given the enclosed elements
     * @param enclosedElements the enclosed elements
     * @param types the type Utility
     * @return the list of StaticMetamodelAttributes
     */
    private static List<StaticMetamodelAttribute> attributes(List<? extends Element> enclosedElements, Types types) {
        return ElementFilter.fieldsIn(enclosedElements).stream()
            .filter(e -> e.asType().toString().startsWith(AttributeType.PACKAGE_NAME)
                      || e.asType().toString().startsWith(AttributeType.PACKAGE_NAME_LEGACY))
            .map(e -> StaticMetamodelAttribute.of(e, types))
            .toList();
    }

}
