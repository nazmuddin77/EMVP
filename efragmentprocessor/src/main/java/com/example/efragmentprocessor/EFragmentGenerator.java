package com.example.efragmentprocessor;
import com.example.efragmentannotation.BindFragment;
import com.example.efragmentannotation.BindFragmentInteractor;
import com.example.efragmentannotation.BindPresenter;
import com.example.efragmentannotation.BindViewDelegate;
import com.example.efragmentprocessor.dataholders.FragmentAnnotatedClass;
import com.example.efragmentprocessor.exceptions.NoPackageNameException;
import com.example.efragmentprocessor.exceptions.ProcessingException;
import com.example.efragmentprocessor.utils.Utils;
import com.example.efragmentprocessor.validators.ClassValidator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.example.efragmentprocessor.utils.Utils.getPackageName;
import static com.example.efragmentprocessor.utils.Utils.getParamSpec;
import static com.example.efragmentprocessor.utils.Utils.getTypeElement;
import static com.example.efragmentprocessor.utils.Utils.getTypeName;
import static com.example.efragmentprocessor.utils.Utils.getTypeVariableName;
import static com.example.efragmentprocessor.utils.Utils.writeFile;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Created by nazmuddinmavliwala on 24/11/16.
 */
public class EFragmentGenerator {

    private static final String EFRAGMENT_PACKAGE_NAME = "com.example.efragment";
    private static final String EFRAGMENT_CLASS_NAME = "EFragmentBinding";
    private static final String PRESENTER_NAME = "com.example.nazmuddinmavliwala.emvp.base.BasePresenter";
    private static final String VIEWDELEGATE_NAME = "com.example.nazmuddinmavliwala.emvp.base.BaseViewDelegate";
    private static final String INTERACTOR_NAME = "com.example.nazmuddinmavliwala.emvp.base.BaseFragmentInteractor";
    private static final String VIEW_PACKAGE_NAME = "android.view";
    private static final String VIEW = "View";
    private static final String VIEW_GROUP = "ViewGroup";
    private static final String INFLATER = "LayoutInflater";
    public static final String E_FRAGMENT_BINDER = "EFragmentBinder";
    private static TypeName INFLATER_TYPE;
    private static TypeName VIEW_GROUP_TYPE;
    private static TypeName VIEW_TYPE;
    private static TypeElement FRAGMENT_INTERACTOR_TYPE;
    private static TypeElement VIEW_DELEGATE_TYPE;
    private static TypeElement PRESENTER_TYPE;
    private static EFragmentGenerator instance;
    private final Messager messager;
    private final Elements elementUtils;
    private final Types typeUtils;
    private final ProcessingEnvironment processingEnv;
    private final Utils utils;

    private EFragmentGenerator(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
        this.utils = Utils.newInstance(processingEnv);
        PRESENTER_TYPE = getTypeElement(PRESENTER_NAME);
        VIEW_DELEGATE_TYPE = getTypeElement(VIEWDELEGATE_NAME);
        FRAGMENT_INTERACTOR_TYPE = getTypeElement(INTERACTOR_NAME);
        VIEW_TYPE = getTypeName(VIEW_PACKAGE_NAME,VIEW);
        VIEW_GROUP_TYPE = getTypeName(VIEW_PACKAGE_NAME,VIEW_GROUP);
        INFLATER_TYPE = getTypeName(VIEW_PACKAGE_NAME,INFLATER);
    }

    public static EFragmentGenerator newInstance(ProcessingEnvironment processingEnv) {
        if (instance == null) {
            instance = new EFragmentGenerator(processingEnv);
        }
        return instance;
    }

    public void generateClasses(
            List<FragmentAnnotatedClass> annos)
            throws NoPackageNameException
            , IOException
            , ProcessingException {

        for (FragmentAnnotatedClass annotatedClass : annos) {
            generateFragmentBindingClasses(annotatedClass);
        }
        generateAutoBindingClass(annos);
    }

