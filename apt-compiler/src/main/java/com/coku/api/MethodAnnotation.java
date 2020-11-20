package com.coku.api;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.sun.tools.javac.code.Type;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

public class MethodAnnotation {
    //方法名
    private String methodName;
    //参数类型
    private List<Type> argtypes;

    //元注解
    private Type.MethodType methodType;
    //返回值类型
    private Type returnType;

    private Type receiverType;

    public MethodAnnotation(Element element) {
        this.methodType = (Type.MethodType) element.asType();
        this.methodName = element.getSimpleName().toString();
        this.argtypes = methodType.argtypes;
        this.returnType = methodType.getReturnType();
        this.receiverType = methodType.getReceiverType();
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Type> getArgTypes() {
        return argtypes;
    }

    public Type.MethodType getMethodType() {
        return methodType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Type getReceiverType() {
        return receiverType;
    }

    @Override
    public String toString() {
        return "MethodAnnotation{" +
                "methodName='" + methodName + '\'' +
                ", argtypes=" + argtypes +
                ", methodType=" + methodType +
                ", returnType=" + returnType +
                ", receiverType=" + receiverType +
                '}';
    }


    public MethodSpec buildMethod(){

        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodName);
        builder.addModifiers(Modifier.PUBLIC);

        int a = 0;

        for (Type t : argtypes) {
            builder.addParameter(ParameterSpec.builder(ClassName.get(t), String.valueOf("var" + (a++)), new Modifier[]{}).build());
        }



     // builder.addStatement("$T observable = retrofit.create("+classType.tsym.getSimpleName()+".class)."+methodName+"("+buildArgs(type.argtypes)+")",type.getReturnType());

//       if(returnType != null){
//           builder.returns((java.lang.reflect.Type) returnType);
//       }

        return builder.build();
    }



    private String buildArgs() {
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
}
