package com.bindcompiler;

import com.simpleannotation.BindOnClick;
import com.simpleannotation.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * 编译时注解处理器
 * <p>
 * 这两个注解和下面的getSupportedAnnotationTypes(), getSupportedSourceVersion()方法作用相同
 *
 * @SupportedAnnotationTypes("com.simplebind.BindView")
 * @SupportedSourceVersion(SourceVersion.RELEASE_7)
 */

public class BindProcessor extends AbstractProcessor {
    private final RScanner rScanner = new RScanner();
    private Trees trees;

    //要导入的头文件
    private final ClassName VIEW = ClassName.get("android.view", "View");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        trees = Trees.instance(processingEnv);
    }

    /**
     * 需要处理的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(BindOnClick.class.getCanonicalName());
        return types;
    }

    /**
     * 默认编译版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    /**
     * 在build中打印log
     */
    private void log(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //数据结构  map<父类全名，list<元素>>
        LinkedHashMap<String, ArrayList<Element>> map = new LinkedHashMap<>();

        addFieldMap(map, roundEnv, BindView.class);
        addFieldMap(map, roundEnv, BindOnClick.class);

        for (String parentQualifiedName : map.keySet()) {
            writeJavaFile(parentQualifiedName, map.get(parentQualifiedName));
        }

        return true;
    }

    /**
     * 将对应注解的元素（变量）添加到map
     */
    private void addFieldMap(LinkedHashMap<String, ArrayList<Element>> map, RoundEnvironment roundEnv, Class<? extends Annotation> aClass) {
        for (Element element : roundEnv.getElementsAnnotatedWith(aClass)) {
            //包裹注解元素的元素,也就是其父元素,该注解注解了成员变量,其上层就是该类
            TypeElement parentElement = (TypeElement) element.getEnclosingElement();
            //父类全名
            String parentQualifiedName = parentElement.getQualifiedName().toString();
            if (map.containsKey(parentQualifiedName)) {
                map.get(parentQualifiedName).add(element);
            } else {
                ArrayList<Element> list = new ArrayList<>();
                list.add(element);
                map.put(parentQualifiedName, list);
            }
        }
    }

    /**
     * 创建java文件
     */
    private void writeJavaFile(String parentQualifiedName, ArrayList<Element> elements) {
        //原类名
        String className = parentQualifiedName.substring(parentQualifiedName.lastIndexOf('.') + 1, parentQualifiedName.length());
        //生成的包名
        String createPackageName = parentQualifiedName.substring(0, parentQualifiedName.lastIndexOf('.'));
        //生成的类名
        String createClassName = "SimpleBind__" + className;

        //要导入的头文件
        ClassName TARGET = ClassName.get("", className);

        MethodSpec.Builder method = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TARGET, "target",Modifier.FINAL)
                .addParameter(VIEW, "view");

        //创建 BindView 相关代码在构造方法中
        setBindViewCode(method, elements);
        //创建 BindOnClick 相关代码在构造方法中
        setBindOnClickCode(method, elements);

        TypeSpec typeSpec = TypeSpec.classBuilder(createClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(method.build())
                .build();
        JavaFile javaFile = JavaFile.builder(createPackageName, typeSpec)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBindViewCode(MethodSpec.Builder method, ArrayList<Element> list) {
        for (Element element : list) {
            BindView annotation = element.getAnnotation(BindView.class);
            if (annotation != null) {
                HashMap<Integer, Id> map = elementToId(element, BindView.class);
                if (map != null) {
                    Id id = map.get(annotation.value());
                    method.addStatement("target.$L = view.findViewById($T.id.$L)",
                            element.getSimpleName(), id.getR(), id.getName());
                }
            }
        }
    }

    private void setBindOnClickCode(MethodSpec.Builder method, ArrayList<Element> list) {
        for (Element element : list) {
            BindOnClick annotation = element.getAnnotation(BindOnClick.class);
            if (annotation != null) {
                HashMap<Integer, Id> map = elementToId(element, BindOnClick.class);
                if (map != null) {
                    for (int val : annotation.value()) {
                        Id id = map.get(val);
                        method.addStatement("view.findViewById($T.id.$L).setOnClickListener(new $T.OnClickListener() {\n" +
                                "@$T\n" +
                                "public void onClick($T v) {\n" +
                                "     target.$L(v);\n" +
                                "  }\n" +
                                "})",
                                id.getR(), id.getName(), VIEW, Override.class, VIEW, element.getSimpleName());
                    }
                }
            }
        }
    }

    private HashMap<Integer, Id> elementToId(Element element, Class<? extends Annotation> annotation) {
        HashMap<Integer, Id> map = null;
        rScanner.clearMap();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                JCTree tree = (JCTree) trees.getTree(element, annotationMirror);
                if (tree != null) {
                    tree.accept(rScanner);
                    map = rScanner.getMap();
                }
            }
        }
        return map;
    }

    private class RScanner extends TreeScanner {
        private HashMap<Integer, Id> map = new HashMap<>();

        @Override
        public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            Symbol symbol = jcFieldAccess.sym;
            if (symbol.getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
                int value = (Integer) ((Symbol.VarSymbol) symbol).getConstantValue();
                map.put(value, new Id(value, symbol));
            }
        }

        private HashMap<Integer, Id> getMap() {
            return map;
        }

        private void clearMap() {
            this.map.clear();
        }
    }
}
