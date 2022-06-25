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
import com.mammb.code.jpa.fluent.modelgen.writer.ApiClassWriter;
import com.mammb.code.jpa.fluent.modelgen.writer.ModelClassWriter;
import com.mammb.code.jpa.fluent.modelgen.writer.RepositoryClassWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Objects;
import java.util.Set;

/**
 * Main annotation processor.
 *
 * @author Naotsugu Kobayashi
 */
@SupportedAnnotationTypes({
    StaticMetamodelEntity.ANNOTATION_TYPE,
    StaticMetamodelEntity.ANNOTATION_TYPE_LEGACY,
    RepositoryTraitType.ANNOTATION_TYPE
})
@SupportedOptions({
    JpaMetaModelEnhanceProcessor.DEBUG_OPTION,
    JpaMetaModelEnhanceProcessor.ADD_REPOSITORY,
})
public class JpaMetaModelEnhanceProcessor extends AbstractProcessor {

    /** Debug option. */
    public static final String DEBUG_OPTION = "debug";

    /** Add criteria option. */
    public static final String ADD_REPOSITORY = "addRepository";

    /** Context of processing. */
    private Context context;

    /** Annotation processing round. */
    private int round = 0;


    @Override
    public void init(ProcessingEnvironment env) {

        super.init(env);
        this.context = Context.of(env,
            Boolean.parseBoolean(env.getOptions().getOrDefault(JpaMetaModelEnhanceProcessor.DEBUG_OPTION, "false")),
            Boolean.parseBoolean(env.getOptions().getOrDefault(JpaMetaModelEnhanceProcessor.ADD_REPOSITORY, "false")));

        var version = getClass().getPackage().getImplementationVersion();
        context.logInfo("JPA Static-Metamodel Enhance Generator {}", (Objects.isNull(version) ? "" : version));

    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        context.logDebug("### Round : {}", ++round);

        if (roundEnv.errorRaised() || roundEnv.processingOver() || annotations.isEmpty()) {
            return false;
        }

        try {

            for (Element element : roundEnv.getRootElements()) {
                StaticMetamodelEntity.of(context, element)
                    .ifPresent(this::createMetaModelClasses);
                RepositoryTraitType.of(context, element)
                    .ifPresent(context::addRepositoryTraitType);
            }

            if (context.hasGeneratedModel()) {
                ApiClassWriter.of(context).writeClasses();
                if (context.isAddRepository()) {
                    context.getGeneratedModelClasses().stream()
                        .filter(StaticMetamodelEntity::isEntityMetamodel)
                        .forEach(model -> RepositoryClassWriter.of(context, model).writeFile());
                }
            }

        } catch (Exception e) {
            context.logError("Exception : {}", e.getMessage());
        }

        return false;

    }


    /**
     * Create the source class
     * @param entity {@link StaticMetamodelEntity}
     */
    protected void createMetaModelClasses(final StaticMetamodelEntity entity) {

        if (!entity.getTargetEntity().getPersistenceType().isEntity() &&
            !entity.getTargetEntity().getPersistenceType().isEmbeddable()) {
            return;
        }

        if (context.isAlreadyGenerated(entity.getQualifiedName())) {
            context.logDebug("Skip meta model generation : {}", entity.getQualifiedName());
            return;
        }

        ModelClassWriter.of(context, entity).writeFile();
        context.addGenerated(entity);

    }

}
