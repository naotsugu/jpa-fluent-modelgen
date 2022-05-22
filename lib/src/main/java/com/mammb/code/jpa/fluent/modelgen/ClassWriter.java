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
    private final ImportSentences importes;


    /**
     * Constructor.
     * @param context the context of processing
     * @param entity the representation of static metamodel
     */
    protected ClassWriter(Context context, StaticMetamodelEntity entity) {
        this.context = context;
        this.entity = entity;
        this.importes = ImportSentences.of(entity.getPackageName());
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
                pw.println(importes.generateImports(context.isJakarta()));
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
                public class %2$sRoot_ implements Supplier<Root<? extends %3$s>> {

                    private final Root<? extends %3$s> root;
                    public %2$sRoot_(Root<? extends %3$s> root) {
                        this.root = root;
                    }

                    @Override
                    public Root<? extends %3$s> get() {
                        return root;
                    }

                """.formatted(
                    JpaMetaModelEnhanceProcessor.class.getName(), // %1$s
                    entity.getSimpleName(),                       // %2$s
                    entity.getTargetEntityName()                  // %3$s
            ));
        } else {
            sb.append("""
                @SuppressWarnings("unchecked")
                @Generated(value = "%1$s")
                public class %2$sRoot_ extends %3$sRoot_ {

                    public %2$sRoot_(Root<? extends %4$s> root) {
                        super(root);
                    }

                """.formatted(
                    JpaMetaModelEnhanceProcessor.class.getName(), // %1$s
                    entity.getSimpleName(),                       // %2$s
                    entity.getSuperClass(),                       // %3$s
                    entity.getTargetEntityName()                  // %4$s
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
                    public %2$s_Root_.Join_ join%3$s() {
                        return new %2$s_Root_.Join_() {
                            @Override
                            public ListJoin<%1$s, %2$s> get() {
                                return ((Root<%1$s>) %1$s_Root_.this.get()).join(%1$s_.%4$s);
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
                    public %2$s_Root_.Join_ join%3$s() {
                        return new %2$s_Root_.Join_() {
                            @Override
                            public SetJoin<%1$s, %2$s> get() {
                                return ((Root<%1$s>) %1$s_Root_.this.get()).join(%1$s_.%4$s);
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
                    public %2$s_Root_.Join_ join%3$s() {
                        return new %2$s_Root_.Join_() {
                            @Override
                            public CollectionJoin<%1$s, %2$s> get() {
                                return ((Root<%1$s>) %1$s_Root_.this.get()).join(%1$s_.%4$s);
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
                    public %2$s_Root_.Join_ join%3$s() {
                        return new %2$s_Root_.Join_() {
                            @Override
                            public Join<%1$s, %2$s> get() {
                                return ((Root<%1$s>) %1$s_Root_.this.get()).join(%1$s_.%4$s);
                            }
                        };
                    }

                    public %2$s_Root_.Path_ get%3$s() {
                        return new %2$s_Root_.Path_() {
                            @Override
                            public Path<%2$s> get() {
                                return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                            }
                        };
                    }

                """);
            }  else {
                return bindAttribute(attribute, """
                    public Path<%2$s> get%3$s() {
                        return ((Root<%1$s>) %1$s_Root_.this.get()).get(%1$s_.%4$s);
                    }

                """);
            }
        } else if (attribute.getAttributeType().isMap()) {
            if (attribute.getValueType().isStruct()) {
                return bindMapAttribute(attribute, """
                    public %3$s_Root_.Join_ join%4$s() {
                        return new %3$s_Root_.Join_() {
                            @Override
                            public MapJoin<%1$s, %2$s, %3$s> get() {
                                return ((Root<%1$s>) %1$s_Root_.this.get()).join(%1$s_.%5$s);
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
                    public static abstract class Join_ implements Supplier<Join<?, ? extends %1$s>> {

                        @Override
                        public abstract Join<?, ? extends %1$s> get();

                """.formatted(
                    entity.getTargetEntityName()  // %1$s
            ));
        } else {
            sb.append("""
                    public static abstract class Join_ extends %2$sRoot_.Join_ {

                        @Override
                        public abstract Join<?, ? extends %1$s> get();

                """.formatted(
                    entity.getTargetEntityName(), // %1$s
                    entity.getSuperClass()        // %2$s
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
                        public %2$s_Root_.Join_ join%3$s() {
                            return new %2$s_Root_.Join_() {
                                @Override
                                public ListJoin<%1$s, %2$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).join(%1$s_.%4$s);
                                }
                            };
                        }

                        public Expression<List<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public ListJoin<%1$s, %2$s> join%3$s() {
                            return ((Join<?, %1$s>) get()).join(%1$s_.%4$s);
                        }

                        public Expression<List<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            }
        } else if (attribute.getAttributeType().isSet()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_ join%3$s() {
                            return new %2$s_Root_.Join_() {
                                @Override
                                public SetJoin<%1$s, %2$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).join(%1$s_.%4$s);
                                }
                            };
                        }

                        public Expression<Set<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public SetJoin<%1$s, %2$s> join%3$s() {
                            return ((Join<?, %1$s>) get()).join(%1$s_.%4$s);
                        }

                        public Expression<Set<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            }
        } else if (attribute.getAttributeType().isCollection()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_ join%3$s() {
                            return new %2$s_Root_.Join_() {
                                @Override
                                public CollectionJoin<%1$s, %2$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).join(%1$s_.%4$s);
                                }
                            };
                        }

                        public Expression<Collection<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public CollectionJoin<%1$s, %2$s> join%3$s() {
                            return ((Join<?, %1$s>) get()).join(%1$s_.%4$s);
                        }
                        public Expression<Collection<%2$s>> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }
                """);
            }
        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                return bindAttribute(attribute, """
                        public %2$s_Root_.Join_ join%3$s() {
                            return new %2$s_Root_.Join_() {
                                @Override
                                public Join<%1$s, %2$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).join(%1$s_.%4$s);
                                }
                            };
                        }

                        public %2$s_Root_.Path_ get%3$s() {
                            return new %2$s_Root_.Path_() {
                                @Override
                                public Path<%2$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).get(%1$s_.%4$s);
                                }
                            };
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public Path<%2$s> get%3$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%4$s);
                        }
                """);
            }
        } else if (attribute.getAttributeType().isMap()) {
            if (attribute.getValueType().isStruct()) {
                return bindMapAttribute(attribute, """
                        public %3$s_Root_.Join_ join%4$s() {
                            return new %3$s_Root_.Join_() {
                                @Override
                                public MapJoin<%1$s, %2$s, %3$s> get() {
                                    return ((Join<?, %1$s>) %1$s_Root_.Join_.this.get()).join(%1$s_.%5$s);
                                }
                            };
                        }

                        public Expression<Map<%2$s, %3$s>> get%4$s() {
                            return ((Join<?, %1$s>) get()).get(%1$s_.%5$s);
                        }

                """);
            } else {
                return bindMapAttribute(attribute, """
                        public MapJoin<%1$s, %2$s, %3$s> join%4$s() {
                            return ((Join<?, %1$s>) get()).join(%1$s_.%5$s);
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
                    public static abstract class Path_ implements Supplier<Path<? extends %1$s>> {

                        @Override
                        public abstract Path<? extends %1$s> get();

                """.formatted(
                    entity.getTargetEntityName()  // %1$s
            ));
        } else {
            sb.append("""
                    public static abstract class Path_ extends %2$sRoot_.Path_ {

                        @Override
                        public abstract Path<? extends %1$s> get();

                """.formatted(
                    entity.getTargetEntityName(), // %1$s
                    entity.getSuperClass()        // %2$s
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
                        public %2$s_Root_.Path_ get%3$s() {
                            return new %2$s_Root_.Path_() {
                                @Override
                                public Path<%2$s> get() {
                                    return ((Path<%1$s>) %1$s_Root_.Path_.this.get()).get(%1$s_.%4$s);
                                }
                            };
                        }

                """);
            } else {
                return bindAttribute(attribute, """
                        public Path<%2$s> get%3$s() {
                            return ((Path<%1$s>) get()).get(%1$s_.%4$s);
                        }

                """);
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
            importes.apply(attribute.getEnclosingType().getName()), // %1$s
            importes.apply(attribute.getValueType().getName()),     // %2$s
            capitalize(attribute.getName()),                        // %3$s
            attribute.getName()                                     // %4$s
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
            importes.apply(attribute.getEnclosingType().getName()), // %1$s
            importes.apply(attribute.getKeyType().getName()),       // %2$s
            importes.apply(attribute.getValueType().getName()),     // %3$s
            capitalize(attribute.getName()),                        // %4$s
            attribute.getName()                                     // %5$s
        );
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
