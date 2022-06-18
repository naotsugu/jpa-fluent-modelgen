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

import com.mammb.code.jpa.fluent.modelgen.model.RepositoryTraitType;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

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
    private final Collection<StaticMetamodelEntity> generatedModelClasses;

    /** RepositoryRootTypes. */
    private final Collection<RepositoryTraitType> repositoryTraits;

    /** Mode of debug. */
    private final boolean debug;

    /** Add criteria option. */
    private final boolean addCriteria;

    /** Add repository option. */
    private final boolean addRepository;

    /** Mode of jakarta or javax. */
    private boolean jakarta;


    /**
     * Private constructor.
     * @param pe the annotation processing environment
     * @param debug the mode of debug
     * @param addCriteria the mode of add criteria
     * @param addRepository the mode of add repository
     */
    protected Context(ProcessingEnvironment pe, boolean debug, boolean addCriteria, boolean addRepository) {
        this.pe = pe;
        this.generatedModelClasses = new HashSet<>();
        this.repositoryTraits = new HashSet<>();
        this.debug = debug;
        this.addCriteria = addCriteria;
        this.addRepository = addRepository;
        this.jakarta = true;
    }


    /**
     * Create the context instance.
     * @param pe processing environment
     * @param debug the mode of debug
     * @param addCriteria the mode of add criteria
     * @param addRepository the mode of add repository
     * @return the context
     */
    public static Context of(ProcessingEnvironment pe,
            boolean debug, boolean addCriteria, boolean addRepository) {
        return new Context(pe, debug, addCriteria, addRepository);
    }


    /**
     * Add a repositoryRootType.
     * @param repositoryRootType {@link RepositoryTraitType}
     */
    public void addRepositoryTraitType(RepositoryTraitType repositoryRootType) {
        repositoryTraits.add(repositoryRootType);
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
     * Add the given metamodel as generated.
     * @param entity the {@link StaticMetamodelEntity}
     */
    void addGenerated(StaticMetamodelEntity entity) {
        generatedModelClasses.add(entity);
    }


    /**
     * Gets whether generatedModelClasses are present.
     * @return {@code true} if generated model classes are present.
     */
    boolean hasGeneratedModel() {
        return !generatedModelClasses.isEmpty();
    }


    /**
     * Get whether the given qualified name has already been generated.
     * @param name the qualified name of metamodel
     * @return {@code true} if already generated
     */
    boolean isAlreadyGenerated(String name) {
        return generatedModelClasses.stream().anyMatch(entity -> entity.getQualifiedName().equals(name));
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
     * Get the option fo add criteria.
     * @return the option fo add criteria
     */
    public boolean isAddCriteria() {
        return addCriteria;
    }


    /**
     * Get the option fo add repository.
     * @return the option fo add repository
     */
    public boolean isAddRepository() {
        return addRepository;
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
    public Collection<StaticMetamodelEntity> getGeneratedModelClasses() {
        return List.copyOf(generatedModelClasses);
    }

}
