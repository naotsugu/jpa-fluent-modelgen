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
package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.ModelContext;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelAttribute;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import java.util.Map;
import java.util.Objects;

/**
 * Abstract class for attribute class generator.
 * @see RootModelClassGenerator
 * @see JoinModelClassGenerator
 * @see PathModelClassGenerator
 * @author Naotsugu Kobayashi
 */
public abstract class AttributeClassGenerator {

    /** Context of processing. */
    private final ModelContext context;

    /** Representation of static metamodel. */
    private final StaticMetamodelEntity entity;

    /** Import sentences. */
    private final ImportBuilder imports;


    /**
     * Constructor.
     * @param context the context of processing
     * @param entity the representation of static metamodel
     * @param imports the import sentences
     */
    protected AttributeClassGenerator(ModelContext context, StaticMetamodelEntity entity, ImportBuilder imports) {
        this.context = context;
        this.entity = entity;
        this.imports = imports;
    }


    /**
     * Generate class.
     * @return the generated class definition
     */
    public String generate() {
        return classTemplate().bind(
            "$EntityClass$", imports.add(entity.getTargetEntityQualifiedName()),
            "$AttributeMethods$", attributeMethods()).getIndentedValue(1);
    }


    private String attributeMethods() {

        StringBuilder sb = new StringBuilder();

        for (StaticMetamodelAttribute attr : entity.getAllAttributes()) {

            var map = Map.of(
                "$EnclosingType$",     imports.add(attr.getEnclosingType().getName()),
                "$ValueType$",         imports.add(attr.getValueType().getName()),
                "$KeyType$",           attr.getAttributeType().isMap() ? imports.add(attr.getKeyType().getName()) : "",
                "$AttributeName$",     capitalize(attr.getName()),
                "$attributeName$",     attr.getName(),
                "$CriteriaPathClass$", criteriaPathClassName(attr),
                "$AttributeJavaType$", attr.getAttributeType().isList() ? imports.add("java.util.List")
                                     : attr.getAttributeType().isSet() ? imports.add("java.util.Set")
                                     : attr.getAttributeType().isCollection() ? imports.add("java.util.Collection")
                                     : attr.getAttributeType().isMap() ? imports.add("java.util.Map") : "");

            if (attr.getAttributeType().isSingular()) {
                singularAttribute(attr, map, sb);
            } else if (attr.getAttributeType().isMap()) {
                mapAttribute(attr, map, sb);
            } else {
                collectionAttribute(attr, map, sb);
            }
        }
        var ret = sb.toString();
        return ret.substring(Template.firstCharIndexOf(ret));
    }


    /**
     * Get the definition of class template.
     * @return the definition of class template
     */
    protected abstract Template classTemplate();


    /**
     * Write the singular attribute methods.
     * @param attr the {@link StaticMetamodelAttribute}
     * @param map the map of binding value
     * @param sb the {@link StringBuilder}
     */
    protected abstract void singularAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb);


    /**
     * Write the plural attribute methods.
     * @param attr the {@link StaticMetamodelAttribute}
     * @param map the map of binding value
     * @param sb the {@link StringBuilder}
     */
    protected abstract void collectionAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb);


    /**
     * Write the map attribute methods.
     * @param attr the {@link StaticMetamodelAttribute}
     * @param map the map of binding value
     * @param sb the {@link StringBuilder}
     */
    protected abstract void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb);


    /**
     * Capitalize the given string.
     * @param str the given string
     * @return Capitalized string
     */
    static String capitalize(String str) {
        return (Objects.isNull(str) || str.isEmpty())
            ? str
            : str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    /**
     * Get the criteria path class name from given attribute.
     * @param attr the attribute
     * @return the criteria path class name
     */
    protected String criteriaPathClassName(StaticMetamodelAttribute attr) {
        if (attr.getValueType().isString()) {
            return "Criteria.StringPath";
        } else if (attr.getValueType().isBoolean()) {
            return "Criteria.BooleanPath";
        } else if (attr.getValueType().isNumber()) {
            return "Criteria.NumberPath<" + imports.add(attr.getValueType().getName()) + ">";
        } else if (attr.getValueType().isComparable()) {
            return "Criteria.ComparablePath<" + imports.add(attr.getValueType().getName()) + ">";
        } else {
            return "Criteria.AnyPath<" + imports.add(attr.getValueType().getName()) + ">";
        }
    }

}
