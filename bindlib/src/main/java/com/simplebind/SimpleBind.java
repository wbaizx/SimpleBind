package com.simplebind;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleBind {
    public static void bind(Object o) {
        try {
            Class<?> aClass = Class.forName(o.getClass().getCanonicalName().substring(0, o.getClass().getCanonicalName().lastIndexOf("."))
                    + ".SimpleBind__" + o.getClass().getSimpleName());
            Constructor<?> constructor = aClass.getConstructor(o.getClass());
            constructor.newInstance(o);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
