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

import com.mammb.code.jpa.fluent.modelgen.JpaModelProcessor;
import com.mammb.code.jpa.fluent.modelgen.ModelContext;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import java.io.PrintWriter;

/**
 * The model class writer using the {@link javax.annotation.processing.Filer} API.
 * @author Naotsugu Kobayashi
 */
public class ModelClassWriter {

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
     */
    protected ModelClassWriter(ModelContext context, StaticMetamodelEntity entity) {
        this.context = context;
        this.entity = entity;
        this.imports = ImportBuilder.of(entity.getPackageName());
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @return Class writer
     */
    public static ModelClassWriter of(ModelContext context, StaticMetamodelEntity entity) {
        return new ModelClassWriter(context, entity);
    }


    /**
     * Write a generated class file.
     */
    public void writeFile() {
        context.logDebug("Create meta model : {}", entity.getQualifiedName());
        try {
            FileObject fo = context.getFiler().createSourceFile(
                entity.getTargetEntityQualifiedName() + "Model", entity.getElement());
            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                String body = generateBody();
                writePackageTo(pw);
                writeImportTo(pw);
                pw.println(body);
                pw.flush();
            }
        } catch (FilerException e) {
            context.logError("Problem with Filer: {}", e.getMessage());
        } catch (Exception e) {
            context.logError("Problem opening file to write Model for {} : {}", entity.getSimpleName(), e.getMessage());
        }
    }


    private void writePackageTo(PrintWriter pw) {
        if (imports.getSelfPackage().isEmpty()) {
            return;
        }
        pw.println("package " + imports.getSelfPackage() + ";");
        pw.println();
    }

    private void writeImportTo(PrintWriter pw) {
        imports.add("jakarta.persistence.criteria.CriteriaBuilder");
        imports.add("jakarta.persistence.criteria.CriteriaQuery");
        imports.add("jakarta.persistence.criteria.Subquery");
        imports.add("jakarta.persistence.criteria.AbstractQuery");
        imports.add("jakarta.persistence.criteria.Expression");
        imports.add("jakarta.persistence.criteria.Predicate");
        imports.add("jakarta.persistence.criteria.Root");
        imports.add("jakarta.persistence.criteria.Join");
        imports.add("jakarta.persistence.criteria.Path");
        imports.add("jakarta.persistence.criteria.ListJoin");
        imports.add("jakarta.persistence.criteria.SetJoin");
        imports.add("jakarta.persistence.criteria.MapJoin");
        imports.add("jakarta.persistence.criteria.CollectionJoin");
        imports.add("java.util.List");
        imports.add("java.util.Map");
        imports.add("java.util.Set");
        imports.add("java.util.Collection");
        imports.add("java.util.function.Supplier");
        imports.add("javax.annotation.processing.Generated");
        imports.add(ApiClassWriter.PACKAGE_NAME + ".*;");
        pw.println(imports.generateImports(context.isJakarta()));
        pw.println();
    }


    private String generateBody() {
        return Template.of("""
            @Generated(value = "$GeneratorClass$")
            public class $ClassName$Model {

                public static Root_ root(Root<$ClassName$> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    return new Root_(root, query, builder);
                }
                public static RootSource<$ClassName$, Root_> root() {
                    return new RootSource<$ClassName$, Root_>() {
                        @Override public Root_ root(Root<$ClassName$> source, AbstractQuery<?> query, CriteriaBuilder builder) {
                            return new Root_(source, query, builder);
                        }
                        @Override public Class<$ClassName$> rootClass() { return $ClassName$.class; }
                    };
                }
                $RootClass$

                $JoinClass$

                $PathClass$
            }
            """).bind(
            "$GeneratorClass$", JpaModelProcessor.class.getName(),
            "$ClassName$", entity.getTargetEntityName(),
            "$RootClass$", RootModelClassGenerator.of(context, entity, imports).generate(),
            "$JoinClass$", JoinModelClassGenerator.of(context, entity, imports).generate(),
            "$PathClass$", PathModelClassGenerator.of(context, entity, imports).generate()).getValue();
    }

}
