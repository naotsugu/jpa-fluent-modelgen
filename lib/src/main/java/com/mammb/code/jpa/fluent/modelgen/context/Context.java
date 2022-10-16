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
package com.mammb.code.jpa.fluent.modelgen.context;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Context of annotation processing.
 *
 * @author Naotsugu Kobayashi
 */
public class Context {

    /** Annotation processing environment. */
    private final ProcessingEnvironment pe;

    /** Mode of debug. */
    private final boolean debug;

    /** Generated classes(fqcn) holder. */
    private final Collection<String> generatedClasses;


    /**
     * Private constructor.
     * @param pe the annotation processing environment
     * @param debug the mode of debug
     */
    public Context(ProcessingEnvironment pe, boolean debug) {
        this.pe = pe;
        this.debug = debug;
        this.generatedClasses = new ArrayList<>();
    }


    /**
     * Get the annotation processing environment.
     * @return the annotation processing environment
     */
    protected ProcessingEnvironment pe() {
        return pe;
    }


    /**
     * Get the filer used to create new source, class, or auxiliary files.
     * @return the filer used to create new source, class, or auxiliary files
     */
    public Filer getFiler() {
        return pe.getFiler();
    }


    /**
     * Get an implementation of some utility methods for operating on elements.
     * @return utility for operating on elements
     */
    public Elements getElementUtils() {
        return pe.getElementUtils();
    }


    /**
     * Get an implementation of some utility methods for operating on types.
     * @return utility for operating on types
     */
    public Types getTypeUtils() {
        return pe.getTypeUtils();
    }


    /**
     * Write the debug log message.
     * @param message the message
     * @param args the arguments referenced by the format specifiers in this string.
     */
    public void logDebug(String message, Object... args) {
        if (!debug) return;
        pe.getMessager().printMessage(Diagnostic.Kind.OTHER, formatted(message, args));
    }


    /**
     * Write the info log message.
     * @param message the message
     * @param args the arguments referenced by the format specifiers in this string.
     */
    public void logInfo(String message, Object... args) {
        pe.getMessager().printMessage(Diagnostic.Kind.NOTE, formatted(message, args));
    }


    /**
     * Write the error log message.
     * @param message the message
     * @param args the arguments referenced by the format specifiers in this string.
     */
    public void logError(String message, Object... args) {
        pe.getMessager().printMessage(Diagnostic.Kind.ERROR, formatted(message, args));
    }


    /**
     * Format the given format string with args.
     * @param format the format string
     * @param args the arguments referenced by the format specifiers in this string.
     * @return the formatted string
     */
    private String formatted(String format, Object... args) {
        return Arrays.stream(args)
            .map(arg -> Objects.nonNull(arg) ? arg.toString() : "")
            .reduce(format, (str, arg) -> str.replaceFirst("\\{}", arg));
    }


    /**
     * Add the given model as generated.
     * @param fqcn the generated class fqcn
     */
    public void addGenerated(String fqcn) {
        generatedClasses.add(fqcn);
    }


    /**
     * Get whether the given qualified name has already been generated.
     * @param fqcn the qualified name of model
     * @return {@code true} if already generated
     */
    public boolean isAlreadyGenerated(String fqcn) {
        return generatedClasses.contains(fqcn);
    }

}