    private void generateAutoBindingClass(
            List<FragmentAnnotatedClass> annos)
            throws NoPackageNameException
            , IOException {

        int i = 0;
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        while (i < annos.size()) {
            FragmentAnnotatedClass annotatedClass = annos.get(i);
            String packageName = getPackageName(annotatedClass.typeElement);
            String condition = "targetName.equals($T.class.getName())";
            String result = "return new $T().bind(($T)fragment,inflater,container)";
            TypeName annotatedClassName = getTypeName(packageName,annotatedClass.annotatedClassName);
            TypeName binderClassName
                    = getTypeName(packageName,getBindingClassName(annotatedClass));
            if (i == 0) {
                codeBlockBuilder.beginControlFlow(String.format("if (%s)"
                        , condition)
                        , annotatedClassName)
                        .addStatement(result
                                , binderClassName
                                , annotatedClassName)
                        .endControlFlow();
            } else {
                codeBlockBuilder.beginControlFlow(String.format("else if (%s)"
                        , condition)
                        , annotatedClassName);
                codeBlockBuilder.addStatement(result
                        , binderClassName
                        , annotatedClassName)
                        .endControlFlow();
            }
            i++;
        }
        MethodSpec bind = MethodSpec.methodBuilder("bind")
                .returns(ClassName.get("android.view","View"))
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(getParamSpec(TypeName.OBJECT,"fragment"))
                .addParameter(getParamSpec(INFLATER_TYPE,"inflater"))
                .addParameter(getParamSpec(VIEW_GROUP_TYPE,"container"))
                .addStatement("Class<?> targetClass = fragment.getClass()")
                .addStatement("String targetName = targetClass.getCanonicalName()")
                .addCode(codeBlockBuilder.build())
                .addStatement("throw new $T(\"Unable to binding for fragment\")"
                ,getTypeName("java.lang","RuntimeException"))
                .build();

        TypeSpec autoBinder = TypeSpec.classBuilder("AutoEFragmentBinder")
                .addModifiers(PUBLIC, FINAL)
                .addSuperinterface(getTypeName(EFRAGMENT_PACKAGE_NAME, E_FRAGMENT_BINDER))
                .addMethod(bind)
                .build();

        writeFile("com.example.nazmuddinmavliwala.emvp",autoBinder);
    }

    /**
     * @param annotatedClass object storing info of the class annotated with @BindFragment
     * @throws NoPackageNameException
     * @throws IOException
     * @throws ProcessingException
     */
    private void generateFragmentBindingClasses(
            FragmentAnnotatedClass annotatedClass)
            throws NoPackageNameException
            , IOException
            , ProcessingException {

        String annotatedClassName = annotatedClass
                .annotatedClassName;
        TypeElement typeElement = annotatedClass
                .typeElement;
        BindFragment bindFragment = typeElement
                .getAnnotation(BindFragment.class);

        // get the layout defined in @BindFragment annotation if
        // the layout value is -1 throw processing exception
        int layout = bindFragment.value();
        if(layout == -1) {
            return;
        }

        String bindingClassPackageName = getPackageName(typeElement);
        String bindingClassName = getBindingClassName(
                annotatedClass);
        TypeName bindingClassTypeName = getTypeName(
                bindingClassPackageName
                ,annotatedClassName);
        TypeName bindingSuperInterface = getTypeName(
                EFRAGMENT_PACKAGE_NAME
                ,EFRAGMENT_CLASS_NAME);
        TypeVariableName typeVariableName = getTypeVariableName(
                "T extends " + bindingClassTypeName);

        ParameterSpec fragSpec = getParamSpec(
                getTypeVariableName("T")
                ,"fragment");
        ParameterSpec layoutInflaterSpec = getParamSpec(
                INFLATER_TYPE
                ,"inflater");
        ParameterSpec viewGroupSpec = getParamSpec(
                VIEW_GROUP_TYPE
                ,"container");

        MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(PUBLIC)
                .addParameter(fragSpec)
                .addParameter(layoutInflaterSpec)
                .addParameter(viewGroupSpec)
                .addStatement("View view = inflater.inflate("+layout+", container, false)")
                .returns(VIEW_TYPE);

        List<? extends Element> elements = typeElement
                .getEnclosedElements();
        //iterate through all the annotated elements and generate
        // all the provider classes. Also simultaneously
        // construct fragment factory class
        int[] indicator = new int[]{0,0,0};
        for (Element element : elements) {
            indicator = processElements(
                    BindFragmentInteractor.class
                    ,indicator
                    ,element
                    ,bindBuilder);
        }

        for (Element element : elements) {
            indicator = processElements(
                    BindPresenter.class
                    ,indicator
                    ,element
                    ,bindBuilder);
        }

        for (Element element : elements) {
            indicator = processElements(
                    BindViewDelegate.class
                    ,indicator
                    ,element
                    ,bindBuilder);
        }

        bindBuilder.addStatement("return view");

        TypeSpec binderSpec = TypeSpec
                .classBuilder(bindingClassName)
                .addSuperinterface(bindingSuperInterface)
                .addModifiers(PUBLIC)
                .addTypeVariable(typeVariableName)
                .addMethod(bindBuilder.build())
                .build();

        writeFile(bindingClassPackageName,binderSpec);
    }

