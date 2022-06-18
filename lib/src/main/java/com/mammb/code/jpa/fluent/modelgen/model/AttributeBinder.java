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
package com.mammb.code.jpa.fluent.modelgen.model;

import com.mammb.code.jpa.fluent.modelgen.writer.ImportBuilder;

import java.util.Objects;

/**
 * Static metamodel Attribute binder to template.
 *
 * @author Naotsugu Kobayashi
 */
public interface AttributeBinder extends MetamodelAttribute {

    /**
     * Bind attribute to template.
     * @param template template
     * @param imports importBuilder
     * @return binded template
     */
    default String bindTo(String template, ImportBuilder imports) {

        return template.formatted(
            imports.add(getEnclosingType().getName()),      // %1$s
            getAttributeType().isMap()
                ? imports.add(getKeyType().getName()) : "", // %2$s
            imports.add(getValueType().getName()),          // %3$s
            capitalize(getName()),                          // %4$s
            getName()                                       // %5$s
        );
    }


    /**
     * Capitalize the given string.
     * @param str the given string
     * @return Capitalized string
     */
    static String capitalize(String str) {
        return (Objects.isNull(str) || str.isEmpty())
            ? str
            : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
