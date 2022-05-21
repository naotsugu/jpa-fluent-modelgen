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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Import statement generator.
 *
 * @author Naotsugu Kobayashi
 */
public class ImportSentences {

    private final Map<String, String> map;
    private final String selfPackage;

    public ImportSentences(String selfPackage) {
        this.map = new HashMap<>();
        this.selfPackage = selfPackage;
        initImplicit();
    }

    public static ImportSentences of(String selfPackage) {
        return new ImportSentences(selfPackage);
    }

    private void initImplicit() {
        map.put("Expression",     "jakarta.persistence.criteria.Expression");
        map.put("Root",           "jakarta.persistence.criteria.Root");
        map.put("Join",           "jakarta.persistence.criteria.Join");
        map.put("Path",           "jakarta.persistence.criteria.Path");
        map.put("ListJoin",       "jakarta.persistence.criteria.ListJoin");
        map.put("SetJoin",        "jakarta.persistence.criteria.SetJoin");
        map.put("MapJoin",        "jakarta.persistence.criteria.MapJoin");
        map.put("CollectionJoin", "jakarta.persistence.criteria.CollectionJoin");
        map.put("List",           "java.util.List");
        map.put("Map",            "java.util.Map");
        map.put("Set",            "java.util.Set");
        map.put("Collection",     "java.util.Collection");
        map.put("Supplier",       "java.util.function.Supplier");
        map.put("Generated",      "javax.annotation.processing.Generated");
    }

    protected String generateImports() {
        return map.values().stream().map(s -> "import " + s + ";")
            .sorted()
            .collect(Collectors.joining("\n"));
    }

    public String apply(String fqcn) {
        if (Objects.isNull(fqcn) || !fqcn.contains(".")) {
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

    private boolean inPackage(String fqcn) {
        return (selfPackage + "." + simpleName(fqcn)).equals(fqcn);
    }

    private static String simpleName(String fqcn) {
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
    }

}
