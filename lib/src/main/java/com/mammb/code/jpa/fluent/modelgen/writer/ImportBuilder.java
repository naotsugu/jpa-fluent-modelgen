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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Import statement generator.
 *
 * @author Naotsugu Kobayashi
 */
public class ImportBuilder {

    /** Map with class name as key. */
    private final Map<String, String> map;

    /** The name of self package. */
    private final String selfPackage;


    /**
     * Constructor.
     * @param selfPackage the name of self package
     */
    private ImportBuilder(String selfPackage) {
        this.map = new HashMap<>();
        this.selfPackage = Objects.isNull(selfPackage) ? "" : selfPackage;
    }


    /**
     * Create the ImportSentences
     * @param selfPackage the name of selfPackage
     * @return the ImportSentences
     */
    public static ImportBuilder of(String selfPackage) {
        return new ImportBuilder(selfPackage);
    }


    /**
     * Generate import sentences.
     * @param jakarta jakarta
     * @return import sentences
     */
    protected String generateImports(boolean jakarta) {

        if (!jakarta) {
            convertJakartaToJavax();
        }

        return map.values().stream().map(s -> "import " + s + ";")
            .sorted()
            .collect(Collectors.joining("\n"));
    }


    /**
     * Add import.
     * @param fqcn FQCN
     * @return Applied import name
     */
    public String add(String fqcn) {

        if (Objects.isNull(fqcn) || !fqcn.contains(".")) {
            return fqcn;
        }

        if (fqcn.endsWith("*")) {
            map.put(fqcn, fqcn);
            return fqcn;
        }

        var simpleName = simpleName(fqcn);
        if (fqcn.startsWith("java.lang.")) {
            return simpleName;
        } else if (!map.containsKey(simpleName)) {
            if (!inPackage(fqcn)) {
                map.put(simpleName, fqcn);
            }
            return simpleName;
        } else if (map.containsKey(simpleName) && map.get(simpleName).equals(fqcn)) {
            return simpleName;
        } else {
            return fqcn;
        }
    }


    /**
     * Removes all the imports.
     */
    public void clear() {
        map.clear();
    }


    /**
     * Get the name of self package.
     * @return the name of self package
     */
    public String getSelfPackage() {
        return selfPackage;
    }


    /**
     * Get whether the given fqcn belongs to a package.
     * @param fqcn the fqcn
     * @return {@code true} if the given fqcn belongs to a package, otherwise {@code false}
     */
    private boolean inPackage(String fqcn) {
        return (selfPackage + "." + simpleName(fqcn)).equals(fqcn);
    }


    /**
     * Get the simple name from the given fqcn.
     * @param fqcn the fqcn
     * @return the simple name
     */
    private static String simpleName(String fqcn) {
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
    }


    /**
     * Convert jakarta to javax.
     */
    private void convertJakartaToJavax() {
        map.replaceAll((k, v) ->
            v.startsWith("jakarta.persistence.")
                ? v.replace("jakarta.persistence.", "javax.persistence.")
                : v);
    }

}
