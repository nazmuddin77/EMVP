package com.example.efragmentprocessor;
import com.example.efragmentprocessor.exceptions.NoPackageNameException;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static com.example.efragmentprocessor.utils.Utils.getPackageName;
import static com.example.efragmentprocessor.utils.Utils.getTypeName;


/**
 * Created by nazmuddinmavliwala on 01/12/16.
 */
public class ArgumentHolder {

    private final VariableElement variableElement;
    private final TypeMirror fieldType;
    private final DeclaredType declaredFieldType;
    public final TypeElement fieldTypeElement;
    public final String variableName;
    private final String variablePackageName;
    private final String variableClassName;
    public final TypeName variableTypeName;

    public ArgumentHolder(Element element) throws NoPackageNameException {
        this.variableElement = (VariableElement)element;
        fieldType = variableElement.asType();
        declaredFieldType = (DeclaredType) fieldType;
        fieldTypeElement = (TypeElement) declaredFieldType.asElement();
        variableName = element.getSimpleName().toString();
        variablePackageName = getPackageName(fieldTypeElement);
        variableClassName = fieldTypeElement.getSimpleName().toString();
        variableTypeName = getTypeName(variablePackageName,variableClassName);
    }
}
