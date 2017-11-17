package com.example.efragmentprocessor.dataholders;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 */
public class FragmentAnnotatedClass {

    public final String annotatedClassName;
    public final List<String> variableNames;
    public final TypeElement typeElement;

    public FragmentAnnotatedClass(TypeElement typeElement, List<String> variableNames) {
        this.annotatedClassName = typeElement.getSimpleName().toString();
        this.variableNames = variableNames;
        this.typeElement = typeElement;
    }

    public TypeMirror getType() {
        return typeElement.asType();
    }
}
