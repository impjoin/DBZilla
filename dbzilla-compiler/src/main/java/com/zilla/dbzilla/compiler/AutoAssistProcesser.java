/*
 * Copyright 2017. Zilla Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zilla.dbzilla.compiler;

import android.database.sqlite.SQLiteStatement;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.zilla.dbzilla.annotations.AutoAssist;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public final class AutoAssistProcesser extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoAssist.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(AutoAssist.class);
        for (Element element : set) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "only support class");
            }
            TypeElement typeElement = (TypeElement) element;

            TypeSpec createdClass = null;
            com.squareup.javapoet.TypeSpec.Builder builder = TypeSpec.classBuilder(element.getSimpleName().toString() + "Attribute")//指定生成的类
                    .addModifiers(Modifier.PUBLIC);


            /*generate methods*/
            MethodSpec bindMethod = null;
            MethodSpec.Builder mb = null;
//            Class typeClass = null;
//            try {
//                typeClass = Class.forName(element.getEnclosingElement().toString() + "." + element.getSimpleName());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
            mb = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(SQLiteStatement.class, "stat")
                    .addParameter(TypeName.get(element.asType()), "model")
                    //                    .addStatement("System.out.println($S)", "this`s java source is created by dynamic")
                    .addStatement("stat.clearBindings()");


            List<VariableElement> allFields = ElementFilter.fieldsIn(element.getEnclosedElements());
            for (VariableElement variableElement : allFields) {

                String variableName = variableElement.getSimpleName().toString();
                String variableName2 = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);

                /*generate fields*/
                FieldSpec fieldSpec = FieldSpec.builder(String.class, variableName.toUpperCase(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", variableName).build();
                builder.addField(fieldSpec);

                /*binding fields*/
                StringBuilder sb = new StringBuilder();
                String type = TypeName.get(variableElement.asType()).box().toString();
                type = type.substring(type.lastIndexOf(".") + 1);

                String appendExtra = "";
                String getOriS = "model.get";
                if ("Integer".equals(type)) {
                    type = "Long";
                } else if ("Short".equals(type)) {
                    type = "Double";
                } else if ("Boolean".equals(type)) {
                    type = "Long";
                    appendExtra = " ? 1L:0L";
                    getOriS = "model.is";
                }
                sb.append("stat.bind").append(type).append("( ").append(1).append(" , ").append(getOriS).append(variableName2).append("()").append(appendExtra).append(")");

                mb.addStatement(sb.toString());
            }

            bindMethod = mb.build();

            builder.addMethod(bindMethod);
            createdClass = builder.build();
            JavaFile javaFile = JavaFile.builder(typeElement.getEnclosingElement().toString(), createdClass).build();

            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
