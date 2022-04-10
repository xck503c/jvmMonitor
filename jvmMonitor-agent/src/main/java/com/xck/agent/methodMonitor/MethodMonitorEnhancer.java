package com.xck.agent.methodMonitor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.json.JSONUtil;
import com.xck.util.LogUtil;
import javassist.*;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodMonitorEnhancer implements ClassFileTransformer {

    private static Map<Class, ClassMethod> enhancerClasses = new HashMap<>();
    private static Map<Class, ClassMethod> waitEnhancerClasses = new HashMap<>();

    public final static MethodMonitorEnhancer INSTANCE = new MethodMonitorEnhancer();

    private MethodMonitorEnhancer() {
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // 如果该方法有依赖其他类，也会调用transform
        // 判断是否是等待增强的类
        if (waitEnhancerClasses == null || !waitEnhancerClasses.containsKey(classBeingRedefined)) {
            return null;
        }

        // 获取等待增强的方法
        Set<String> waitMethodName = waitEnhancerClasses.get(classBeingRedefined).getMethods();
        if (CollectionUtil.isEmpty(waitMethodName)) {
            LogUtil.info(classBeingRedefined + " method is empty");
            return null;
        }

        LogUtil.info("wait enhancer");

        className = className.replace("/", ".");
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = null;
        try {
            ctClass = pool.getCtClass(className);
        } catch (NotFoundException e) {
            LogUtil.error("find class error", e);
            throw new IllegalClassFormatException(ExceptionUtil.getRootCauseMessage(e));
        }

        LogUtil.info("find class");

        Set<String> successMethodNames = new HashSet<>();
        for (String methodName : waitMethodName) {
            try {
                CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
                MethodInfo methodInfo = ctMethod.getMethodInfo();
                LocalVariableAttribute attribute = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute(LocalVariableAttribute.tag);
                if (attribute != null) {
                    boolean isStatic = Modifier.isStatic(ctMethod.getModifiers());
                    //这里如果不加，会说找不到变量
                    ctMethod.addLocalVariable("_monitorStart", CtClass.longType);
                    ctMethod.addLocalVariable("_monitorDiff", CtClass.longType);
                    ctMethod.insertBefore("_monitorStart = System.currentTimeMillis();");
                    ctMethod.insertBefore(MethodInvokeListenerManager
                            .beforeListenerStr(className, methodName, isStatic));
                    ctMethod.insertAfter("_monitorDiff = System.currentTimeMillis() - _monitorStart;");
                    ctMethod.insertAfter(MethodInvokeListenerManager
                            .afterListenerStr(className, methodName, isStatic));
                    CtClass etype = ClassPool.getDefault().get("java.lang.Throwable");
                    ctMethod.addCatch(MethodInvokeListenerManager
                            .registerTryCatchListenerStr(className, methodName, isStatic), etype);

                    successMethodNames.add(methodName);
                }
            } catch (Exception e) {
                LogUtil.error("enhance retransformClasses error, class=" + className + ", methodName=" + methodName, e);
                throw new IllegalClassFormatException(ExceptionUtil.getRootCauseMessage(e));
            }
        }
        try {
            byte[] ctClassBytes = ctClass.toBytecode();

            //成功增强
            ClassMethod successEnhancerClass = enhancerClasses.get(classBeingRedefined);
            if (successEnhancerClass == null) {
                enhancerClasses.put(classBeingRedefined, successEnhancerClass = new ClassMethod(classBeingRedefined));
            }
            for (String sucMethod : successMethodNames) {
                successEnhancerClass.addMethodName(sucMethod);
                successEnhancerClass.addUseCount(); //成功增强，增加引用计数
            }

            LogUtil.info("enhance retransformClasses success, class=" + className
                    + ", useCount=" + successEnhancerClass.getCount());

            waitEnhancerClasses.remove(classBeingRedefined);
            return ctClassBytes;
        } catch (Exception e) {
            LogUtil.error("enhance retransformClasses eror", e);
            throw new IllegalClassFormatException(ExceptionUtil.getRootCauseMessage(e));
        }
    }

    public static synchronized void enhance(final Instrumentation inst, Set<ClassMethod> newClass)
            throws UnmodifiableClassException, ClassNotFoundException{
        LogUtil.info("wait enhance " + JSONUtil.toJsonStr(newClass));
        for (ClassMethod newClassMethod : newClass) {
            //判断是否存在
            Class c = Class.forName(newClassMethod.getClassName());
            //判断待增强的方法集合是否为空
            Set<String> waitEnhanceMethods = newClassMethod.getMethods();
            if (CollectionUtil.isEmpty(waitEnhanceMethods)) {
                continue;
            }

            //获取已经增强class
            ClassMethod enhanceClass = enhancerClasses.get(c);
            for (String waitEnhanceMethod : waitEnhanceMethods) {
                //已经增强的，则跳过
                if (enhanceClass != null && enhanceClass.isContainsMethodName(waitEnhanceMethod)) {
                    enhanceClass.addUseCount(); //已经成功增强，增加引用计数
                    continue;
                }

                //成功放入待增强集合
                ClassMethod waitEnhanceClass = waitEnhancerClasses.get(c);
                if (waitEnhanceClass == null) {
                    LogUtil.info("put class=" + c);
                    waitEnhancerClasses.put(c, waitEnhanceClass = new ClassMethod(c));
                }
                waitEnhanceClass.addMethodName(waitEnhanceMethod);
            }

            if (waitEnhancerClasses.size() > 0) {
                LogUtil.info("start enhancer");
                inst.retransformClasses(c);
                LogUtil.info("end enhancer");
            }
        }

        waitEnhancerClasses.clear(); //清空
    }

    /**
     * 移除所有
     *
     * @param inst
     */
    public static synchronized void resetAll(final Instrumentation inst) {
//        try {
//            reset(inst, new HashSet<>(enhancerClasses.values()));
//        } catch (UnmodifiableClassException e) {
//        }
//        enhancerClasses.clear();
        waitEnhancerClasses.clear();
    }

    /**
     * 清除增强内容(该方法还是有点问题)
     *
     * @param inst
     * @param resetClassMethods
     * @throws UnmodifiableClassException
     */
    public static synchronized void reset(final Instrumentation inst, Set<ClassMethod> resetClassMethods)
            throws UnmodifiableClassException {
        final ClassFileTransformer resetClassFileTransformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                return null;
            }
        };

        Class[] classArray = new Class[resetClassMethods.size()];
        int i = 0;
        for (ClassMethod resetClassMethod : resetClassMethods) {
            //判断是否存在
            Class c = null;
            try {
                c = Class.forName(resetClassMethod.getClassName());
            } catch (ClassNotFoundException e) {
                continue;
            }

            //获取已经增强class
            ClassMethod enhanceClass = enhancerClasses.get(c);
            if (enhanceClass == null) {
                continue; //没有被增强，跳过
            }

            //获取该类的所有方法名
            Set<String> classMethods = ClassUtil.getDeclaredMethodNames(c);
            for (String resetMethod : resetClassMethod.getMethods()) {
                //如果不包含改方法，则跳过
                if (!classMethods.contains(resetMethod)) continue;
                //未被增强的，则跳过
                if (!enhanceClass.isContainsMethodName(resetMethod)) continue;

                if (!enhanceClass.isCountZero()) {
                    enhanceClass.decrUseCount(); //已经成功增强，扣除引用计数
                    LogUtil.info("reset retransformClasses decrUseCount class=" + c.getName()
                            + ", useCount=" + enhanceClass.getCount());
                }
            }

            if (enhanceClass.isCountZero()) {
                classArray[i++] = c;
            }

        }

        if (i > 0) {
            try {
                inst.addTransformer(resetClassFileTransformer);
                inst.retransformClasses(classArray);

                for (Class clzz : classArray) {
                    enhancerClasses.remove(clzz);
                    LogUtil.info("reset retransformClasses success class=" + clzz.getName());
                }
            } finally {
                inst.removeTransformer(resetClassFileTransformer);
            }
        }
    }
}
