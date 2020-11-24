package com.coku.compiler;

import com.coku.annotation.AutoClass;
import com.coku.annotation.AutoMethod;

import com.coku.lib.BaseService;
import com.coku.lib.TargetObserver;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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
import javax.tools.Diagnostic;

import retrofit2.Retrofit;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/16.
 * @email coku_lwp@126.com
 *
 * https://juejin.cn/post/6844903456629587976
 * https://www.coder.work/article/2818864
 */

@AutoService(Processor.class)
public class AutoRequestProcessor extends AbstractProcessor {




    private Map<String,List<Element>> methodElementMap = new HashMap<>();
    private Map<String,TypeSpec> clazzInterface = new HashMap<>();
    private Filer mFiler;
    private  Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        mFiler = environment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {

        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "start:");


        Set<? extends Element> methodElements = environment.getElementsAnnotatedWith(AutoMethod.class);

        createMethodWithElement(methodElements, environment);

        Set<? extends Element> classElements = environment.getElementsAnnotatedWith(AutoClass.class);

        createDisPatchInterfaceWithElement(classElements, environment);

        createServiceClassWithElement(classElements, environment);

        return false;
    }

    private void createDisPatchInterfaceWithElement(Set<? extends Element> classElements, RoundEnvironment environment) {


        Iterator<? extends Element> classIterator = classElements.iterator();

        while (classIterator.hasNext()) {
            Element classElement = classIterator.next();

            Type.ClassType classType = (Type.ClassType) classElement.asType();
            //原类名
            String originalClassName = classType.tsym.name.toString();

            String packageName = classType.tsym.toString().split("." + originalClassName)[0];

            // 定义一个名字叫 xxx 的类
            TypeSpec.Builder disPath = TypeSpec.interfaceBuilder("$" + originalClassName + "DisPatch");
            // 声明为 public 的
            disPath.addModifiers(Modifier.PUBLIC);
            // 为这个类加入一段注释
            disPath.addJavadoc("自动生成的接口，请勿更改");
            List<Element> list = methodElementMap.get(packageName +"."+ originalClassName);
            if(list != null){
                List<MethodSpec> methods = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {
                    Element element =  list.get(i);
                    String methodName = element.getSimpleName().toString();

                    MethodSpec method = createDisPatchMethodWithElements(element, methodName,classType);
                    methods.add(method);
                }

                disPath.addMethods(methods);


            }else{
                messager.printMessage(Diagnostic.Kind.NOTE, "list == null:");
            }


            TypeSpec clazzSpec = disPath.build();

            clazzInterface.put(packageName +"."+ originalClassName,clazzSpec);

            // 将这个类写入文件
            writeClassToFile(clazzSpec, packageName);

        }




    }

    private MethodSpec createDisPatchMethodWithElements(Element element, String methodName, Type.ClassType classType) {
        // 因为 @Annotation 只能添加在方法上，所以这里直接强转为 MethodType
        Type.MethodType type = (Type.MethodType) element.asType();

        // getAllClasses 是生成的方法的名称
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);

        builder.addModifiers(Modifier.PUBLIC);
        builder.addModifiers(Modifier.ABSTRACT);

        builder.returns(ParameterizedTypeName.get(TargetObserver.class));


        int var = 0;

        ClassName className =  ClassName.get("com.coku.lib","RequestCallback");
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(className,
                ParameterizedTypeName.get(type.getReturnType().getTypeArguments().get(0)));


        builder.addParameter(parameterizedTypeName,String.valueOf("var" + (++var)), new Modifier[]{}).build();


        return builder.build();
    }

    private void createMethodWithElement(Set<? extends Element> methodElements, RoundEnvironment environment) {


        Iterator<? extends Element> methodIterator = methodElements.iterator();

        while (methodIterator.hasNext()) {
            Element methodElement = methodIterator.next();
            AutoMethod autoRequest = methodElement.getAnnotation(AutoMethod.class);
            List<Element> list = methodElementMap.get(autoRequest.className());
            if(list == null){
                list = new ArrayList<>();
                methodElementMap.put(autoRequest.className(),list);
            }
            list.add(methodElement);
        }


    }

    private void createServiceClassWithElement(Set<? extends Element> classElements, RoundEnvironment environment) {
        Iterator<? extends Element> classIterator = classElements.iterator();

        while (classIterator.hasNext()) {
            Element classElement = classIterator.next();

            Type.ClassType classType = (Type.ClassType) classElement.asType();
            //原类名
            String originalClassName = classType.tsym.name.toString();

            String packageName = classType.tsym.toString().split("." + originalClassName)[0];



            // 定义一个名字叫 xxx 的类
            TypeSpec.Builder autoRequestService = TypeSpec.classBuilder("$" + originalClassName+"Service");
            // 声明为 public 的
            autoRequestService.addModifiers(Modifier.PUBLIC);
            autoRequestService.superclass(BaseService.class);
            // 为这个类加入一段注释
            autoRequestService.addJavadoc("自动生成的类，请勿修改");

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


                ClassName className = ClassName.get(packageName,"$" + originalClassName + "DisPatch");

                autoRequestService.addField(className,"disPatch", Modifier.PRIVATE);


                MethodSpec methodSpec = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(Retrofit.class),"retrofit")
                        .addParameter(className,"disPatch")
                        .addStatement("super(retrofit)")
                        .addStatement("this.disPatch = disPatch")
                        .build();

                autoRequestService.addMethod(methodSpec);

            }else{
                messager.printMessage(Diagnostic.Kind.NOTE, "list == null:");
            }



            TypeSpec clazzSpec = autoRequestService.build();

            // 将这个类写入文件
            writeClassToFile(clazzSpec, packageName);


        }


    }




    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set set = new HashSet();
        set.add(AutoMethod.class.getCanonicalName());
        set.add(AutoClass.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }





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

            builder.addParameter(ParameterSpec.builder(ClassName.get(t), String.valueOf("var" + (++a)), new Modifier[]{}).build());

        }

        //getActualTypeArguments

        Type.ClassType returnClassType = (Type.ClassType) type.getReturnType();


        Type t = returnClassType.getTypeArguments().get(0);

        messager.printMessage(Diagnostic.Kind.NOTE,"ClassName  t = " +  t);
        if(!t.getTypeArguments().isEmpty()){
            messager.printMessage(Diagnostic.Kind.NOTE,"ClassName  ... = " +  t.getTypeArguments());
        }



