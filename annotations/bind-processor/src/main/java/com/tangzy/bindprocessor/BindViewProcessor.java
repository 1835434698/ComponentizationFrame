package com.tangzy.bindprocessor;

import com.tangzy.bindannotation.BindView;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
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
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementUtils;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();

    //初始化。可以得到ProcessingEnviroment，ProcessingEnviroment提供很多有用的工具类Elements, Types 和 Filer
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
        mMessager.printMessage(Diagnostic.Kind.NOTE, " mElementUtils = "+mElementUtils);
    }

    //指定这个注解处理器是注册给哪个注解的，这里说明是注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }

    //指定使用的Java版本，通常这里返回
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //可以在这里写扫描、评估和处理注解的代码，生成Java文件
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing.1..");
        mProxyMap.clear();
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        mMessager.printMessage(Diagnostic.Kind.NOTE, " elements.size = "+elements.size());
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE, " classElement = "+classElement+", fullClassName = "+fullClassName);
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElementUtils, classElement);
                mProxyMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindAnnotation.value();
            mMessager.printMessage(Diagnostic.Kind.NOTE, " id = "+id+", variableElement = "+variableElement);
            proxy.putElement(id, variableElement);
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, " mProxyMap.size = "+mProxyMap.size());
        //通过遍历mProxyMap，创建java文件
        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);
            try {
                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + proxyInfo.getProxyClassFullName() +", getTypeElement = " + proxyInfo.getTypeElement());
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + proxyInfo.getProxyClassFullName() + "error");
            }
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, "process finish ...");
        return true;
    }
}