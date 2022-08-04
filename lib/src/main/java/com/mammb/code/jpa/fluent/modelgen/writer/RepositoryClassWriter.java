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
import com.mammb.code.jpa.fluent.modelgen.context.ModelContext;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import java.io.PrintWriter;
import java.util.Objects;

import static java.util.function.Predicate.not;

/**
 * The repository class writer.
 * @author Naotsugu Kobayashi
 */
public class RepositoryClassWriter {

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
    protected RepositoryClassWriter(ModelContext context, StaticMetamodelEntity entity) {
        this.context = context;
        this.entity = entity;
        this.imports = ImportBuilder.of(entity.getPackageName());
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the representation of static metamodel
     * @return Root class factory writer
     */
    public static RepositoryClassWriter of(ModelContext context, StaticMetamodelEntity entity) {
        return new RepositoryClassWriter(context, entity);
    }


    /**
     * Write a generated class file.
     */
    public void writeFile() {

        context.logDebug("Create repository : {}", entity.getQualifiedName());

        try {

            FileObject fo = context.getFiler().createSourceFile(
                entity.getTargetEntityQualifiedName() + "Repository_", entity.getElement());

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                var extendsClause = String.join(", ", context.getRepositoryTraitTypes().stream()
                    .map(trait -> trait.createExtendsClause(entity, imports))
                    .filter(not(String::isEmpty))
                    .toArray(String[]::new));

                var body = """
                    public interface %2$sRepository_ extends Repository<%1$s, %2$s, %2$sModel.Root_>{extends} {
                        default RootSource<%2$s, %2$sModel.Root_> rootSource() {
                            return %2$sModel.root();
                        }
                    }
                    """
                    .replace("{extends}", extendsClause.isEmpty() ? "" : (", " + extendsClause))
                    .formatted(
                        imports.add(entity.getEntityIdType().get().toString()),  // %1$s
                        imports.add(entity.getTargetEntityQualifiedName()),      // %2$s
                        unCapitalize(entity.getTargetEntityName())               // %3$s
                    );

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add(ApiClassWriter.PACKAGE_NAME + ".*");
                imports.add(entity.getTargetEntityQualifiedName() + "Model");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
                pw.println(body);
                pw.flush();
            }

        } catch (FilerException e) {
            context.logError("Problem with Filer : {}", e.getMessage());
        } catch (Exception e) {
            context.logError("Problem opening file to write Repository for {} : {}", entity.getSimpleName(), e.getMessage());
        }
    }

    /**
     * UnCapitalize the given string.
     * @param str the given string
     * @return Capitalized string
     */
    protected static String unCapitalize(String str) {
        return (Objects.isNull(str) || str.isEmpty())
            ? str
            : str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
