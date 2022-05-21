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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Main annotation processor.
 *
 * @author Naotsugu Kobayashi
 */
@SupportedAnnotationTypes({
    StaticMetamodelEntity.ANNOTATION_TYPE
})
public class JpaMetaModelEnhanceProcessor extends AbstractProcessor {

    /** Context of processing. */
    private Context context;


    @Override
    public void init(ProcessingEnvironment env) {

        super.init(env);
        this.context = Context.of(env);

        var version = getClass().getPackage().getImplementationVersion();
        context.logInfo("JPA Static-Metamodel Enhance Generator " + (Objects.isNull(version) ? "" : version));

    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver() || annotations.isEmpty()) {
            return false;
        }

        roundEnv.getRootElements().stream()
            .map(this::asStaticMetamodelEntity)
            .flatMap(Optional::stream)
            .forEach(this::createMetaModelClasses);

        return false;

    }


    /**
     * Create the {@link StaticMetamodelEntity}.
     * @param element source
     * @return the {@link StaticMetamodelEntity}
     */
    protected Optional<StaticMetamodelEntity> asStaticMetamodelEntity(final Element element) {
        return StaticMetamodelEntity.of(context, element);
    }


    /**
     * Create the source class
     * @param entity {@link StaticMetamodelEntity}
     */
    protected void createMetaModelClasses(final StaticMetamodelEntity entity) {
        context.logDebug("Create meta model " + entity.getQualifiedName());
        new ClassWriter(context, entity).writeFile();
    }

}
