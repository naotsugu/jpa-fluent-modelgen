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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Context of metamodel enhance process.
 *
 * @author Naotsugu Kobayashi
 */
public class Context {

    /** Annotation processing environment. */
    private final ProcessingEnvironment pe;

    /** Generated model classes holder. */
    private final Collection<String> generatedModelClasses;

    /** Mode of debug. */
    private final boolean debug;

    /** Add root factory option. */
    private final boolean addRoot;

    /** Mode of jakarta or javax. */
    private boolean jakarta;


    /**
     * Private constructor.
     * @param pe the annotation processing environment
     * @param debug the mode of debug
     * @param addRoot the mode of add root factory
     */
    protected Context(ProcessingEnvironment pe, boolean debug, boolean addRoot) {
        this.pe = pe;
        this.generatedModelClasses = new HashSet<>();
        this.debug = debug;
        this.addRoot = addRoot;
        this.jakarta = true;
    }


    /**
     * Create the context instance.
     * @param pe processing environment
     * @param debug the mode of debug
     * @param addRoot the mode of add root factory
     * @return the context
     */
    public static Context of(ProcessingEnvironment pe, boolean debug, boolean addRoot) {
        return new Context(pe, debug, addRoot);
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
     * Mark the given metamodel as generated.
     * @param name the qualified name of metamodel
     */
    void markGenerated(String name) {
        generatedModelClasses.add(name);
    }


    /**
     * Get whether the given qualified name has already been generated.
     * @param name the qualified name of metamodel
     * @return {@code true} if already generated
     */
    boolean isAlreadyGenerated(String name) {
        return generatedModelClasses.contains(name);
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
     * Get the option fo add root factory.
     * @return the mode of add root factory
     */
    public boolean isAddRoot() {
        return addRoot;
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


    /**
     * Get the generated model classes.
     * @return the generated model classes
     */
    public Collection<String> getGeneratedModelClasses() {
        return List.copyOf(generatedModelClasses);
    }

}