    private int[] processElements(
            Class<?> bindAnnotationClass
            , int[] indicator
            , Element element
            , MethodSpec.Builder bindBuilder)
            throws ProcessingException, NoPackageNameException {
        if(!(element instanceof VariableElement)) {
            return indicator;
        }
        VariableElement variableElement = (VariableElement) element;
        if(!isBindingAnnotationPresent(variableElement)) {
            return indicator;
        }
        ArgumentHolder holder = new ArgumentHolder(element);

        if (bindAnnotationClass.getSimpleName().equals(BindPresenter.class.getSimpleName())) {
            BindPresenter bindPresenter =
                    element.getAnnotation(BindPresenter.class);
            if (bindPresenter != null) {
                return processPresenter(indicator,bindBuilder,holder);
            }

        } else if (bindAnnotationClass.getSimpleName().equals(BindViewDelegate.class
                .getSimpleName())) {
            BindViewDelegate bindViewDelegate =
                    element.getAnnotation(BindViewDelegate.class);
            if (bindViewDelegate != null) {
                return processViewDelegate(indicator,bindBuilder,holder);
            }

        } else if (bindAnnotationClass.getSimpleName().equals(BindFragmentInteractor.class
                .getSimpleName())) {
            BindFragmentInteractor bindFragmentInteractor =
                    element.getAnnotation(BindFragmentInteractor.class);
            if (bindFragmentInteractor != null) {
                return processFragmentInteractor(indicator,bindBuilder,holder);
            }

        }
        return indicator;
    }

    private int[] processFragmentInteractor(
            int[] indicator
            , MethodSpec.Builder bindBuilder
            , ArgumentHolder holder)
            throws ProcessingException {
        if (indicator[2] == 0) {
            if(isValidFragmentInteractor(holder.fieldTypeElement)) {
                String fragmentInteractorStm = "fragment."
                        +holder.variableName
                        +" = ($T)fragment.getActivity()";
                bindBuilder.addStatement(
                        fragmentInteractorStm
                        ,holder.variableTypeName);
                indicator[2] = 1;
            } else {
                throw new ProcessingException();
            }
        } else {
            messager.printMessage(ERROR,"A fragment cannot" +
                    " have more than one fragmentInteractor");
            throw new ProcessingException();
        }
        return indicator;
    }

    private int[] processViewDelegate(
            int[] indicator
            , MethodSpec.Builder bindBuilder
            , ArgumentHolder holder)
            throws ProcessingException {
        if(indicator[1] == 0) {
            //validate field and generate view delegate provider
            if(isValidViewDelegate(holder.fieldTypeElement)) {
                String viewDelegateStatement = "fragment."
                        + holder.variableName
                        + " = new $T(view,fragment.getActivity(),fragment)";
                bindBuilder.addStatement(
                        viewDelegateStatement
                        ,holder.variableTypeName);
                indicator[1] = 1;
            } else {
                throw new ProcessingException();
            }
        } else {
            messager.printMessage(ERROR,"A fragment cannot " +
                    "have more than one viewDelegate");
            throw new ProcessingException();
        }
        return indicator;
    }

