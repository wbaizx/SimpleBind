package com.simplebind;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.simplebind.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class BindProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(te);
            for (Element e : elementsAnnotatedWith) {
                ElementKind kind = e.getKind();
                // 判断该元素是否为变量
                if (kind.isField()) {
                    // 包裹注解元素的元素, 也就是其父元素, 比如注解了成员变量或者成员函数, 其上层就是该类
                    Element enclosingElement = e.getEnclosingElement();
                    // 获取父元素的全类名, 用来生成包名
                    String enclosingQualifiedName;
                    if (enclosingElement instanceof PackageElement) {
                        enclosingQualifiedName = ((PackageElement) enclosingElement).getQualifiedName().toString();
                    } else {
                        enclosingQualifiedName = ((TypeElement) enclosingElement).getQualifiedName().toString();
                    }
                    // 生成的包名
                    String genaratePackageName = enclosingQualifiedName.substring(0, enclosingQualifiedName.lastIndexOf('.'));
                    // 生成的类名
                    String genarateClassName = "SimpleBind__" + enclosingElement.getSimpleName();
                    try {
                        // 创建Java文件
                        JavaFileObject f = processingEnv.getFiler().createSourceFile(genarateClassName);
                        Writer w = f.openWriter();
                        try {
                            PrintWriter pw = new PrintWriter(w);
                            pw.println("package " + genaratePackageName + ";\n" +
                                    "import android.view.View;\n" +
                                    "import " + genaratePackageName + ".R;\n" +
                                    "public class " + genarateClassName + " {\n" +
                                    "    public " + genarateClassName + "(" + enclosingElement.getSimpleName() + " activity){\n" +
                                    "        View view = activity.getWindow().getDecorView();\n" +
                                    "        activity." + e.getSimpleName() + "=view.findViewById(R.id." + e.getSimpleName() + ");\n" +
                                    "    }\n" +
                                    "}");
                            pw.flush();
                        } finally {
                            w.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
}
