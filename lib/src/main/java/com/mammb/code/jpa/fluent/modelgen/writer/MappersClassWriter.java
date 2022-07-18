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

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.JpaModelProcessor;
import com.mammb.code.jpa.fluent.modelgen.model.MappableType;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Mappers class writer.
 * @author Naotsugu Kobayashi
 */
public class MappersClassWriter {

    /** Context of processing. */
    private final Context context;

    /** MappableTypes. */
    private final List<MappableType> types;

    /** Import sentences. */
    private final ImportBuilder imports;


    /**
     * Constructor.
     * @param context the context of processing
     * @param types   the representation of static metamodel
     */
    protected MappersClassWriter(Context context, List<MappableType> types) {
        this.context = context;
        this.types = types;
        this.imports = ImportBuilder.of(
            PackageNames.createCommonPackageName(types.stream()
                .map(MappableType::getQualifiedName).toList()));
    }


    /**
     * Create a class writer instance.
     *
     * @param context the context of processing
     * @param types   the representation of mappable type
     * @return Root class factory writer
     */
    public static MappersClassWriter of(Context context, List<MappableType> types) {
        return new MappersClassWriter(context, types);
    }


    /**
     * Write a generated class file.
     */
    public void writeFile() {

        var className = "Mappers";
        var fqcn = imports.getSelfPackage() + "." + className;
        addDefaultImports();

        try (PrintWriter pw = new PrintWriter(
            context.getFiler().createSourceFile(fqcn).openOutputStream())) {

            pw.println(Template.of("""
                package $packageName$;

                $import$

                @Generated(value = "$GeneratorClass$")
                public abstract class $MapperClassName$ {

                    public static record IntegerResult(Integer value) { }
                    public static record LongResult(Long value) { }

                    public static <E, R extends RootAware<E>> Mapper<E, R, IntegerResult> integerResult(
                            Criteria.Selector<E, R, Integer> e1) {
                        return Mapper.construct(IntegerResult.class, Arrays.asList(Selector.of(e1)));
                    }
                    public static <E, R extends RootAware<E>> Mapper<E, R, LongResult> longResult(
                            Criteria.Selector<E, R, Long> e1) {
                        return Mapper.construct(LongResult.class, Arrays.asList(Selector.of(e1)));
                    }

                    $mapperMethods$
                }
                """).bind(
                "$packageName$", imports.getSelfPackage(),
                "$GeneratorClass$", JpaModelProcessor.class.getName(),
                "$MapperClassName$", className,
                "$mapperMethods$", mapperMethods(),
                "$import$", imports.generateImports(false)));

            pw.flush();
            context.addGenerated(fqcn);

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", fqcn, e.getMessage());
        }
    }


    private String mapperMethods() {
        var sb = new StringBuilder();
        types.forEach(type -> sb.append(mapperMethod(type)));
        return sb.toString();
    }


    private String mapperMethod(MappableType type) {
        return Template.of("""
            public static <E, R extends RootAware<E>> Mapper<E, R, $DtoClassName$> $dtoClassName$(
                    $MapperArgs$) {
                return Mapper.construct($DtoClassName$.class, Arrays.asList($SelectorArgs$));
            }
            """).bind(
            "$DtoClassName$", imports.add(type.getQualifiedName()),
            "$dtoClassName$", type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1),
            "$MapperArgs$", mapperArgs(type),
            "$SelectorArgs$", selectorArgs(type)).getIndentedValue(1);
    }


    private String mapperArgs(MappableType type) {
        var i = new AtomicInteger(1);
        return type.getConstructorArgTypeNames().stream()
            .map(name -> "Criteria.Selector<E, R, %s> e%d".formatted(imports.add(name), i.getAndIncrement()))
            .collect(Collectors.joining(", "));
    }


    private String selectorArgs(MappableType type) {
        var i = new AtomicInteger(1);
        return type.getConstructorArgTypeNames().stream()
            .map(name -> "Selector.of(e%d)".formatted(i.getAndIncrement()))
            .collect(Collectors.joining(", "));
    }


    private void addDefaultImports() {
        imports.add("java.util.Arrays");
        imports.add("javax.annotation.processing.Generated");
        imports.add(ApiClassWriter.PACKAGE_NAME + ".Criteria");
        imports.add(ApiClassWriter.PACKAGE_NAME + ".RootAware");
        imports.add("com.mammb.code.jpa.fluent.query.Mapper");
        imports.add("com.mammb.code.jpa.fluent.query.Selector");
    }

}
