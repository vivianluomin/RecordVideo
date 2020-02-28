package com.example.asus1.videorecoder.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args){
        try {
            Class cl = A.class;
            Constructor constructor = cl.getDeclaredConstructor(long.class);
            constructor.setAccessible(true);
            A a = (A)constructor.newInstance(1);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

    }
}
