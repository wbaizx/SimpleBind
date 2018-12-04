package com.bindcompiler;

import com.squareup.javapoet.ClassName;
import com.sun.tools.javac.code.Symbol;

public class Id {
    private ClassName R;
    private String name;
    private int value;

    protected Id(int value, Symbol symbol) {
        this.R = ClassName.get(symbol.packge().getQualifiedName().toString(), "R");
        this.name = symbol.getSimpleName().toString();
        this.value = value;
    }

    protected ClassName getR() {
        return R;
    }

    protected String getName() {
        return name;
    }

    protected int getValue() {
        return value;
    }
}
