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

    /** The name of BuilderAware class. */
    public static final String BUILDER_AWARE = "BuilderAware";
    /** The name of QueryAware class. */
    public static final String QUERY_AWARE = "QueryAware";
    /** The name of Criteria class. */
    public static final String CRITERIA = "Criteria";
    /** The name of Repository class. */
    public static final String REPOSITORY = "Repository";
    /** The name of RootAware class. */
    public static final String ROOT_AWARE = "RootAware";
    /** The name of RootSource class. */
    public static final String ROOT_SOURCE = "RootSource";
    /** The name of Typed class. */
    public static final String TYPED = "Typed";

    /** Context of processing. */
    private final ModelContext context;


    /**
     * Constructor.
     * @param context the context of processing
     */
    protected ApiClassWriter(ModelContext context) {
        this.context = context;
    }


    /**
     * Create a criteria writer instance.
     * @param context the context of processing
     * @return the criteria class writer
     */
    public static ApiClassWriter of(ModelContext context) {
        return new ApiClassWriter(context);
    }


    /**
     * Write api classes.
     */
    public void writeClasses() {
        context.logDebug("Create api class");
        writeRootSourceClass();
        writeTypedClass();
        writeQueryAwareClass();
        writeBuilderAwareClass();
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
                imports.add("jakarta.persistence.criteria.AbstractQuery");
                imports.add("jakarta.persistence.criteria.Root");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
                pw.println("""
                    public interface RootSource<E, R extends RootAware<E>> {
                        R root(Root<E> source, AbstractQuery<?> query, CriteriaBuilder builder);
                        Class<E> rootClass();
                        static <E, R extends RootAware<E>> RootSource<E, R> directly(R root, Class<E> rootClass) {
                            return new RootSource<>() {
                                @Override
                                @SuppressWarnings("unchecked")
                                public R root(Root<E> source, AbstractQuery<?> query, CriteriaBuilder builder) {
                                    return (R) root.with(source, query);
                                }
                                @Override
                                public Class<E> rootClass() {
                                    return rootClass;
                                }
                            };
                        }
                    }
                    """.formatted(ROOT_SOURCE));

                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", ROOT_SOURCE, e.getMessage());
        }

    }


    /**
     * Write a QueryAware class file.
     */
    private void writeQueryAwareClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + QUERY_AWARE))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(PACKAGE_NAME + "." + QUERY_AWARE);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                imports.add("jakarta.persistence.criteria.AbstractQuery");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
                pw.println("""
                    public interface %1$s<Q extends AbstractQuery<?>> {
                        Q query();
                    }
                    """.formatted(QUERY_AWARE));
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", QUERY_AWARE, e.getMessage());
        }

    }


    /**
     * Write a BuilderAware class file.
     */
    private void writeBuilderAwareClass() {

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

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
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
                imports.add("jakarta.persistence.criteria.AbstractQuery");
                imports.add("jakarta.persistence.criteria.Root");
                imports.add("java.util.function.Supplier");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
                pw.println("""
                    public interface %1$s<E> extends
                            Supplier<Root<E>>,
                            %2$s,
                            %3$s<AbstractQuery<?>>,
                            Criteria.AnyExpression<E, Root<E>>,
                            %4$s<E> {

                        RootAware<E> with(Root<E> root, AbstractQuery<?> query);
                    }
                    """.formatted(ROOT_AWARE, BUILDER_AWARE, QUERY_AWARE, TYPED));
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
                imports.add("jakarta.persistence.criteria.Selection");
                imports.add("java.util.Collection");
                imports.add("java.util.Objects");
                imports.add("java.util.function.Supplier");
                imports.add("java.util.regex.Pattern");
                imports.add("java.math.BigDecimal");
                imports.add("java.math.BigInteger");
                imports.add("java.util.Map");

                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
                pw.println("""
                    public class %1$s {

                        public interface Selector<E, R extends RootAware<E>, U> {
                            Criteria.AnyExpression<U, ? extends Selection<U>> apply(R root);
                        }
                        public interface ExpressionSelector<E, R extends RootAware<E>, U> {
                            Criteria.AnyExpression<U, ? extends Expression<U>> apply(R root);
                        }

                        interface CommonType extends BuilderAware {}

                        public static class AnyPath<E> implements AnyExpression<E, Path<E>>, CommonType {
                            private final Supplier<Path<E>> path;
                            private final CriteriaBuilder builder;
                            public AnyPath(Supplier<Path<E>> path, CriteriaBuilder builder) {
                                this.path = path;
                                this.builder = builder;
                            }
                            @Override public Path<E> get() { return path.get(); }
                            @Override public CriteriaBuilder builder() { return builder; }
                        }

                        public static class AnyExp<E> implements AnyExpression<E, Expression<E>>, CommonType {
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
                                extends AnyPath<E> implements ComparableExpression<E, Path<E>>, CommonType {
                            public ComparablePath(Supplier<Path<E>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }

                        public static class ComparableExp<E extends Comparable<? super E>>
                                extends AnyExp<E> implements ComparableExpression<E, Expression<E>>, CommonType {
                            public ComparableExp(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        public static class StringPath extends AnyPath<String> implements StringExpression<Path<String>>, CommonType {
                            public StringPath(Supplier<Path<String>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }

                        public static class StringExp extends AnyExp<String> implements StringExpression<Expression<String>>, CommonType {
                            public StringExp(Supplier<Expression<String>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        public static class BooleanPath extends AnyPath<Boolean> implements BooleanExpression<Path<Boolean>>, CommonType {
                            public BooleanPath(Supplier<Path<Boolean>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }
                        public static class BooleanExp extends AnyExp<Boolean> implements BooleanExpression<Expression<Boolean>>, CommonType {
                            public BooleanExp(Supplier<Expression<Boolean>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        public static class NumberPath<T extends Number> extends AnyPath<T> implements NumberExpression<T, Path<T>>, CommonType {
                            public NumberPath(Supplier<Path<T>> path, CriteriaBuilder builder) {
                                super(path, builder);
                            }
                        }

                        public static class NumberExp<T extends Number> extends AnyExp<T> implements NumberExpression<T, Expression<T>>, CommonType {
                            public NumberExp(Supplier<Expression<T>> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }


                        public static class AnyCollectionExp<C extends Collection<?>, T extends Expression<C>> implements AnyCollectionExpression<C, T>, CommonType {
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
                                extends AnyCollectionExp<C, T> implements CollectionExpression<E, C, T>, CommonType {
                            public CollectionExp(Supplier<T> expression, CriteriaBuilder builder) {
                                super(expression, builder);
                            }
                        }

                        // ------------------------------------------------------------------------

                        public interface AnyExpression<E, T extends Expression<E>> extends Supplier<T>, CommonType {
                            T get();
                            default Predicate eq(AnyExpression<E, ?> y) { return builder().equal(get(), y.get()); }
                            default Predicate eq(Expression<?> y) { return builder().equal(get(), y); }
                            default Predicate eq(Object y) { return isEmpty(y) ? null : builder().equal(get(), y); }
                            default Predicate ne(AnyExpression<E, ?> y) { return builder().notEqual(get(), y.get()); }
                            default Predicate ne(Expression<?> y) { return builder().notEqual(get(), y); }
                            default Predicate ne(Object y) { return isEmpty(y) ? null : builder().notEqual(get(), y); }
                            default Predicate isNull(Expression<?> x) { return builder().isNull(get()); }
                            default Predicate nonNull() { return builder().isNotNull(get()); }
                            default Predicate in(AnyExpression<E, ?>... values) {
                                return get().in(Arrays.stream(values).map(AnyExpression::get).toArray(Expression<?>[]::new));
                            }
                            default Predicate in(Expression<?>... values) { return get().in(values); }
                            default Predicate in(Expression<Collection<?>> values) { return get().in(values); }
                            default Predicate in(Collection<?> values) { return get().in(values); }
                            default Predicate in(Object... values) { return get().in(values); }

                            default Order asc() { return builder().asc(get()); }
                            default Order desc() { return builder().desc(get()); }
                        }

                        public interface ComparableExpression<E extends Comparable<? super E>, T extends Expression<E>>
                                extends Supplier<T>, AnyExpression<E, T>, CommonType {
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
                                return (isEmpty(x) && isEmpty(y)) ? null
                                    : isEmpty(y) ? ge(x)
                                    : isEmpty(x) ? le(y)
                                    : builder().between(get(), x, y);
                            }
                        }

                        public interface StringExpression<T extends Expression<String>>
                                extends Supplier<T>, AnyExpression<String, T>, ComparableExpression<String, T>, CommonType {
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

                            default Expression<String> concat(String y) { return builder().concat(get(), y); }
                            default Expression<String> concat(Expression<String> y) { return builder().concat(get(), y); }
                            default Expression<String> substring(int from) { return builder().substring(get(), from); }
                            default Expression<String> substring(int from, int len) { return builder().substring(get(), from, len); }
                            default Expression<String> trim() { return builder().trim(get()); }
                            default Expression<String> lower() { return builder().lower(get()); }
                            default Expression<String> upper() { return builder().upper(get()); }
                            default Expression<Integer> length() { return builder().length(get()); }
                        }

                        public interface BooleanExpression<T extends Expression<Boolean>>
                                extends Supplier<T>, AnyExpression<Boolean, T>, ComparableExpression<Boolean, T>, CommonType {
                            T get();
                            default Predicate isTrue() { return builder().isTrue(get()); }
                            default Predicate isFalse() { return builder().isFalse(get()); }
                        }

                        public interface NumberExpression<E extends Number, T extends Expression<E>>
                                extends Supplier<T>, AnyExpression<E, T>, CommonType {
                            T get();
                            default Predicate gt(Expression<? extends Number> y) { return builder().gt(get(), y); }
                            default Predicate gt(Number y) { return Objects.isNull(y) ? null : builder().gt(get(), y); }
                            default Predicate ge(Expression<? extends Number> y) { return builder().ge(get(), y); }
                            default Predicate ge(Number y) { return Objects.isNull(y) ? null : builder().ge(get(), y); }
                            default Predicate lt(Expression<? extends Number> y) { return builder().lt(get(), y); }
                            default Predicate lt(Number y) { return Objects.isNull(y) ? null : builder().lt(get(), y); }
                            default Predicate le(Expression<? extends Number> y) { return builder().le(get(), y); }
                            default Predicate le(Number y) { return Objects.isNull(y) ? null : builder().le(get(), y); }

                            default Expression<Long> toLong() { return builder().toLong(get()); }
                            default Expression<Integer> toInteger() { return builder().toInteger(get()); }
                            default Expression<Float> toFloat() { return builder().toFloat(get()); }
                            default Expression<Double> toDouble() { return builder().toDouble(get()); }
                            default Expression<BigDecimal> toBigDecimal() { return builder().toBigDecimal(get()); }
                            default Expression<BigInteger> toBigInteger() { return builder().toBigInteger(get()); }
                        }

                        public interface AnyCollectionExpression<C extends Collection<?>, T extends Expression<C>>
                                extends Supplier<T>, AnyExpression<C, T>, CommonType {
                            T get();
                            default Predicate isEmpty() { return builder().isEmpty(get()); }
                            default Predicate isNotEmpty() { return builder().isNotEmpty(get()); }
                            default Expression<Integer> size(Expression<C> collection) { return builder().size(get()); }
                        }

                        public interface CollectionExpression<E, C extends Collection<E>, T extends Expression<C>>
                                extends Supplier<T>, AnyExpression<C, T>, AnyCollectionExpression<C, T>, CommonType {
                            T get();
                            default Predicate isMember(Expression<E> elem) { return builder().isMember(elem, get()); }
                            default Predicate isMember(E elem) { return Objects.isNull(elem) ? null : builder().isMember(elem, get()); }
                            default Predicate isNotMember(Expression<E> elem) { return builder().isMember(elem, get()); }
                            default Predicate isNotMember(E elem) { return Objects.isNull(elem) ? null : builder().isMember(elem, get()); }
                        }

                        public interface AnyMapExpression<M extends Map<?, ?>, T extends Expression<M>>
                            extends Supplier<T>, AnyExpression<M, T>, CommonType {
                            T get();
                        }

                        public interface MapExpression<K, V, M extends Map<K, V>, T extends Expression<M>>
                            extends Supplier<T>, AnyExpression<M, T>, AnyMapExpression<M, T>, CommonType {
                            T get();
                        }

                        public interface MapKeyPath<K> extends Supplier<Path<K>>, CommonType {
                            Path<K> key();
                        }
                        public interface MapValuePath<V> extends Supplier<Path<V>>, CommonType {
                            Path<V> key();
                        }

                        private static boolean isEmpty(Object obj) {
                            return Objects.isNull(obj) || (obj instanceof String str && str.isEmpty());
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

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
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


    /**
     * Write a Typed class file.
     */
    private void writeTypedClass() {

        if (Objects.nonNull(context.getElementUtils().getTypeElement(PACKAGE_NAME + "." + TYPED))) {
            return;
        }

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + "." + TYPED);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println("package " + imports.getSelfPackage() + ";");
                pw.println();

                imports.add("javax.annotation.processing.Generated");
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                pw.println("@Generated(value = \"%s\")".formatted(JpaModelProcessor.class.getName()));
                pw.println("""
                    public interface Typed<E> {
                        Class<E> type();
                    }
                   """.formatted(TYPED, ROOT_AWARE));
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", TYPED, e.getMessage());
        }

    }

}
