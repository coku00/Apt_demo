package com.coku.compiler;

import com.coku.annotation.AutoRequest;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Type;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/16.
 * @email coku_lwp@126.com
 */

@AutoService(Processor.class)
public class AutoRequestProcessor extends AbstractProcessor {



    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    /**
     * 用于将创建的类写入到文件
     */
    private Filer mFiler;
    private Element element;


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

        // 获取所有被 @AutoRequest 注解的方法
        Set<? extends Element> elements = environment.getElementsAnnotatedWith(AutoRequest.class);

        Iterator<? extends Element> iterator = elements.iterator();

        List<MethodSpec> methodSpecList = new ArrayList<>();


        while (iterator.hasNext()){
            Element element = iterator.next();
            String methodName = element.getSimpleName().toString();
            // 创建一个方法，返回 List<Class>
            MethodSpec method = createMethodWithElements(element,methodName);
            methodSpecList.add(method);


        }

        // 创建一个类
        TypeSpec clazz = createClassWithMethod(methodSpecList);

        // 将这个类写入文件
        writeClassToFile(clazz);

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoRequest.class.getCanonicalName());
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
    private MethodSpec createMethodWithElements(Element element,String methodName) {


        // 因为 @Annotation 只能添加在方法上，所以这里直接强转为 MethodType
        Type.MethodType type = (Type.MethodType) element.asType();

        // getAllClasses 是生成的方法的名称
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);

        // public static
    //    builder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        builder.addModifiers(Modifier.PUBLIC);
//
//        // 定义返回值类型为 Set<Class>
//        ParameterizedTypeName returnType = ParameterizedTypeName.get(
//                ClassName.get(Set.class),
//                ClassName.get(Class.class)
//        );
      //  builder.returns(returnType);

        // 经过上面的步骤，
        // 我们得到了 public static Set<Class> getAllClasses() {} 这个方法,
        // 接下来我们实现它的方法体：

        // 方法中的第一行: Set<Class> set = new HashSet<>();
     //   builder.addStatement("$T<$T> set = new $T<>()", Set.class, Class.class, HashSet.class);

        int a = 0;

        for (Type t :type.argtypes) {
           System.out.println("t.asElement() = "+t.asElement().name);

          builder.addParameter(ParameterSpec.builder(ClassName.get(t),String.valueOf("var"+(++a)), new Modifier[]{}).build());


        }


        // 在我们创建的方法中，新增一行代码： set.add(XXX.class);
      //  builder.addStatement("set.add($T.class)", type);

        // 经过上面的 for 循环，我们就把所有添加了注解的类加入到 set 变量中了，
        // 最后，只需要把这个 set 作为返回值 return 就好了：
    //    builder.addStatement("return set");

        return builder.build();
    }

    /**
     * 创建一个类，并把参数中的方法加入到这个类中
     */
    private TypeSpec createClassWithMethod(List<MethodSpec>  methods) {
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
    private void writeClassToFile(TypeSpec clazz) {

        // 声明一个文件在 "com.coku.apt" 下
        JavaFile file = JavaFile.builder("com.coku.apt", clazz).build();

        // 写入文件
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
