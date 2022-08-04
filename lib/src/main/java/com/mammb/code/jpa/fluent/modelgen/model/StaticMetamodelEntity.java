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

import com.mammb.code.jpa.fluent.modelgen.context.ModelContext;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final ModelContext context;

    /** Static metamodel type element. */
    private final TypeElement element;

    /** Static metamodel type attributes. */
    private final List<StaticMetamodelAttribute> attributes;

    /** Static metamodel type attributes. */
    private final List<StaticMetamodelAttribute> parentAttributes;

    /** Children staticMetamodelEntity. */
    private final List<StaticMetamodelEntity> children;


    /**
     * Private constructor.
     * @param context the context of processing
     * @param element the static metamodel type element
     */
    protected StaticMetamodelEntity(ModelContext context, TypeElement element) {
        this.context = context;
        this.element = element;
        this.attributes = attributes(context, element.getEnclosedElements());
        this.parentAttributes = parentAttributes(context, element);
        this.children = new ArrayList<>();
        context.setJakarta(annotationTypes(element).contains(ANNOTATION_TYPE));
    }


    /**
     * Create the {@link StaticMetamodelEntity} for the given element.
     * @param context the context of processing
     * @param element the static metamodel type element
     * @return StaticMetamodelEntity
     */
    public static Optional<StaticMetamodelEntity> of(ModelContext context, Element element) {
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
     * @return the superclass name of this static metamodel, if the superclass is an Object, return {@code ""}.
     */
    public String getSuperClass() {
        TypeMirror superClass = element.getSuperclass();
        return (Object.class.getCanonicalName().equals(superClass.toString())) ? "" : superClass.toString();
    }


    /**
     * Get the superclass name of this static metamodel target entity.
     * e.g. {@code foo.Book_} -> {@code foo.Book}
     * @return the superclass name of this static metamodel target entity,
     * if the superclass is an Object, return {@code ""}.
     */
    public String getSuperQualifiedName() {
        var name = getSuperClass();
        return name.isBlank() ? "" : name.substring(0, name.length() - 1);
    }


    /**
     * Get the super entity fqcn, if the target entity of this static metamodel has a parent entity.
     * @return the super entity fqcn. MappedSuperclass is not included.
     */
    public Optional<String> getSuperEntityQualifiedName() {
        var name = getSuperQualifiedName();
        if (name.isBlank()) {
            return Optional.empty();
        }
        if (context.getElementUtils().getTypeElement(name).getAnnotationMirrors().stream()
            .map(am -> am.getAnnotationType().toString())
            .map(PersistenceType::of)
            .anyMatch(PersistenceType::isEntity)) {
            return Optional.of(name);
        }
        return Optional.empty();
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
     * Static metamodel class with {@code _} removed from the end of the name.
     * @return the entity class name
     */
    public String getTargetEntityName() {
        return getSimpleName().substring(0, getSimpleName().length() - 1);
    }


    /**
     * Get the entity class qualified name.
     * Static metamodel class with {@code _} removed from the end of the name.
     * @return the entity class name
     */
    public String getTargetEntityQualifiedName() {
        return getQualifiedName().substring(0, getQualifiedName().length() - 1);
    }


    /**
     * Get the static metamodel attribute list.
     * @return the static metamodel attribute list
     */
    public List<StaticMetamodelAttribute> getAttributes() {
        return attributes;
    }


    /**
     * Get the static metamodel attribute list.
     * Include super class attribute.
     * @return the static metamodel attribute list
     */
    public List<StaticMetamodelAttribute> getAllAttributes() {
        var ret = new ArrayList<>(parentAttributes);
        ret.addAll(attributes);
        return ret;
    }


    /**
     * Gets whether the target element has a static metamodel annotation.
     * @param element the target element
     * @return If the target element has a static metamodel annotation, return {@code true}.
     */
    private static boolean isStaticMetamodel(Element element) {
        return element.getKind().isClass() && annotationTypes(element).stream()
                .anyMatch(ann -> ANNOTATION_TYPE.equals(ann) || ANNOTATION_TYPE_LEGACY.equals(ann));
    }


    /**
     * Get the annotation types of target element.
     * e.g. {@code jakarta.persistence.metamodel.StaticMetamodel}
     * or {@code javax.persistence.metamodel.StaticMetamodel}
     *
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
     * Get the metamodel target entity as {@link TypeArgument}.
     * @return the metamodel target entity
     */
    public TypeArgument getTargetEntity() {
        return TypeArgument.of(context, getTargetEntityTypeElement().asType());
    }


    /**
     * Get the metamodel target entity as {@link TypeElement}.
     * @return the metamodel target entity as {@link TypeElement}
     */
    private TypeElement getTargetEntityTypeElement() {
        var metamodelName = element.getQualifiedName().toString();
        var entityName = metamodelName.substring(0, metamodelName.length() - 1);
        return context.getElementUtils().getTypeElement(entityName);
    }


    /**
     * Create the {@link StaticMetamodelAttribute}s from the given enclosed elements
     * @param context the context of processing
     * @param enclosedElements the enclosed elements
     * @return the list of StaticMetamodelAttributes
     */
    private static List<StaticMetamodelAttribute> attributes(ModelContext context, List<? extends Element> enclosedElements) {
        return ElementFilter.fieldsIn(enclosedElements).stream()
            .filter(e -> e.asType().toString().startsWith(AttributeType.PACKAGE_NAME)
                      || e.asType().toString().startsWith(AttributeType.PACKAGE_NAME_LEGACY))
            .map(e -> StaticMetamodelAttribute.of(context, e))
            .toList();
    }


    /**
     * Create the {@link StaticMetamodelAttribute}s from the given typeElement super class.
     * @param context the context of processing
     * @param element the static metamodel type element
     * @return the list of StaticMetamodelAttributes
     */
    private static List<StaticMetamodelAttribute> parentAttributes(ModelContext context, TypeElement element) {
        List<StaticMetamodelAttribute> attributes = new ArrayList<>();
        TypeMirror superclass = element.getSuperclass();
        while (Objects.nonNull(superclass)) {
            Element elem = context.getTypeUtils().asElement(superclass);
            if (isStaticMetamodel(elem) && elem instanceof TypeElement typeElement) {
                attributes.addAll(attributes(context, typeElement.getEnclosedElements()));
                superclass = typeElement.getSuperclass();
            } else {
                break;
            }
        }
        return attributes;
    }


    /**
     * Get whether this static metamodel is for Entity.
     * @return {@code true} if this static metamodel is for Entity
     */
    public boolean isEntityMetamodel() {
        return getTargetEntityTypeElement().getAnnotationMirrors().stream()
            .map(am -> am.getAnnotationType().toString())
            .map(PersistenceType::of)
            .anyMatch(type -> type == PersistenceType.ENTITY);
    }


    /**
     * Get the entity id type.
     * e.g. {@code java.lang.Long}
     * @return the entity id type
     */
    public Optional<TypeMirror> getEntityIdType() {
        return isEntityMetamodel()
            ? findIdField(getTargetEntityTypeElement()).map(VariableElement::asType)
            : Optional.empty();
    }


    /**
     * Find id field.
     * @param element {@link TypeElement}
     * @return the VariableElement of id
     */
    private Optional<VariableElement> findIdField(TypeElement element) {

        return ElementFilter.fieldsIn(element.getEnclosedElements()).stream()
            .filter(e -> e.getAnnotationMirrors().stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(Object::toString)
                .anyMatch(ann -> ann.equals("jakarta.persistence.Id") || ann.equals("javax.persistence.Id")))
            .findFirst()
            .or(() -> findIdField(context.getElementUtils().getTypeElement(element.getSuperclass().toString())));

    }


    /**
     * Add the child {@link StaticMetamodelEntity}.
     * @param child the child {@link StaticMetamodelEntity}
     */
    public void addChild(StaticMetamodelEntity child) {
        children.add(child);
    }


    /**
     * Get the descendants.
     * @return the descendants
     */
    public List<StaticMetamodelEntity> getDescendants() {
        List<StaticMetamodelEntity> ret = new ArrayList<>(children);
        for (StaticMetamodelEntity e : children) {
            ret.addAll(e.getDescendants());
        }
        return ret;
    }

}
