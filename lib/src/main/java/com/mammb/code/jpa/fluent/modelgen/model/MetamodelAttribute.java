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

/**
 * The Metamodel attribute.
 * @author Naotsugu Kobayashi
 */
public interface MetamodelAttribute {

    /**
     * Get the attribute name.
     * e.g. {@code userName}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> userName;
     * }</pre>
     * @return the attribute name
     */
    String getName();


    /**
     * Get the attribute type.
     * e.g. {@code jakarta.persistence.metamodel.SingularAttribute}
     * @return the attribute type
     */
    AttributeType getAttributeType();


    /**
     * Get the type containing the represented attribute.
     *
     * e.g. {@code RootEntity}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> userName;
     * }</pre>
     *
     * @return The type containing the represented attribute
     */
    TypeArgument getEnclosingType();


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
    TypeArgument getKeyType();


    /**
     * Get the type of the represented attribute.
     *
     * e.g. {@code String}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> userName;
     * }</pre>
     *
     * @return The type of the represented attribute
     */
    TypeArgument getValueType();

}
