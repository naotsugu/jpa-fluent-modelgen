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

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.util.List;

/**
 * Representation of static metamodel attributes.
 *
 * @author Naotsugu Kobayashi
 */
public class StaticMetamodelAttribute implements MetamodelAttribute {

    /** Context of processing. */
    private final Context context;

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
     * @param context context of processing
     * @param element the static metamodel element
     */
    protected StaticMetamodelAttribute(Context context, Element element) {

        if (!element.asType().toString().startsWith(AttributeType.PACKAGE_NAME) &&
            !element.asType().toString().startsWith(AttributeType.PACKAGE_NAME_LEGACY)) {
            throw new IllegalArgumentException("Unsupported type : " + element.asType().toString());
        }

        this.context = context;
        this.element = element;
        this.name = element.getSimpleName().toString();

        final var declaredType = asType(element);

        this.attributeType = AttributeType.of(declaredType.asElement());

        this.typeArguments = declaredType.getTypeArguments().stream()
            .map(t -> TypeArgument.of(context, t))
            .toList();

        if (typeArguments.size() < 2) {
            throw new IllegalArgumentException("Unsupported type arguments size : "
                + element.asType().toString() + ", " + typeArguments.size());
        }

    }


    /**
     * Create a new {@link StaticMetamodelAttribute} instance with the given entity.
     * @param context context of processing
     * @param element the attribute element
     * @return static metamodel attribute
     */
    public static StaticMetamodelAttribute of(Context context, Element element) {
        return new StaticMetamodelAttribute(context, element);
    }


    @Override
    public AttributeType getAttributeType() {
        return attributeType;
    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public TypeArgument getEnclosingType() {
        return typeArguments.get(0);
    }


    @Override
    public TypeArgument getKeyType() {
        return typeArguments.size() > 2 ? typeArguments.get(1) : null;
    }


    @Override
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