//        String s = returnClassType.toString().split("io.reactivex.Observable")[1];
//        s = s.substring(1,s.length() - 1);




        try{
       //    messager.printMessage(Diagnostic.Kind.NOTE,"ClassName = " +  s);
         //   messager.printMessage(Diagnostic.Kind.NOTE,"returnClassType = " + s);

        }catch (Exception e){
            e.printStackTrace();
        }




        ClassName className =  ClassName.get("com.coku.lib","RequestCallback");

        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(className,
                ParameterizedTypeName.get(t));

        builder.addParameter(parameterizedTypeName,String.valueOf("var" + (++a)), new Modifier[]{}).build();
        builder.beginControlFlow("if(disPatch != null && getRetrofit() != null)");
        builder.addStatement("addSubscribe(" +
                "getRetrofit()" +"\n"+
                ".create("+classType.tsym.getSimpleName()+".class)" +"\n"+
                "."+ methodName + "("+buildArgs(type.argtypes)+")" +"\n"+
                ".compose(transformer())" +"\n"+
                ".subscribeWith(" +
                "this.disPatch" +
                "." + methodName+"(var"+ a +")))");
        builder.endControlFlow();

        return builder.build();
    }

    private String buildArgs(com.sun.tools.javac.util.List<Type> argtypes) {
        StringBuilder sb = new StringBuilder();


        int var = 0;

        for (int i = 0; i <argtypes.size();i++) {

            sb.append("var").append(++var);

            if(i != argtypes.size() - 1){
                sb.append(",") ;
            }

        }

        return sb.toString();
    }


    /**
     * 将一个创建好的类写入到文件中参与编译
     */
    private void writeClassToFile(TypeSpec clazz, String packageName) {

        JavaFile file = JavaFile.builder(packageName, clazz).build();

        // 写入文件
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
