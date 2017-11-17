package com.example.efragmentprocessor;


import com.example.efragmentannotation.BindFragment;
import com.example.efragmentannotation.BindFragmentInteractor;
import com.example.efragmentannotation.BindPresenter;
import com.example.efragmentannotation.BindViewDelegate;
import com.example.efragmentprocessor.dataholders.FragmentAnnotatedClass;
import com.example.efragmentprocessor.exceptions.NoPackageNameException;
import com.example.efragmentprocessor.exceptions.ProcessingException;
import com.example.efragmentprocessor.validators.ClassValidator;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Created by nazmuddinmavliwala on 10/11/16.
 */
@AutoService(Processor.class)
public class EFragmentProcessor extends AbstractProcessor {

    private Messager messager;
    private static final String ANNOTATION = "@" + BindFragment.class.getSimpleName();
    private EFragmentGenerator generator;
    private TypeElement TYPE_FRAGMENT;
    private TypeElement TYPE_SUPPORT_FRAGMENT;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        generator = EFragmentGenerator.newInstance(processingEnv);
        TYPE_FRAGMENT = elementUtils.getTypeElement("android.app.Fragment");
        TYPE_SUPPORT_FRAGMENT = elementUtils.getTypeElement("android.support.v4.app.Fragment");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindFragment.class.getCanonicalName());
        types.add(BindPresenter.class.getCanonicalName());
        types.add(BindViewDelegate.class.getCanonicalName());
        types.add(BindFragmentInteractor.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ArrayList<FragmentAnnotatedClass> annotatedClasses = new ArrayList<>();
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(BindFragment.class)) {
            TypeElement annotatedClass = (TypeElement) annotatedElement;
            if (!isValidClass(annotatedClass)) {
                return true;
            }
            try {
                annotatedClasses.add(buildAnnotatedClass(annotatedClass));
            } catch (NoPackageNameException | IOException e) {
                String message = String.format("Couldn't process class %s: %s", annotatedClass,
                        e.getMessage());
                messager.printMessage(ERROR, message, annotatedElement);
            }
        }
        try {
            generate(annotatedClasses);
        } catch (NoPackageNameException e) {
            messager.printMessage(ERROR, "Couldn't generate class + nopa");
        } catch (IOException e) {
            messager.printMessage(ERROR, "Couldn't generate class "+e.getMessage());
        } catch (ProcessingException ignored) {
        }
        return true;
    }

    private boolean isValidClass(TypeElement annotatedClass) {

        if (!ClassValidator.isPublic(annotatedClass)) {
            String message = String.format("Classes annotated with %s must be public.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isAbstract(annotatedClass)) {
            String message = String.format("Classes annotated with %s must not be abstract.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isInterface(annotatedClass)) {
            String message = String.format("Classes annotated with %s must not be an intereface.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (!ClassValidator.isFragment(typeUtils,annotatedClass,TYPE_FRAGMENT,TYPE_SUPPORT_FRAGMENT)) {
            String message = String.format("Classes annotated with %s must only be a fragment.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        return true;
    }

    private FragmentAnnotatedClass buildAnnotatedClass(TypeElement annotatedClass)
            throws NoPackageNameException, IOException {
        ArrayList<String> variableNames = new ArrayList<>();
        for (Element element : annotatedClass.getEnclosedElements()) {
            if (!(element instanceof VariableElement)) {
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            variableNames.add(variableElement.getSimpleName().toString());
        }

        return new FragmentAnnotatedClass(annotatedClass , variableNames);
    }

    private void generate(List<FragmentAnnotatedClass> annos)
            throws NoPackageNameException, IOException, ProcessingException {
        if (annos.size() == 0) {
            return;
        }
        generator.generateClasses(annos);
    }

}
