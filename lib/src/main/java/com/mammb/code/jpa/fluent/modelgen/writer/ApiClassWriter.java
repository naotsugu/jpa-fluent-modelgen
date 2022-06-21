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
import com.mammb.code.jpa.fluent.modelgen.JpaMetaModelEnhanceProcessor;

import javax.tools.FileObject;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * Helper to write the criteria class factory class
 * using the {@link javax.annotation.processing.Filer} API.
 *
 * @author Naotsugu Kobayashi
 */
public class ApiClassWriter {

    /** The name of package. */
    public static final String PACKAGE_NAME = "com.mammb.code.jpa.core";

    /** The name of RootSource class. */
    public static final String ROOT_SOURCE = "RootSource";
    /** The name of BuilderAware class. */
    public static final String BUILDER_AWARE = "BuilderAware";
    /** The name of RootAware class. */
    public static final String ROOT_AWARE = "RootAware";
    /** The name of Criteria class. */
    public static final String CRITERIA = "Criteria";
    /** The name of Repository class. */
    public static final String REPOSITORY = "Repository";

    /** Context of processing. */
    private final Context context;


    /**
     * Constructor.
     * @param context the context of processing
     */
    protected ApiClassWriter(Context context) {
        this.context = context;
    }


    /**
     * Create a criteria writer instance.
     * @param context the context of processing
     * @return the criteria class writer
     */
    public static ApiClassWriter of(Context context) {
        return new ApiClassWriter(context);
    }


    /**
     * Write api classes.
     */
    public void writeClasses() {
        context.logDebug("Create api class");
        writeRootSourceClass();
        writeBuilderClass();
        writeRootAwareClass();
        writeCriteriaClass();
        writeRepositoryClass();
    }


