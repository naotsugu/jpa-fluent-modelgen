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

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * Helper to write the actual enhanced metamodel class
 * using the {@link javax.annotation.processing.Filer} API.
 *
 * @author Naotsugu Kobayashi
 */
public class ClassWriter {

    /** Context of processing. */
    private final Context context;

    /** Representation of static metamodel. */
    private final StaticMetamodelEntity entity;

    /** Import sentences. */
    private final ImportSentences imports;


    /**
     * Constructor.
     * @param context the context of processing
     * @param entity the representation of static metamodel
     */
    protected ClassWriter(Context context, StaticMetamodelEntity entity) {
        this.context = context;
        this.entity = entity;
        this.imports = ImportSentences.of(entity.getPackageName());
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @return Class writer
     */
    public static ClassWriter of(Context context, StaticMetamodelEntity entity) {
        return new ClassWriter(context, entity);
    }


    /**
     * Write a generated class file.
     */
    public void writeFile() {
        try {

            String body = generateBody().toString();

            FileObject fo = context.getFiler().createSourceFile(
                entity.getQualifiedName() + "Root_", entity.getElement());

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                if (!entity.getPackageName().isEmpty()) {
                    pw.println("package " + entity.getPackageName() + ";");
                    pw.println();
                }
                pw.println(imports.generateImports(context.isJakarta()));
                if (context.isAddCriteria()) {
                    pw.println("import " + CriteriaClassWriter.PACKAGE + ".*;");
                }
                pw.println();

                pw.println(body);
                pw.flush();
            }

        } catch (FilerException e) {
            context.logError("Problem with Filer: " + e.getMessage());
        } catch (Exception e) {
            context.logError("Problem opening file to write MetaModel for " +
                entity.getSimpleName() + ": " + e.getMessage());
        }
    }


