package com.xck.agent.methodMonitor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 类方法
 *
 * @author xuchengkun
 * @date 2022/04/04 17:09
 **/
public class ClassMethod {

    private String className;
    private Class clzz;
    private Set<String> methods;
    private int count = 0; //引用计数

    public ClassMethod(String className) {
        this.className = className;
        this.methods = new HashSet<>();
    }

    public ClassMethod(Class clzz) {
        this.clzz = clzz;
        this.className = clzz.getName();
        this.methods = new HashSet<>();
    }

    public void addMethodName(String methodName) {
        methods.add(methodName);
    }

    public boolean isContainsMethodName(String methodName) {
        return methods.contains(methodName);
    }

    public void addUseCount() {
        ++count;
    }

    public void decrUseCount() {
        --count;
    }

    public boolean isCountZero() {
        return count == 0;
    }

    public Class getClzz() {
        return clzz;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassMethod that = (ClassMethod) o;
        return Objects.equals(clzz, that.clzz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clzz);
    }
}
