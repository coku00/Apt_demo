package com.coku.api;

import com.sun.tools.javac.code.Type;

import java.util.List;

import javax.lang.model.element.Element;

public class ClassAnnotation {

    //类名
    private String className;
    //包名
    private String packageName;

    //元注解
    private Type.ClassType classType;


    public ClassAnnotation(Element classElement){
        this.classType = (Type.ClassType) classElement.asType();
        this.className = classType.tsym.getSimpleName().toString();
        this.packageName = classType.tsym.toString().split("."+className)[0];
    }

    @Override
    public String toString() {
        return "ClassAnnotation{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", classType=" + classType +
                '}';
    }
}
