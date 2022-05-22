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

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * Representation of static metamodel attributes.
 *
 * @author Naotsugu Kobayashi
 */
public class StaticMetamodelAttribute {

    /** Static metamodel element. */
    private final Element element;

    /** Attribute type. */
    private final AttributeType attributeType;

    /** Type arguments. */
    private final List<TypeArgument> typeArguments;

    /** Attribute name. */
    private final String name;


    /**
     * Constructor.
     * @param element the static metamodel element
     * @param types the type utility
     */
    protected StaticMetamodelAttribute(Element element, Types types) {

        if (!element.asType().toString().startsWith(AttributeType.PACKAGE_NAME) &&
            !element.asType().toString().startsWith(AttributeType.PACKAGE_NAME_LEGACY)) {
            throw new IllegalArgumentException("Unsupported type : " + element.asType().toString());
        }

        this.element = element;
        this.name = element.getSimpleName().toString();

        final var declaredType = asType(element);

        this.attributeType = AttributeType.of(declaredType.asElement());

        this.typeArguments = declaredType.getTypeArguments().stream()
            .map(t -> TypeArgument.of(t, types.asElement(t)))
            .toList();

        if (typeArguments.size() < 2) {
            throw new IllegalArgumentException("Unsupported type arguments size : "
                + element.asType().toString() + ", " + typeArguments.size());
        }

    }


    /**
     * Create a new {@link StaticMetamodelAttribute} instance with the given entity.
     * @param element the attribute element
     * @param types the type utils
     * @return static metamodel attribute
     */
    public static StaticMetamodelAttribute of(Element element, Types types) {
        return new StaticMetamodelAttribute(element, types);
    }


    /**
     * Get the attribute type.
     * e.g. jakarta.persistence.metamodel.SingularAttribute
     * @return the attribute type
     */
    public AttributeType getAttributeType() {
        return attributeType;
    }


    /**
     * Get the attribute name.
     * e.g. userName
     * @return the attribute name
     */
    public String getName() {
        return name;
    }


    /**
     * Get the type containing the represented attribute.
     *
     * e.g. {@code RootEntity}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> name;
     * }</pre>
     *
     * @return The type containing the represented attribute
     */
    public TypeArgument getEnclosingType() {
        return typeArguments.get(0);
    }


    /**
     * Get the type of the key of the represented Map.
     *
     * e.g. {@code String}, if you have the following code :
     * <pre>{@code
     *     public static volatile MapAttribute<RootEntity, String, Child> map;
     * }</pre>
     *
     * @return The type of the key of the represented Map
     */
    public TypeArgument getKeyType() {
        return typeArguments.size() > 2 ? typeArguments.get(1) : null;
    }


    /**
     * Get the type of the represented attribute.
     *
     * e.g. {@code String}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> name;
     * }</pre>
     *
     * @return The type of the represented attribute
     */
    public TypeArgument getValueType() {
        return typeArguments.get(typeArguments.size() - 1);
    }


    /**
     * Get the type arguments of attribute.
     * e.g. foo.bar.Customer, java.lang.String
     * @return the type arguments
     */
    public List<String> getTypeArgumentsString() {
        return typeArguments.stream().map(TypeArgument::getName).toList();
    }


    /**
     * Cast as declared type.
     * @param element the element
     * @return the declared type
     */
    private static DeclaredType asType(Element element) {
        if (element.asType() instanceof DeclaredType declaredType) {
            return declaredType;
        }
        throw new IllegalArgumentException(element.toString());
    }

}
