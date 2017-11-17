package com.example.efragmentprocessor.validators;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by nazmuddinmavliwala on 02/08/16.
 */
public class ClassValidator {

    public static boolean isPublic(TypeElement annotatedClass) {
        return annotatedClass.getModifiers().contains(PUBLIC);
    }

    public static boolean isAbstract(TypeElement annotatedClass) {
        return annotatedClass.getModifiers().contains(ABSTRACT);
    }

    public static boolean isInterface(TypeElement annotatedClass) {
        return annotatedClass.getKind().isInterface();
    }

    public static boolean isFragment(Types typeUtils, TypeElement classElement
            , TypeElement fragmentType, TypeElement supportFragmentType) {

        return isValidClass(typeUtils, classElement, fragmentType)
                || isValidClass(typeUtils, classElement, supportFragmentType);

    }

    private static boolean isValidClass(Types typeUtils, TypeElement classElement
            , TypeElement superClassTypeElement) {
        if (superClassTypeElement != null) {
            if (typeUtils.isSubtype(classElement.asType(),
                    superClassTypeElement.asType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPresenter(Types typeUtils
            , TypeElement fieldTypeElement, TypeElement presenterType) {
        return isValidClass(typeUtils,fieldTypeElement,presenterType);
    }

    public static boolean isViewDelegate(Types typeUtils
            , TypeElement fieldTypeElement, TypeElement viewdelegateType) {
        return isValidClass(typeUtils,fieldTypeElement,viewdelegateType);
    }

    public static boolean isFragmentInteractor(Types typeUtils
            , TypeElement fieldTypeElement, TypeElement fragmentInteractorType) {
        return isValidClass(typeUtils,fieldTypeElement,fragmentInteractorType);
    }
}
