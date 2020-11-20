package com.coku.compiler;

import com.coku.annotation.AutoInject;
import com.coku.annotation.AutoRequest;

import com.coku.lib.AutoRequestCall;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/16.
 * @email coku_lwp@126.com
 */

@AutoService(Processor.class)
public class AutoRequestProcessor extends AbstractProcessor {




    private Map<String,List<Element>> methodElementMap = new HashMap<>();

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 用于将创建的类写入到文件
     */
    private Filer mFiler;
    private Elements elements;
    private  Messager messager;

    /* ======================================================= */
    /* Override/Implements Methods                             */
    /* ======================================================= */

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        mFiler = environment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {

        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "start:");


         elements = processingEnv.getElementUtils();



        Set<? extends Element> methodElements = environment.getElementsAnnotatedWith(AutoRequest.class);

        createMethodWithElement(methodElements, environment);

        Set<? extends Element> classElements = environment.getElementsAnnotatedWith(AutoInject.class);

        createClassWithElement(classElements, environment);

        return false;
    }

    private void createMethodWithElement(Set<? extends Element> methodElements, RoundEnvironment environment) {


        Iterator<? extends Element> methodIterator = methodElements.iterator();

        while (methodIterator.hasNext()) {
            Element methodElement = methodIterator.next();
            AutoRequest autoRequest = methodElement.getAnnotation(AutoRequest.class);
            List<Element> list = methodElementMap.get(autoRequest.value());
            if(list == null){
                list = new ArrayList<>();
                methodElementMap.put(autoRequest.value(),list);
            }
            list.add(methodElement);
        }


    }

    private void createClassWithElement(Set<? extends Element> classElements, RoundEnvironment environment) {
        Iterator<? extends Element> classIterator = classElements.iterator();

        while (classIterator.hasNext()) {
            Element classElement = classIterator.next();

            Type.ClassType classType = (Type.ClassType) classElement.asType();
            //原类名
            String originalClassName = classType.tsym.name.toString();

            String packageName = classType.tsym.toString().split("." + originalClassName)[0];


            buildInterface(packageName,originalClassName);


            // 定义一个名字叫 xxx 的类
            TypeSpec.Builder autoRequestService = TypeSpec.classBuilder("$" + originalClassName);
            // 声明为 public 的
            autoRequestService.addModifiers(Modifier.PUBLIC);
            autoRequestService.addModifiers(Modifier.ABSTRACT);
            // 为这个类加入一段注释
            autoRequestService.addJavadoc("Automatically generated file. DO NOT MODIFY");


            autoRequestService.addField(Retrofit.class,"retrofit",new Modifier[]{Modifier.PRIVATE});

            List<Element> list = methodElementMap.get(packageName +"."+ originalClassName);
            if(list != null){

                List<MethodSpec> methods = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    Element element =  list.get(i);
                    String methodName = element.getSimpleName().toString();

                    // 创建一个方法，返回 List<Class>
                    MethodSpec method = createMethodWithElements(element, methodName,classType);
                    methods.add(method);
                }

                autoRequestService.addMethods(methods);

                MethodSpec.Builder builder = MethodSpec.methodBuilder("initRetrofit");
                builder.addModifiers(Modifier.PUBLIC);




                TypeName typeName = ParameterizedTypeName.get(Retrofit.class);

                builder.addParameter(typeName,"retrofit",new Modifier[]{});

                builder.addStatement("this.retrofit = retrofit");
//
                autoRequestService.addMethod(builder.build());


//                public <T> ObservableTransformer<T, T> transformer() {
//                    return new ObservableTransformer<T, T>() {
//                        @Override
//                        public ObservableSource<T> apply(Observable<T> upstream) {
//                            return upstream.subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread());
//                        }
//                    };
//                }


            }else{
                messager.printMessage(Diagnostic.Kind.NOTE, "list == null:");
            }



            TypeSpec clazzSpec = autoRequestService.build();

            // 将这个类写入文件
            writeClassToFile(clazzSpec, packageName);


        }


    }

    private void buildInterface(String packageName,String originalClassName) {
        TypeSpec.Builder interfaceBuilder =  TypeSpec.interfaceBuilder("I"+originalClassName);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("provideRetrofit");
        builder.addModifiers(Modifier.PUBLIC);
        builder.addModifiers(Modifier.ABSTRACT);
        builder.returns(ParameterizedTypeName.get(Retrofit.class));

        interfaceBuilder.addMethod(builder.build());
        // 声明一个文件在 "com.coku.apt" 下
        JavaFile file = JavaFile.builder(packageName, interfaceBuilder.build()).build();
        // 写入文件
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set set = new HashSet();
        set.add(AutoRequest.class.getCanonicalName());
        set.add(AutoInject.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }




    /* ======================================================= */
    /* Private Methods                                         */
    /* ======================================================= */

    /**
     * 创建一个方法，这个方法返回参数中的所有类信息。
     */
    private MethodSpec createMethodWithElements(Element element, String methodName,Type.ClassType classType) {

        // 因为 @Annotation 只能添加在方法上，所以这里直接强转为 MethodType
        Type.MethodType type = (Type.MethodType) element.asType();

        // getAllClasses 是生成的方法的名称
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);

        builder.addModifiers(Modifier.PUBLIC);


        int a = 0;

        for (Type t : type.argtypes) {
            messager.printMessage(Diagnostic.Kind.NOTE, "arg = "+t.tsym);

            builder.addParameter(ParameterSpec.builder(ClassName.get(t), String.valueOf("var" + (a++)), new Modifier[]{}).build());

        }

        System.out.println("====="+type.getReturnType().allparams());


        TypeName typeName = ParameterizedTypeName.get(AutoRequestCall.class);


        builder.addParameter(typeName,String.valueOf("var" + (a++)), new Modifier[]{}).build();

        builder.addStatement("$T observable = retrofit.create("+classType.tsym.getSimpleName()+".class)."+methodName+"("+buildArgs(type.argtypes)+")",type.getReturnType());




        return builder.build();
    }

    private String buildArgs(com.sun.tools.javac.util.List<Type> argtypes) {
        StringBuilder sb = new StringBuilder();


        int a = 0;

        for (int i = 0; i <argtypes.size();i++) {

            sb.append("var").append(a++);

            if(i != argtypes.size() - 1){
                sb.append(",") ;
            }

        }

        return sb.toString();
    }


    /**
     * 创建一个类，并把参数中的方法加入到这个类中
     */
    private TypeSpec createClassWithMethod(List<MethodSpec> methods) {
        // 定义一个名字叫 OurClass 的类
        TypeSpec.Builder autoRequestService = TypeSpec.classBuilder("AutoRequestService");
        // 声明为 public 的
        autoRequestService.addModifiers(Modifier.PUBLIC);
        // 为这个类加入一段注释
        autoRequestService.addJavadoc("Automatically generated file. DO NOT MODIFY");

        autoRequestService.addMethods(methods);

        return autoRequestService.build();
    }

    /**
     * 将一个创建好的类写入到文件中参与编译
     */
    private void writeClassToFile(TypeSpec clazz, String packageName) {

        // 声明一个文件在 "com.coku.apt" 下
        JavaFile file = JavaFile.builder(packageName, clazz).build();

        // 写入文件
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
