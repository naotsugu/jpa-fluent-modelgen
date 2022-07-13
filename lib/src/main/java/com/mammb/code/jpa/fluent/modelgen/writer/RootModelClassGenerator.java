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

/**
 * The root model class generator.
 * @see AttributeClassGenerator
 * @author Naotsugu Kobayashi
 */
public class RootModelClassGenerator extends AttributeClassGenerator {

    private RootModelClassGenerator(ModelContext context, StaticMetamodelEntity entity, ImportBuilder imports) {
        super(context, entity, imports);
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @param imports the import sentences
     * @return Class writer
     */
    public static RootModelClassGenerator of(ModelContext context, StaticMetamodelEntity entity, ImportBuilder imports) {
        return new RootModelClassGenerator(context, entity, imports);
    }


    @Override
    protected Template classTemplate() {
        return Template.of("""
            public static class Root_ implements RootAware<$EntityClass$> {
                private final Root<$EntityClass$> root;
                private final AbstractQuery<?> query;
                private final CriteriaBuilder builder;
                public Root_(Root<$EntityClass$> root, AbstractQuery<?> query, CriteriaBuilder builder) {
                    this.root = root;
                    this.query = query;
                    this.builder = builder;
                }
                @Override public Root<$EntityClass$> get() { return root; }
                @Override public CriteriaBuilder builder() { return builder; }
                @Override public AbstractQuery<?> query() { return query; }
                @Override public Root_ with(Root<$EntityClass$> root, AbstractQuery<?> query) { return new Root_(root, query, builder()); }
                @Override public Class<$EntityClass$> type() { return $EntityClass$.class; }

                $AttributeMethods$
            }
            """);
    }


    protected void singularAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().getPersistenceType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_ join$AttributeName$() {
                    return new $ValueType$Model.Join_(() -> get().join($EnclosingType$_.$attributeName$), query(), builder());
                }
                public $ValueType$Model.Path_ get$AttributeName$() {
                    return new $ValueType$Model.Path_(() ->get().get($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        } else {
            sb.append(Template.of("""
                public $CriteriaPathClass$ get$AttributeName$() {
                    return new $CriteriaPathClass$(() -> get().get($EnclosingType$_.$attributeName$), builder());
                }
            """).bind(map));
        }
    }


    @Override
    protected void collectionAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().getPersistenceType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_ join$AttributeName$() {
                    return new $ValueType$Model.Join_(() -> get().join($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Criteria.CollectionExp<$ValueType$, $AttributeJavaType$<$ValueType$>, Expression<$AttributeJavaType$<$ValueType$>>> get$AttributeName$() {
                return new Criteria.CollectionExp(() -> ((Root<$EnclosingType$>) get()).get($EnclosingType$_.$attributeName$), builder());
            }
        """).bind(map));
    }


    @Override
    protected void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().getPersistenceType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_ join$AttributeName$() {
                    return new $ValueType$Model.Join_(() -> get().join($EnclosingType$_.$attributeName$),query(), builder());
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Expression<Map<$KeyType$, $ValueType$>> get$AttributeName$() {
                return get().get($EnclosingType$_.$attributeName$);
            }
        """).bind(map));
    }

}
