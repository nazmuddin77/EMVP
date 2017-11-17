package com.example.efragmentprocessor.utils;
import com.example.efragmentprocessor.exceptions.NoPackageNameException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.squareup.javapoet.JavaFile.builder;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 */
public class Utils {

    private static Utils instance;
    private  static ProcessingEnvironment processingEnv;
    private static Elements elementUtils;
    private static Types typeUtils;

    public Utils(ProcessingEnvironment processingEnv) {
        Utils.processingEnv = processingEnv;
        Utils.elementUtils = processingEnv.getElementUtils();
        Utils.typeUtils = processingEnv.getTypeUtils();
    }

    public static Utils newInstance(ProcessingEnvironment processingEnv) {
        if (instance == null) {
            instance = new Utils(processingEnv);
        }
        return instance;
    }

    public static String getPackageName(TypeElement type)
            throws NoPackageNameException {
        PackageElement pkg = elementUtils.getPackageOf(type);
        if (pkg.isUnnamed()) {
            throw new NoPackageNameException(type);
        }
        return pkg.getQualifiedName().toString();
    }

    public static TypeName getTypeName(String packageName, String className) {
        return ClassName.get(packageName,className);
    }

    public static TypeVariableName getTypeVariableName(String variable) {
        return TypeVariableName.get(variable);
    }

    public static ParameterSpec getParamSpec(TypeName typeName, String name, Modifier... modifiers) {
        return ParameterSpec.builder(typeName,name,modifiers).build();
    }

    public static TypeElement getTypeElement(String type) {
        return elementUtils.getTypeElement(type);
    }

    public static void writeFile(String packageName, TypeSpec typeSpec) throws IOException {
        JavaFile javaFile = builder(packageName,typeSpec).build();
        javaFile.writeTo(processingEnv.getFiler());
    }
}
