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

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Context of metamodel enhance process.
 *
 * @author Naotsugu Kobayashi
 */
public class Context {

    /** Annotation processing environment. */
    private final ProcessingEnvironment pe;

    /** Mode of debug. */
    private final boolean debug;

    /** Mode of jakarta or javax. */
    private boolean jakarta;


    /**
     * Private constructor.
     */

    /**
     * Private constructor.
     * @param pe the annotation processing environment
     * @param debug the mode of debug
     */
    protected Context(ProcessingEnvironment pe, boolean debug) {
        this.pe = pe;
        this.debug = debug;
        this.jakarta = true;
    }


    /**
     * Create the context instance.
     * @param pe processing environment
     * @return the context
     */
    public static Context of(ProcessingEnvironment pe) {
        return new Context(pe, Boolean.parseBoolean(pe.getOptions().get("debug")));
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
     */
    public void logDebug(String message) {
        if (!debug) return;
        pe.getMessager().printMessage(Diagnostic.Kind.OTHER, message);
    }


    /**
     * Write the info log message.
     * @param message the message
     */
    public void logInfo(String message) {
        pe.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }


    /**
     * Write the error log message.
     * @param message the message
     */
    public void logError(String message) {
        pe.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }


    /**
     * Get jakarta
     * @return jakarta
     */
    public boolean isJakarta() {
        return jakarta;
    }

    /**
     * Set jakarta
     * @param jakarta jakarta
     */
    public void setJakarta(boolean jakarta) {
        this.jakarta = jakarta;
    }

}