    /**
     * Write a RootSource class file.
     */
    private void writeRootSourceClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + ROOT_SOURCE))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + "." + ROOT_SOURCE);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add("jakarta.persistence.criteria.CriteriaBuilder");
                imports.add("jakarta.persistence.criteria.CriteriaQuery");
                imports.add("jakarta.persistence.criteria.Root");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaMetaModelEnhanceProcessor.class.getName()));
                pw.println("""
                public interface %1$s<E, T extends java.util.function.Supplier<Root<E>>> {
                    T root(CriteriaQuery<?> query, CriteriaBuilder builder);
                    Class<E> rootClass();
                }
                """.formatted(ROOT_SOURCE));

                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", ROOT_SOURCE, e.getMessage());
        }

    }


    /**
     * Write a criteria class file.
     */
    private void writeBuilderClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + BUILDER_AWARE))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(PACKAGE_NAME + "." + BUILDER_AWARE);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add("jakarta.persistence.criteria.CriteriaBuilder");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaMetaModelEnhanceProcessor.class.getName()));
                pw.println("""
                public interface %1$s {
                    CriteriaBuilder builder();
                }
                """.formatted(BUILDER_AWARE));
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", BUILDER_AWARE, e.getMessage());
        }

    }


    /**
     * Write a criteria class file.
     */
    private void writeRootAwareClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + ROOT_AWARE))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + "." + ROOT_AWARE);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add("jakarta.persistence.criteria.Root");
                imports.add("java.util.function.Supplier");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaMetaModelEnhanceProcessor.class.getName()));
                pw.println("""
                    public interface %1$s<E> extends Supplier<Root<E>>, %2$s {
                    }
                    """.formatted(ROOT_AWARE, BUILDER_AWARE));
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", ROOT_AWARE, e.getMessage());
        }

    }


    /**
     * Write a criteria class file.
     */
    private void writeCriteriaClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + CRITERIA))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + "." + CRITERIA);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add("jakarta.persistence.criteria.CriteriaBuilder");
                imports.add("jakarta.persistence.criteria.Expression");
                imports.add("jakarta.persistence.criteria.Order");
                imports.add("jakarta.persistence.criteria.Path");
                imports.add("jakarta.persistence.criteria.Predicate");
                imports.add("java.util.Collection");
                imports.add("java.util.Objects");
                imports.add("java.util.function.Supplier");
                imports.add("java.util.regex.Pattern");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaMetaModelEnhanceProcessor.class.getName()));
                pw.println("""
                    public class %1$s {

                        public static class AnyPath<E> implements AnyExpression<E, Path<E>>, %2$s {
                            private final Supplier<Path<E>> path;
                            private final CriteriaBuilder builder;
                            public AnyPath(Supplier<Path<E>> path, CriteriaBuilder builder) {
                                this.path = path;
                                this.builder = builder;
                            }
                            @Override public Path<E> get() { return path.get(); }
                            @Override public CriteriaBuilder builder() { return builder; }
                        }

                        public static class AnyExp<E> implements AnyExpression<E, Expression<E>>, %2$s {
                            private final Supplier<Expression<E>> expression;
                            private final CriteriaBuilder builder;
                            public AnyExp(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
                                this.expression = expression;
                                this.builder = builder;
                            }
                            @Override public Expression<E> get() { return expression.get(); };
                            @Override public CriteriaBuilder builder() { return builder; }
                        }

                        public static class ComparablePath<E extends Comparable<? super E>>
                                extends AnyPath<E> implements ComparableExpression<E, Path<E>>, %2$s {
                            public ComparablePath(Supplier<Path<E>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }

                        public static class ComparableExp<E extends Comparable<? super E>>
                                extends AnyExp<E> implements ComparableExpression<E, Expression<E>>, %2$s {
                            public ComparableExp(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        public static class StringPath extends AnyPath<String> implements StringExpression<Path<String>>, %2$s {
                            public StringPath(Supplier<Path<String>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }

                        public static class StringExp extends AnyExp<String> implements StringExpression<Expression<String>>, %2$s {
                            public StringExp(Supplier<Expression<String>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        public static class BooleanPath extends AnyPath<Boolean> implements BooleanExpression<Path<Boolean>>, %2$s {
                            public BooleanPath(Supplier<Path<Boolean>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }
                        public static class BooleanExp extends AnyExp<Boolean> implements BooleanExpression<Expression<Boolean>>, %2$s {
                            public BooleanExp(Supplier<Expression<Boolean>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        public static class NumberPath<T extends Number> extends AnyPath<T> implements NumberExpression<T, Path<T>>, %2$s {
                            public NumberPath(Supplier<Path<T>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }

                        public static class NumberExp<T extends Number> extends AnyExp<T> implements NumberExpression<T, Expression<T>>, %2$s {
                            public NumberExp(Supplier<Expression<T>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }


                        public static class AnyCollectionExp<C extends Collection<?>, T extends Expression<C>> implements AnyCollectionExpression<C, T>, %2$s {
                            private final Supplier<T> expression;
                            private final CriteriaBuilder builder;
                            public AnyCollectionExp(Supplier<T> expression, CriteriaBuilder builder) {
                                this.expression = expression;
                                this.builder = builder;
                            }
                            @Override public T get() { return expression.get(); }
                            @Override public CriteriaBuilder builder() { return builder; }
                        }

                        public static class CollectionExp<E, C extends Collection<E>, T extends Expression<C>>
                                extends AnyCollectionExp<C, T> implements CollectionExpression<E, C, T>, %2$s {
                            public CollectionExp(Supplier<T> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        // ------------------------------------------------------------------------

                        public interface AnyExpression<E, T extends Expression<E>> extends Supplier<T>, %2$s {
                            T get();
                            default Predicate eq(Expression<?> y) {
                                return builder().equal(get(), y);
                            }
                            default Predicate eq(Object y) {
                                return isEmpty(y) ? null : builder().equal(get(), y);
                            }
                            default Predicate ne(Expression<?> y) {
                                return builder().notEqual(get(), y);
                            }
                            default Predicate ne(Object y) {
                                return isEmpty(y) ? null : builder().notEqual(get(), y);
                            }
                            default Predicate isNull(Expression<?> x) {
                                return builder().isNull(get());
                            }
                            default Predicate nonNull() {
                                return builder().isNotNull(get());
                            }
                            default Order asc() { return builder().asc(get()); }
                            default Order desc() { return builder().desc(get()); }
                        }

                        public interface ComparableExpression<E extends Comparable<? super E>, T extends Expression<E>>
                                extends Supplier<T>, AnyExpression<E, T> {
                            T get();
                            default Predicate gt(Expression<? extends E> y) { return builder().greaterThan(get(), y); }
                            default Predicate gt(E y) { return isEmpty(y) ? null : builder().greaterThan(get(), y); }
                            default Predicate ge(Expression<? extends E> y) { return builder().greaterThanOrEqualTo(get(), y); }
                            default Predicate ge(E y) { return isEmpty(y) ? null : builder().greaterThanOrEqualTo(get(), y); }
                            default Predicate lt(Expression<? extends E> y) { return builder().lessThan(get(), y); }
                            default Predicate lt(E y) { return isEmpty(y) ? null : builder().lessThan(get(), y); }
                            default Predicate le(Expression<? extends E> y) { return builder().lessThanOrEqualTo(get(), y); }
                            default Predicate le(E y) { return isEmpty(y) ? null : builder().lessThanOrEqualTo(get(), y); }
                            default Predicate between(Expression<? extends E> x, Expression<? extends E> y) { return builder().between(get(), x, y); }
                            default Predicate between(E x, E y) {
                                if (isEmpty(x) && isEmpty(y)) {
                                    return null;
                                } else if (isEmpty(y)) {
                                    return ge(x);
                                } else if (isEmpty(x)) {
                                    return le(y);
                                } else {
                                    return builder().between(get(), x, y);
                                }
                            }
                        }

                        public interface StringExpression<T extends Expression<String>>
                                extends Supplier<T>, AnyExpression<String, T>, ComparableExpression<String, T> {
                            T get();
                            default Predicate like(Expression<String> pattern) { return builder().like(get(), pattern, '\\\\'); }
                            default Predicate like(String pattern) { return isEmpty(pattern) ? null : builder().like(get(), escaped(pattern), '\\\\'); }
                            default Predicate likePartial(String pattern) { return isEmpty(pattern) ? null : builder().like(get(), escapedPartial(pattern), '\\\\'); }
                            default Predicate notLike(Expression<String> pattern) { return builder().notLike(get(), pattern); }
                            default Predicate notLike(String pattern) { return isEmpty(pattern) ? null : builder().notLike(get(), escaped(pattern), '\\\\'); }
                            default Predicate notLikePartial(String pattern) { return isEmpty(pattern) ? null : builder().notLike(get(), escapedPartial(pattern), '\\\\'); }

                            Pattern ESCAPE_PATTERN = Pattern.compile("([%%_\\\\\\\\])");
                            private static String escaped(String str) {
                                return ESCAPE_PATTERN.matcher(str).replaceAll("\\\\\\\\$1") + "%%";
                            }
                            private static String escapedPartial(String str) {
                                return "%%" + ESCAPE_PATTERN.matcher(str).replaceAll("\\\\\\\\$1") + "%%";
                            }
                        }

                        public interface BooleanExpression<T extends Expression<Boolean>>
                                extends Supplier<T>, AnyExpression<Boolean, T>, ComparableExpression<Boolean, T> {
                            T get();
                            default Predicate isTrue() { return builder().isTrue(get()); }
                            default Predicate isFalse() { return builder().isFalse(get()); }
                        }

                        public interface NumberExpression<E extends Number, T extends Expression<E>>
                                extends Supplier<T>, AnyExpression<E, T> {
                            T get();
                            default Predicate gt(Expression<? extends Number> y) { return builder().gt(get(), y); }
                            default Predicate gt(Number y) { return Objects.isNull(y) ? null : builder().gt(get(), y); }
                            default Predicate ge(Expression<? extends Number> y) { return builder().ge(get(), y); }
                            default Predicate ge(Number y) { return Objects.isNull(y) ? null : builder().ge(get(), y); }
                            default Predicate lt(Expression<? extends Number> y) { return builder().lt(get(), y); }
                            default Predicate lt(Number y) { return Objects.isNull(y) ? null : builder().lt(get(), y); }
                            default Predicate le(Expression<? extends Number> y) { return builder().le(get(), y); }
                            default Predicate le(Number y) { return Objects.isNull(y) ? null : builder().le(get(), y); }
                        }

                        public interface AnyCollectionExpression<C extends Collection<?>, T extends Expression<C>>
                                extends Supplier<T>, AnyExpression<C, T> {
                            T get();
                            default Predicate isEmpty() { return builder().isEmpty(get()); }
                            default Predicate isNotEmpty() { return builder().isNotEmpty(get()); }
                            default Expression<Integer> size(Expression<C> collection) { return builder().size(get()); }
                        }

                        public interface CollectionExpression<E, C extends Collection<E>, T extends Expression<C>>
                                extends Supplier<T>, AnyExpression<C, T>, AnyCollectionExpression<C, T> {
                            T get();
                            default Predicate isMember(Expression<E> elem) { return builder().isMember(elem, get()); }
                            default Predicate isMember(E elem) { return Objects.isNull(elem) ? null : builder().isMember(elem, get()); }
                            default Predicate isNotMember(Expression<E> elem) { return builder().isMember(elem, get()); }
                            default Predicate isNotMember(E elem) { return Objects.isNull(elem) ? null : builder().isMember(elem, get()); }
                        }

                        public interface MapKeyPath<K> extends Supplier<Path<K>> {
                            Path<K> key();
                        }
                        public interface MapValuePath<V> extends Supplier<Path<V>> {
                            Path<V> key();
                        }
                        private static boolean isEmpty(Object obj) {
                            if (Objects.isNull(obj)) {
                                return true;
                            }
                            if (obj instanceof String str) {
                                return str.isEmpty();
                            }
                            return false;
                        }

                    }
                    """.formatted(CRITERIA, BUILDER_AWARE));
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", CRITERIA, e.getMessage());
        }

    }


    /**
     * Write a repository class file.
     */
    private void writeRepositoryClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + REPOSITORY))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + "." + REPOSITORY);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add("java.io.Serializable");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaMetaModelEnhanceProcessor.class.getName()));
                pw.println("""
                    public interface %1$s<PK extends Serializable, E, R extends %2$s<E>> {
                        %3$s<E, R> rootSource();
                    }
                    """.formatted(REPOSITORY, ROOT_AWARE, ROOT_SOURCE));
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", REPOSITORY, e.getMessage());
        }

    }

}
