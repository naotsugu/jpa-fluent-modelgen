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
 * The join model class generator.
 * @see AttributeClassGenerator
 * @author Naotsugu Kobayashi
 */
public class JoinModelClassGenerator extends AttributeClassGenerator {

    private JoinModelClassGenerator(ModelContext context, StaticMetamodelEntity entity, ImportBuilder imports) {
        super(context, entity, imports);
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @param imports the import sentences
     * @return Class writer
     */
    public static JoinModelClassGenerator of(ModelContext context, StaticMetamodelEntity entity, ImportBuilder imports) {
        return new JoinModelClassGenerator(context, entity, imports);
    }


    @Override
    protected Template classTemplate() {
        return Template.of("""
            public static class Join_ implements Supplier<Join<?, $EntityClass$>>, Criteria.AnyExpression<$EntityClass$, Join<?, $EntityClass$>> {
                private final Supplier<Join<?, $EntityClass$>> join;
                protected final AbstractQuery<?> query;
                protected final CriteriaBuilder builder;
                public Join_(Supplier<Join<?, $EntityClass$>> join, AbstractQuery<?> query, CriteriaBuilder builder) {
                    this.join = join;
                    this.query = query;
                    this.builder = builder;
                }
                @Override public Join<?, $EntityClass$> get() { return join.get(); }
                @Override public CriteriaBuilder builder() { return builder; }
                public AbstractQuery<?> query() { return query; }
                $AttributeMethods$
                $TreatMethods$
            }
            """);
    }


    @Override
    protected void singularAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().getPersistenceType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_ join$AttributeName$() {
                    return new $ValueType$Model.Join_(() -> get().join($EnclosingType$_.$attributeName$), query(), builder());
                }
                public $ValueType$Model.Path_ get$AttributeName$() {
                    return new $ValueType$Model.Path_(() -> get().get($EnclosingType$_.$attributeName$), query(), builder());
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
        } else {
            sb.append(Template.of("""
                public $AttributeJavaType$Join<T, $ValueType$> join$AttributeName$() {
                    return ((Join<?, $EnclosingType$>) get()).join($EnclosingType$_.$attributeName$);
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Criteria.CollectionExp<$ValueType$, $AttributeJavaType$<$ValueType$>, Expression<$AttributeJavaType$<$ValueType$>>> get$AttributeName$() {
                return new Criteria.CollectionExp(() -> get().get($EnclosingType$_.$attributeName$), builder());
            }
        """).bind(map));
    }


    @Override
    protected void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().getPersistenceType().isStruct()) {
            sb.append(Template.of("""
                public $EnclosingType$Map.Join_ join$AttributeName$() {
                    return new $EnclosingType$Map.Join_(() -> get().join($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        } else {
            sb.append(Template.of("""
                public MapJoin<T, $KeyType$, $ValueType$> join$AttributeName$() {
                    return get().join($EnclosingType$_.$attributeName$);
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Expression<Map<$KeyType$, $ValueType$>> get$AttributeName$() {
                return get().get($EnclosingType$_.$attributeName$);
            }
        """).bind(map));
    }


    @Override
    protected void treatMethods(Map<String, String> map, StringBuilder sb) {
        sb.append(Template.of("""
            public $DescendantEntityClass$Model.Join_ as$DescendantEntityClass$Model() {
                return new $DescendantEntityClass$Model.Join_(() -> builder().treat(get(), $DescendantEntityClass$.class), query(), builder());
            }
        """).bind(map));
    }

}
