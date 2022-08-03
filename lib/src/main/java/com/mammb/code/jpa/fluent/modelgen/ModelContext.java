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
import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Context of metamodel enhance process.
 *
 * @author Naotsugu Kobayashi
 */
public class ModelContext extends Context {

    /** Generated model classes holder. */
    private final Collection<StaticMetamodelEntity> generatedModelClasses;

    /** RepositoryRootTypes. */
    private final Collection<RepositoryTraitType> repositoryTraits;

    /** Add repository option. */
    private final boolean addRepository;

    /** Mode of jakarta or javax. */
    private boolean jakarta;


    /**
     * Private constructor.
     * @param pe the annotation processing environment
     * @param debug the mode of debug
     * @param addRepository the mode of add repository
     */
    protected ModelContext(ProcessingEnvironment pe, boolean debug, boolean addRepository) {
        super(pe, debug);
        this.generatedModelClasses = new HashSet<>();
        this.repositoryTraits = new HashSet<>();
        this.addRepository = addRepository;
        this.jakarta = true;
    }


    /**
     * Create the context instance.
     * @param pe processing environment
     * @param debug the mode of debug
     * @param addRepository the mode of add repository
     * @return the context
     */
    public static ModelContext of(ProcessingEnvironment pe, boolean debug, boolean addRepository) {
        return new ModelContext(pe, debug, addRepository);
    }


    /**
     * Add a repositoryRootType.
     * @param repositoryRootType {@link RepositoryTraitType}
     */
    public void addRepositoryTraitType(RepositoryTraitType repositoryRootType) {
        repositoryTraits.add(repositoryRootType);
    }


    /**
     * Get the repository trait types.
     * @return the repository trait types
     */
    public List<RepositoryTraitType> getRepositoryTraitTypes() {
        return List.copyOf(repositoryTraits);
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
    public boolean isAlreadyGenerated(String name) {
        return generatedModelClasses.stream().anyMatch(entity -> entity.getQualifiedName().equals(name));
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
