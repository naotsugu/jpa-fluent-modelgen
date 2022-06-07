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

import javax.tools.FileObject;
import java.io.PrintWriter;

/**
 * Helper to write the criteria class factory class
 * using the {@link javax.annotation.processing.Filer} API.
 *
 * @author Naotsugu Kobayashi
 */
public class CriteriaClassWriter {

    /** Name of criteria class. */
    public static String PACKAGE = "com.mammb.code.jpa.core";

    /** Context of processing. */
    private final Context context;

    /**
     * Constructor.
     * @param context the context of processing
     */
    protected CriteriaClassWriter(Context context) {
        this.context = context;
    }


    /**
     * Create a criteria writer instance.
     * @param context the context of processing
     * @return the criteria class writer
     */
    public static CriteriaClassWriter of(Context context) {
        return new CriteriaClassWriter(context);
    }


    /**
     * Write a criteria class file.
     */
    public void writeFile() {
        try {
            FileObject fo = context.getFiler().createSourceFile(PACKAGE + ".Criteria_");

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                pw.println("""
                        package com.mammb.code.jpa.core;
                                                
                        import jakarta.persistence.criteria.CriteriaBuilder;
                        import jakarta.persistence.criteria.Expression;
                        import jakarta.persistence.criteria.Path;
                        import jakarta.persistence.criteria.Predicate;
                        import java.util.Collection;
                        import java.util.function.Supplier;
                                                
                        public class Criteria_ {
                                                
                            public static class AnyPath_<E> implements AnyExpression<E, Path<E>> {
                                private final Supplier<Path<E>> path;
                                private final CriteriaBuilder builder;
                                public AnyPath_(Supplier<Path<E>> path, CriteriaBuilder builder) {
                                    this.path = path;
                                    this.builder = builder;
                                }
                                @Override public Path<E> get() { return path.get(); }
                                @Override public CriteriaBuilder builder() { return builder; }
                            }
                            public static class AnyExpression_<E> implements AnyExpression<E, Expression<E>> {
                                private final Supplier<Expression<E>> expression;
                                private final CriteriaBuilder builder;
                                public AnyExpression_(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
                                    this.expression = expression;
                                    this.builder = builder;
                                }
                                @Override public Expression<E> get() { return expression.get(); };
                                @Override public CriteriaBuilder builder() { return builder; }
                            }
                                                
                            public static class ComparablePath_<E extends Comparable<? super E>>
                                    extends AnyPath_<E> implements ComparableExpression<E, Path<E>> {
                                public ComparablePath_(Supplier<Path<E>> path, CriteriaBuilder builder) {
                                    super(path, builder);
                                }
                            }
                            public static class ComparableExpression_<E extends Comparable<? super E>>
                                    extends AnyExpression_<E> implements ComparableExpression<E, Expression<E>> {
                                public ComparableExpression_(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
                                    super(expression, builder);
                                }
                            }
                                                
                            public static class StringPath_ extends AnyPath_<String> implements StringExpression<Path<String>> {
                                public StringPath_(Supplier<Path<String>> path, CriteriaBuilder builder) {
                                    super(path, builder);
                                }
                            }
                            public static class StringExpression_ extends AnyExpression_<String> implements StringExpression<Expression<String>> {
                                public StringExpression_(Supplier<Expression<String>> expression, CriteriaBuilder builder) {
                                    super(expression, builder);
                                }
                            }
                                                
                            public static class BooleanPath_ extends AnyPath_<Boolean> implements BooleanExpression<Path<Boolean>> {
                                public BooleanPath_(Supplier<Path<Boolean>> path, CriteriaBuilder builder) {
                                    super(path, builder);
                                }
                            }
                            public static class BooleanExpression_ extends AnyExpression_<Boolean> implements BooleanExpression<Expression<Boolean>> {
                                public BooleanExpression_(Supplier<Expression<Boolean>> expression, CriteriaBuilder builder) {
                                    super(expression, builder);
                                }
                            }
                                                
                            public static class NumberPath_<T extends Number> extends AnyPath_<T> implements NumberExpression<T, Path<T>> {
                                public NumberPath_(Supplier<Path<T>> path, CriteriaBuilder builder) {
                                    super(path, builder);
                                }
                            }
                            public static class NumberExpression_<T extends Number> extends AnyExpression_<T> implements NumberExpression<T, Expression<T>> {
                                public NumberExpression_(Supplier<Expression<T>> expression, CriteriaBuilder builder) {
                                    super(expression, builder);
                                }
                            }
                                                
                                                
                            public static class AnyCollectionExpression_<C extends Collection<?>, T extends Expression<C>> implements AnyCollectionExpression<C, T> {
                                private final Supplier<T> expression;
                                private final CriteriaBuilder builder;
                                public AnyCollectionExpression_(Supplier<T> expression, CriteriaBuilder builder) {
                                    this.expression = expression;
                                    this.builder = builder;
                                }
                                @Override public T get() { return expression.get(); }
                                @Override public CriteriaBuilder builder() { return builder; }
                            }
                                                
                            public static class CollectionExpression_<E, C extends Collection<E>, T extends Expression<C>>
                                    extends AnyCollectionExpression_<C, T> implements CollectionExpression<E, C, T> {
                                public CollectionExpression_(Supplier<T> expression, CriteriaBuilder builder) {
                                    super(expression, builder);
                                }
                            }
                                                
                            // ------------------------------------------------------------------------
                                                
                            public interface AnyExpression<E, T extends Expression<E>> extends Supplier<T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate equal(Expression<?> y) {
                                    return builder().equal(get(), y);
                                }
                                default Predicate equal(Object y) {
                                    return builder().equal(get(), y);
                                }
                                default Predicate notEqual(Expression<?> y) {
                                    return builder().notEqual(get(), y);
                                }
                                default Predicate notEqual(Object y) {
                                    return builder().notEqual(get(), y);
                                }
                                default Predicate isNull(Expression<?> x) {
                                    return builder().isNull(get());
                                }
                                default Predicate isNotNull() {
                                    return builder().isNotNull(get());
                                }
                            }
                                                
                            public interface ComparableExpression<E extends Comparable<? super E>, T extends Expression<E>>
                                    extends Supplier<T>, AnyExpression<E, T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate gt(Expression<? extends E> y) { return builder().greaterThan(get(), y); }
                                default Predicate gt(E y) { return builder().greaterThan(get(), y); }
                                default Predicate ge(Expression<? extends E> y) { return builder().greaterThanOrEqualTo(get(), y); }
                                default Predicate ge(E y) { return builder().greaterThanOrEqualTo(get(), y); }
                                default Predicate lt(Expression<? extends E> y) { return builder().lessThan(get(), y); }
                                default Predicate lt(E y) { return builder().lessThan(get(), y); }
                                default Predicate le(Expression<? extends E> y) { return builder().lessThanOrEqualTo(get(), y); }
                                default Predicate le(E y) { return builder().lessThanOrEqualTo(get(), y); }
                                default Predicate between(Expression<? extends E> x, Expression<? extends E> y) { return builder().between(get(), x, y); }
                                default Predicate between(E x, E y) { return builder().between(get(), x, y); }
                            }
                                                
                            public interface StringExpression<T extends Expression<String>>
                                    extends Supplier<T>, AnyExpression<String, T>, ComparableExpression<String, T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate like(Expression<String> pattern) { return builder().like(get(), pattern); }
                                default Predicate like(String pattern) { return builder().like(get(), pattern); }
                                default Predicate like(Expression<String> pattern, char escapeChar) { return builder().like(get(), pattern, escapeChar); }
                                default Predicate like(String pattern, char escapeChar) { return builder().like(get(), pattern, escapeChar); }
                                default Predicate notLike(Expression<String> pattern) { return builder().notLike(get(), pattern); }
                                default Predicate notLike(String pattern) { return builder().notLike(get(), pattern); }
                                default Predicate notLike(Expression<String> pattern, char escapeChar) { return builder().notLike(get(), pattern, escapeChar); }
                                default Predicate notLike(String pattern, char escapeChar) { return builder().notLike(get(), pattern, escapeChar); }
                            }
                                                
                            public interface BooleanExpression<T extends Expression<Boolean>>
                                    extends Supplier<T>, AnyExpression<Boolean, T>, ComparableExpression<Boolean, T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate isTrue() { return builder().isTrue(get()); }
                                default Predicate isFalse() { return builder().isFalse(get()); }
                            }
                                                
                            public interface NumberExpression<E extends Number, T extends Expression<E>>
                                    extends Supplier<T>, AnyExpression<E, T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate gt(Expression<? extends Number> y) { return builder().gt(get(), y); }
                                default Predicate gt(Number y) { return builder().gt(get(), y); }
                                default Predicate ge(Expression<? extends Number> y) { return builder().ge(get(), y); }
                                default Predicate ge(Number y) { return builder().ge(get(), y); }
                                default Predicate lt(Expression<? extends Number> y) { return builder().lt(get(), y); }
                                default Predicate lt(Number y) { return builder().lt(get(), y); }
                                default Predicate le(Expression<? extends Number> y) { return builder().le(get(), y); }
                                default Predicate le(Number y) { return builder().le(get(), y); }
                            }
                                                
                            public interface AnyCollectionExpression<C extends Collection<?>, T extends Expression<C>>
                                    extends Supplier<T>, AnyExpression<C, T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate isEmpty() { return builder().isEmpty(get()); }
                                default Predicate isNotEmpty() { return builder().isNotEmpty(get()); }
                                default Expression<Integer> size(Expression<C> collection) { return builder().size(get()); }
                            }
                                                
                            public interface CollectionExpression<E, C extends Collection<E>, T extends Expression<C>>
                                    extends Supplier<T>, AnyExpression<C, T>, AnyCollectionExpression<C, T> {
                                T get();
                                CriteriaBuilder builder();
                                default Predicate isMember(Expression<E> elem) { return builder().isMember(elem, get()); }
                                default Predicate isMember(E elem) { return builder().isMember(elem, get()); }
                                default Predicate isNotMember(Expression<E> elem) { return builder().isMember(elem, get()); }
                                default Predicate isNotMember(E elem) { return builder().isMember(elem, get()); }
                            }
                                                
                            public interface MapKeyPath<K> extends Supplier<Path<K>> {
                                Path<K> key();
                            }
                            public interface MapValuePath<V> extends Supplier<Path<V>> {
                                Path<V> key();
                            }
                                                
                        }
                                                
                        """);
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write criteria class : " + e.getMessage());
        }

    }

}
