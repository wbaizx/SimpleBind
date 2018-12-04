package com.simplebind;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleBind {
    public static void bind(Activity activity) {
        creatClass(activity, activity.getWindow().getDecorView());
    }

    public static void bind(Fragment fragment, View view) {
        creatClass(fragment, view);
    }

    private static void creatClass(Object target, View view) {
        StringBuilder createActivityclassName = new StringBuilder(target.getClass().getCanonicalName());
        createActivityclassName.insert(createActivityclassName.lastIndexOf(".") + 1, "SimpleBind__");
        try {
            Class<?> aClass = Class.forName(createActivityclassName.toString());
            Constructor<?> constructor = aClass.getConstructor(target.getClass(), View.class);
            constructor.newInstance(target, view);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
