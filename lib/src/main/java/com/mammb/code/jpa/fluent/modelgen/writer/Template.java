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

import java.util.Map;

/**
 * The utility of string template.
 * @author Naotsugu Kobayashi
 */
public class Template {

    private String string;


    private Template(String string) {
        this.string = string;
    }


    /**
     * Create a template from the given string.
     * @param string the template
     * @return a {@link Template}
     */
    public static Template of(String string) {
        return new Template(string);
    }


    /**
     * Bind the variable to own template.
     * @param map the variable
     * @return the bound template
     */
    public Template bind(Map<String, String> map) {
        string = map.entrySet().stream()
            .reduce(string, (s, e) -> s.replace(e.getKey(), e.getValue()) , (s1, s2) -> s1 + s2);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param key the key name
     * @param value the value
     * @return the bound template
     */
    public Template bind(String key, String value) {
        string = string.replace(key, value);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2) {
        bind(k1, v1);
        bind(k2, v2);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @param k3 the key name
     * @param v3 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3) {
        bind(k1, v1, k2, v2);
        bind(k3, v3);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @param k3 the key name
     * @param v3 the value
     * @param k4 the key name
     * @param v4 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4) {
        bind(k1, v1, k2, v2, k3, v3);
        bind(k4, v4);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @param k3 the key name
     * @param v3 the value
     * @param k4 the key name
     * @param v4 the value
     * @param k5 the key name
     * @param v5 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4);
        bind(k5, v5);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @param k3 the key name
     * @param v3 the value
     * @param k4 the key name
     * @param v4 the value
     * @param k5 the key name
     * @param v5 the value
     * @param k6 the key name
     * @param v6 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
        bind(k6, v6);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @param k3 the key name
     * @param v3 the value
     * @param k4 the key name
     * @param v4 the value
     * @param k5 the key name
     * @param v5 the value
     * @param k6 the key name
     * @param v6 the value
     * @param k7 the key name
     * @param v7 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
        bind(k7, v7);
        return this;
    }


    /**
     * Bind the variable to own template.
     * @param k1 the key name
     * @param v1 the value
     * @param k2 the key name
     * @param v2 the value
     * @param k3 the key name
     * @param v3 the value
     * @param k4 the key name
     * @param v4 the value
     * @param k5 the key name
     * @param v5 the value
     * @param k6 the key name
     * @param v6 the value
     * @param k7 the key name
     * @param v7 the value
     * @param k8 the key name
     * @param v8 the value
     * @return the bound template
     */
    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
        bind(k8, v8);
        return this;
    }


    @Override
    public String toString() {
        return string;
    }


    /**
     * Get the string value.
     * @return the string value
     */
    public String getValue() {
        return string.substring(firstCharIndexOf(string));
    }


    /**
     * Get an indented string.
     * @param tab the size of tab
     * @return the string value
     */
    public String getIndentedValue(int tab) {
        var ret = string.indent(tab * 4);
        return ret.substring(firstCharIndexOf(ret));
    }


    /**
     * Get the index at which the character appears for the first time.
     * @param str the Target string
     * @return the index at which the character appears for the first time
     */
    private static int firstCharIndexOf(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