    private int[] processPresenter(
            int[] indicator
            ,MethodSpec.Builder bindBuilder
            ,ArgumentHolder holder)
            throws ProcessingException {
        if(indicator[0] == 0) {
            //validate field and generate presenter provider
            if(isValidPresenter(holder.fieldTypeElement)){
                String presenterStm = "fragment."
                        + holder.variableName
                        + " = new $T(fragment.getActivity()"
                        + ", fragment)";
                bindBuilder.addStatement(
                        presenterStm
                        ,holder.variableTypeName);
                indicator[0] = 1;
            } else {
                throw new ProcessingException();
            }
        } else {
            messager.printMessage(ERROR,"A fragment cannot" +
                    " have more than one presenter");
            throw new ProcessingException();
        }
        return indicator;
    }

    private boolean isValidFragmentInteractor(TypeElement fieldTypeElement) {
        if (!ClassValidator.isInterface(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " must be an interface";
            messager.printMessage(ERROR, message);
            return false;
        }

        if (!ClassValidator.isPublic(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " must be a public interface";
            messager.printMessage(ERROR, message);
            return false;
        }

        if(!ClassValidator.isFragmentInteractor(typeUtils,fieldTypeElement,FRAGMENT_INTERACTOR_TYPE)) {
            String message = String.format("@BindFragmentInteractor is only applicable"
                    + " to fragmentInteractor interface which extends BaseFragmentInteractor "
                    + "%s is not a valid fragmentInteractor"
                    , fieldTypeElement.getSimpleName().toString());
            messager.printMessage(ERROR, message);
            return false;
        }
        return true;
    }

    private boolean isValidViewDelegate(TypeElement fieldTypeElement) {
        if (!ClassValidator.isPublic(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " must be public class";
            messager.printMessage(ERROR, message);
            return false;
        }

        if (ClassValidator.isAbstract(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " cannot be abstract";
            messager.printMessage(ERROR, message);
            return false;
        }

        if (ClassValidator.isInterface(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " cannot be an interface";
            messager.printMessage(ERROR, message);
            return false;
        }

        if(!ClassValidator.isViewDelegate(typeUtils,fieldTypeElement, VIEW_DELEGATE_TYPE)) {
            String message = String.format("@BindViewDelegate is only applicable"
                    + " to ViewDelegate class which extends BaseEmployeeViewDelegate "
                    + "%s is not a valid viewDelegate"
                    , fieldTypeElement.getSimpleName().toString());
            messager.printMessage(ERROR, message);
            return false;
        }
        return true;
    }

    private boolean isValidPresenter(TypeElement fieldTypeElement) {
        if (!ClassValidator.isPublic(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " must be public class";
            messager.printMessage(ERROR, message);
            return false;
        }

        if (ClassValidator.isAbstract(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " cannot be abstract";
            messager.printMessage(ERROR, message);
            return false;
        }

        if (ClassValidator.isInterface(fieldTypeElement)) {
            String message = fieldTypeElement.getSimpleName().toString()
                    + " cannot be an interface";
            messager.printMessage(ERROR, message);
            return false;
        }

        if(!ClassValidator.isPresenter(typeUtils,fieldTypeElement,PRESENTER_TYPE)) {
            String message = String.format("@BindPresenter is only applicable"
                    + " to presenter class which extends BaseEmployeePresenter "
                    + "%s is not a valid presenter"
                    , fieldTypeElement.getSimpleName().toString());
            messager.printMessage(ERROR, message);
            return false;
        }
        return true;
    }

    private boolean isBindingAnnotationPresent(VariableElement variableElement) {
        return variableElement.getAnnotation(BindPresenter.class) != null
                || variableElement.getAnnotation(BindViewDelegate.class) != null
                || variableElement.getAnnotation(BindFragmentInteractor.class) != null;
    }

    private String getBindingClassName(FragmentAnnotatedClass annotatedClass) {
        return annotatedClass.annotatedClassName + "_FragmentBinding";
    }
}