    /**
     * Generate the body of class.
     * @return the body of class
     */
    protected StringBuilder generateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append(generateClassDeclaration()).append(System.lineSeparator());
        return sb;
    }


    /**
     * Generate the class declaration.
     * @return the class declaration
     */
    protected String generateClassDeclaration() {
        StringBuilder sb = new StringBuilder();
        if (Objects.isNull(entity.getSuperClass())) {
            sb.append("""
                @SuppressWarnings("unchecked")
                @Generated(value = "%1$s")
                public class %2$sRoot_<T extends %4$s> implements Supplier<Root<T>>%5$s {

                    protected final Root<T> root;
                    protected final CriteriaQuery<?> query;
                    protected final CriteriaBuilder builder;
                    
                    public %2$sRoot_(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                        this.root = root;
                        this.query = query;
                        this.builder = builder;
                    }
                        
                    public %2$sRoot_(Root<T> root) { this(root, null, null); }

                    @Override
                    public Root<T> get() { return root; }
                    
                """.formatted(
                    JpaMetaModelEnhanceProcessor.class.getName(), // %1$s
                    entity.getSimpleName(),                       // %2$s
                    entity.getSuperClass(),                       // %3$s
                    entity.getTargetEntityName(),                 // %4$s
                    criteriaRootImplements(entity, true)          // %5$s
            ));
            if (context.isAddCriteria()) {
                sb.append("""
                        public CriteriaBuilder builder() { return builder; }
                        
                    """);
            }
        } else {
            sb.append("""
                @SuppressWarnings("unchecked")
                @Generated(value = "%1$s")
                public class %2$sRoot_<T extends %4$s> extends %3$sRoot_<T>%5$s {
                
                    @Override
                    public Root<T> get() {
                        return root;
                    }

                    public %2$sRoot_(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                        super(root, query, builder);
                    }

                    public %2$sRoot_(Root<T> root) {
                        super(root, null, null);
                    }

                """.formatted(
                    JpaMetaModelEnhanceProcessor.class.getName(), // %1$s
                    entity.getSimpleName(),                       // %2$s
                    entity.getSuperClass(),                       // %3$s
                    entity.getTargetEntityName(),                 // %4$s
                    criteriaRootImplements(entity, false)         // %5$s
            ));
        }

        entity.getAttributes().forEach(attr -> sb.append(generateRootMethod(attr)));

        sb.append('\n');
        sb.append(generateJoinClass()).append('\n');
        sb.append(generatePathClass()).append('\n');

        sb.append("}");
        return sb.toString();
    }


    /**
     * Generate root methods.
     * @param attribute the static metamodel attribute
     * @return root methods
     */
    protected String generateRootMethod(StaticMetamodelAttribute attribute) {

        if (attribute.getAttributeType().isList()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                    public %2$s_Root_.Join_<%2$s> join%3$s() {
                        return new %2$s_Root_.Join_<>(query, builder) {
                            @Override
                            public ListJoin<? extends %1$s, %2$s> get() {
                                return %1$s_Root_.this.get().join(%1$s_.%4$s);
                            }
                        };
                    }

                    public Expression<List<%2$s>> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            } else {
                return bindAttribute(attribute, """
                    public Expression<List<%2$s>> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            }

        } else if (attribute.getAttributeType().isSet()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                    public %2$s_Root_.Join_<%2$s> join%3$s() {
                        return new %2$s_Root_.Join_<>(query, builder) {
                            @Override
                            public SetJoin<? extends %1$s, %2$s> get() {
                                return %1$s_Root_.this.get().join(%1$s_.%4$s);
                            }
                        };
                    }

                    public Expression<Set<%2$s>> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            } else {
                return bindAttribute(attribute, """
                    public Expression<Set<%2$s>> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            }
        } else if (attribute.getAttributeType().isCollection()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                    public %2$s_Root_.Join_<%2$s> join%3$s() {
                        return new %2$s_Root_.Join_<>(query, builder) {
                            @Override
                            public CollectionJoin<? extends %1$s, %2$s> get() {
                                return %1$s_Root_.this.get().join(%1$s_.%4$s);
                            }
                        };
                    }

                    public Expression<Collection<%2$s>> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            } else {
                return bindAttribute(attribute, """
                    public Expression<Collection<%2$s>> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            }

        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                    public %2$s_Root_.Join_<%2$s> join%3$s() {
                        return new %2$s_Root_.Join_<>(query, builder) {
                            @Override
                            public Join<? extends %1$s, %2$s> get() {
                                return %1$s_Root_.this.get().join(%1$s_.%4$s);
                            }
                        };
                    }

                    public %2$s_Root_.Path_<%2$s> get%3$s() {
                        return new %2$s_Root_.Path_<>(query, builder) {
                            @Override
                            public Path<%2$s> get() {
                                return %1$s_Root_.this.get().get(%1$s_.%4$s);
                            }
                        };
                    }

                """);
            } else {
                if (context.isAddCriteria()) {
                    return bindAttribute(attribute, """
                            public PATH_CLASSNAME get%3$s() {
                                return new PATH_CLASSNAME(() -> %1$s_Root_.this.get().get(%1$s_.%4$s), builder);
                            }
        
                        """.replace("PATH_CLASSNAME", criteriaPathClassName(attribute)));
                } else {
                    return bindAttribute(attribute, """
                        public Path<%2$s> get%3$s() {
                            return %1$s_Root_.this.get().get(%1$s_.%4$s);
                        }
    
                    """);
                }
            }
        } else if (attribute.getAttributeType().isMap()) {
            if (attribute.getValueType().isStruct()) {
                return bindMapAttribute(attribute, """
                    public %3$s_Root_.Join_<%3$s> join%4$s() {
                        return new %3$s_Root_.Join_<>(query, builder) {
                            @Override
                            public MapJoin<? extends %1$s, %2$s, %3$s> get() {
                                return %1$s_Root_.this.get().join(%1$s_.%5$s);
                            }
                        };
                    }

                    public Expression<Map<%2$s, %3$s>> get%4$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%5$s);
                    }

                """);
            } else {
                return bindMapAttribute(attribute, """
                    public Expression<Map<%2$s, %3$s>> get%4$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%5$s);
                    }

                """);
            }
        } else {
            return "";
        }
    }


    /**
     * Generate the join class.
     * @return generated join class
     */
    protected String generateJoinClass() {

        StringBuilder sb = new StringBuilder();

        if (Objects.isNull(entity.getSuperClass())) {
            sb.append("""
                        public static abstract class Join_<T extends %1$s> implements Supplier<Join<?, T>>%3$s {
                            
                            protected final CriteriaQuery<?> query;
                            protected final CriteriaBuilder builder;
                            
                            public Join_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                                this.query = query;
                                this.builder = builder;
                            }
                                         
                            @Override
                            public abstract Join<?, T> get();
                            
                    """.formatted(
                    entity.getTargetEntityName(),        // %1$s
                    entity.getSuperClass(),              // %2$s
                    criteriaJoinImplements(entity, true) // %3$s
            ));
            if (context.isAddCriteria()) {
                sb.append("""
                            public CriteriaBuilder builder() { return builder; }
                            
                    """);
            }
        } else {
            sb.append("""
                    public static abstract class Join_<T extends %1$s> extends %2$sRoot_.Join_<T>%3$s {
                    
                        public Join_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            super(query, builder);
                        }

                        @Override
                        public abstract Join<?, T> get();

                """.formatted(
                    entity.getTargetEntityName(),         // %1$s
                    entity.getSuperClass(),               // %2$s
                    criteriaJoinImplements(entity, false) // %3$s
            ));
        }
        entity.getAttributes().forEach(attr -> sb.append(generateJoinMethod(attr)));

        sb.append("    }").append(System.lineSeparator());
        return sb.toString();
    }


    /**
     * Generate join methods
     * @param attribute attribute
     * @return Generated join methods
     */
    protected String generateJoinMethod(StaticMetamodelAttribute attribute) {

        if (attribute.getAttributeType().isList()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_<%2$s> join%3$s() {
                            return new %2$s_Root_.Join_<>(query, builder) {
                                @Override
                                public ListJoin<T, %2$s> get() {
                                    return %1$s_Root_.Join_.this.get().join(%1$s_.%4$s);
                                }
                            };
                        }

                        public Expression<List<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public ListJoin<T, %2$s> join%3$s() {
                            return get().join(%1$s_.%4$s);
                        }

                        public Expression<List<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            }
        } else if (attribute.getAttributeType().isSet()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_<%2$s> join%3$s() {
                            return new %2$s_Root_.Join_<>(query, builder) {
                                @Override
                                public SetJoin<T, %2$s> get() {
                                    return %1$s_Root_.Join_.this.get().join(%1$s_.%4$s);
                                }
                            };
                        }

                        public Expression<Set<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public SetJoin<T, %2$s> join%3$s() {
                            return get().join(%1$s_.%4$s);
                        }

                        public Expression<Set<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            }
        } else if (attribute.getAttributeType().isCollection()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_<%2$s> join%3$s() {
                            return new %2$s_Root_.Join_<>(query, builder) {
                                @Override
                                public CollectionJoin<T, %2$s> get() {
                                    return %1$s_Root_.Join_.this.get().join(%1$s_.%4$s);
                                }
                            };
                        }

                        public Expression<Collection<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public CollectionJoin<T, %2$s> join%3$s() {
                            return get().join(%1$s_.%4$s);
                        }
                        public Expression<Collection<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }
                """);
            }
        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_<%2$s> join%3$s() {
                            return new %2$s_Root_.Join_<>(query, builder) {
                                @Override
                                public Join<T, %2$s> get() {
                                    return %1$s_Root_.Join_.this.get().join(%1$s_.%4$s);
                                }
                            };
                        }

                        public %2$s_Root_.Path_<%2$s> get%3$s() {
                            return new %2$s_Root_.Path_<>(query, builder) {
                                @Override
                                public Path<%2$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).get(%1$s_.%4$s);
                                }
                            };
                        }

                """);
            } else {
                if (context.isAddCriteria()) {
                    return bindAttribute(attribute, """
                            public PATH_CLASSNAME get%3$s() {
                                return new PATH_CLASSNAME(() -> ((Join<?, %1$s>) get()).get(%1$s_.%4$s), builder);
                            }
                                    
                    """.replace("PATH_CLASSNAME", criteriaPathClassName(attribute)));
                } else {
                    return bindAttribute(attribute, """
                            public Path<%2$s> get%3$s() {
                                return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                            }
                            
                    """);
                }
            }
        } else if (attribute.getAttributeType().isMap()) {
            if (attribute.getValueType().isStruct()) {
                return bindMapAttribute(attribute, """
                        public %3$s_Root_.Join_<%3$s> join%4$s() {
                            return new %3$s_Root_.Join_<>(query, builder) {
                                @Override
                                public MapJoin<T, %2$s, %3$s> get() {
                                    return %1$s_Root_.Join_.this.get().join(%1$s_.%5$s);
                                }
                            };
                        }

                        public Expression<Map<%2$s, %3$s>> get%4$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%5$s);
                        }

                """);
            } else {
                return bindMapAttribute(attribute, """
                        public MapJoin<T, %2$s, %3$s> join%4$s() {
                            return get().join(%1$s_.%5$s);
                        }

                        public Expression<Map<%2$s, %3$s>> get%4$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%5$s);
                        }

                """);
            }
        } else {
            return "";
        }
    }


    /**
     * Generate path class.
     * @return Generated path class
     */
    protected String generatePathClass() {
        StringBuilder sb = new StringBuilder();

        if (Objects.isNull(entity.getSuperClass())) {
            sb.append("""
                    public static abstract class Path_<T extends %1$s> implements Supplier<Path<T>>%3$s {
                    
                        protected final CriteriaQuery<?> query;
                        protected final CriteriaBuilder builder;
                
                        public Path_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            this.query = query;
                            this.builder = builder;
                        }

                        @Override
                        public abstract Path<T> get();
                        
                """.formatted(
                    entity.getTargetEntityName(),          // %1$s
                    entity.getSuperClass(),                // %2$s
                    criteriaPathImplements(entity, true)   // %3$s
            ));
            if (context.isAddCriteria()) {
                sb.append("""
                            public CriteriaBuilder builder() { return builder; }
                            
                    """);
            }
        } else {
            sb.append("""
                    public static abstract class Path_<T extends %1$s> extends %2$sRoot_.Path_<T>%3$s {
                    
                        public Path_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            super(query, builder);
                        }

                        @Override
                        public abstract Path<T> get();

                """.formatted(
                    entity.getTargetEntityName(),         // %1$s
                    entity.getSuperClass(),               // %2$s
                    criteriaPathImplements(entity, false) // %3$s
            ));
        }
        entity.getAttributes().forEach(attr -> sb.append(generatePathMethod(attr)));

        sb.append("    }").append('\n');
        return sb.toString();
    }


    /**
     * Generate path methods.
     * @param attribute attribute
     * @return Generated path methods
     */
    protected String generatePathMethod(StaticMetamodelAttribute attribute) {
        if (attribute.getAttributeType().isList()) {
            return bindAttribute(attribute, """
                    public Expression<List<%2$s>> get%3$s() {
                        return ((Path<%1$s>) get()).get(%1$s_.%4$s);
                    }

            """);
        } else if (attribute.getAttributeType().isSet()) {
            return bindAttribute(attribute, """
                    public Expression<Set<%2$s>> get%3$s() {
                        return ((Path<%1$s>) get()).get(%1$s_.%4$s);
                    }

            """);
        } else if (attribute.getAttributeType().isCollection()) {
            return bindAttribute(attribute, """
                    public Expression<Collection<%2$s>> get%3$s() {
                        return ((Path<%1$s>) get()).get(%1$s_.%4$s);
                    }

            """);
        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Path_<%2$s> get%3$s() {
                            return new %2$s_Root_.Path_<>(query, builder) {
                                @Override
                                public Path<%2$s> get() {
                                    return %1$s_Root_.Path_.this.get().get(%1$s_.%4$s);
                                }
                            };
                        }

                """);
            } else {
                if (context.isAddCriteria()) {
                    return bindAttribute(attribute, """
                            public PATH_CLASSNAME get%3$s() {
                                return new PATH_CLASSNAME(() -> ((Path<%1$s>) get()).get(%1$s_.%4$s), builder);
                            }
        
                    """.replace("PATH_CLASSNAME", criteriaPathClassName(attribute)));
                } else {
                    return bindAttribute(attribute, """
                            public Path<%2$s> get%3$s() {
                                return ((Path<%1$s>) get()).get(%1$s_.%4$s);
                            }

                    """);
                }
            }
        } else if (attribute.getAttributeType().isMap()) {
            return bindMapAttribute(attribute, """
                    public Expression<Map<%2$s, %3$s>> get%4$s() {
                        return ((Path<%1$s>) get()).get(%1$s_.%5$s);
                    }

            """);
        } else {
            return "";
        }
    }


    /**
     * Bind attribute to template.
     * @param attribute attribute
     * @param template template
     * @return Binded template
     */
    protected String bindAttribute(StaticMetamodelAttribute attribute, String template) {
        if (attribute.getAttributeType().isMap()) {
            throw new IllegalArgumentException(attribute.getAttributeType().toString());
        }
        return template.formatted(
            imports.apply(attribute.getEnclosingType().getName()), // %1$s
            imports.apply(attribute.getValueType().getName()),     // %2$s
            capitalize(attribute.getName()),                       // %3$s
            attribute.getName()                                    // %4$s
        );
    }


    /**
     * Bind map attribute to template.
     * @param attribute attribute
     * @param template template
     * @return Binded template
     */
    protected String bindMapAttribute(StaticMetamodelAttribute attribute, String template) {
        if (!attribute.getAttributeType().isMap()) {
            throw new IllegalArgumentException(attribute.getAttributeType().toString());
        }
        return template.formatted(
            imports.apply(attribute.getEnclosingType().getName()), // %1$s
            imports.apply(attribute.getKeyType().getName()),       // %2$s
            imports.apply(attribute.getValueType().getName()),     // %3$s
            capitalize(attribute.getName()),                       // %4$s
            attribute.getName()                                    // %5$s
        );
    }


    /**
     * Get the criteria implements clause.
     * @param entity the static metamodel entity
     * @param append append implements?
     * @return the criteria implements clause
     */
    protected String criteriaRootImplements(StaticMetamodelEntity entity, boolean append) {
        if (context.isAddCriteria()) {
            if (entity.getTargetEntity().isComparable()) {
                return (append ? ", " : " implements ") + "Criteria_.ComparableExpression<T, Root<T>>";
            } else {
                return (append ? ", " : " implements ") + "Criteria_.AnyExpression<T, Root<T>>";
            }
        } else {
            return "";
        }
    }


    /**
     * Get the criteria implements clause.
     * @param entity the static metamodel entity
     * @param append append implements?
     * @return the criteria implements clause
     */
    protected String criteriaJoinImplements(StaticMetamodelEntity entity, boolean append) {
        return criteriaRootImplements(entity, append).replace("Root<T>", "Join<?, T>");
    }


    /**
     * Get the criteria implements clause.
     * @param entity the static metamodel entity
     * @param append append implements?
     * @return the criteria implements clause
     */
    protected String criteriaPathImplements(StaticMetamodelEntity entity, boolean append) {
        return criteriaRootImplements(entity, append).replace("Root", "Path");
    }


    /**
     * Get the criteria path class name from given attribute.
     * @param attribute the attribute
     * @return the criteria path class name
     */
    protected static String criteriaPathClassName(StaticMetamodelAttribute attribute) {
        if (attribute.getValueType().isString()) {
            return "Criteria_.StringPath_";
        } else if (attribute.getValueType().isBoolean()) {
            return "Criteria_.BooleanPath_";
        } else if (attribute.getValueType().isNumber()) {
            return "Criteria_.NumberPath_";
        } else if (attribute.getValueType().isComparable()) {
            return "Criteria_.ComparablePath_";
        } else {
            return "Criteria_.AnyPath_";
        }
    }


    /**
     * Capitalize the given string.
     * @param str the given string
     * @return Capitalized string
     */
    protected static String capitalize(String str) {
        return (Objects.isNull(str) || str.isEmpty())
            ? str
            : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
