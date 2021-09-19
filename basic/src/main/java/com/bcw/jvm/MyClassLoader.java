package com.bcw.jvm;


import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader {

    public static void main(String[] args) throws Exception {
        final String className = "Hello";
        final String methodName = "hello";

        ClassLoader classLoader = new MyClassLoader();
        Class<?> clazz = classLoader.loadClass(className);
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(clazz.getSimpleName() + "." + method.getName());
        }
        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method method = clazz.getMethod(methodName);
        method.invoke(instance);
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println(name);
        String resourcePath = name.replace(".", "/");
        final String suffix = ".xlass";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath + suffix);

        try {
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            byte[] classBytes = decode(bytes);
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            close(inputStream);
        }
    }

    private static byte[] decode(byte[] byteArray) {
        byte[] bytes = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            bytes[i] = (byte) (255 - byteArray[i]);
        }
        return bytes;
    }

    private static void close(Closeable res) {
        if (null != res) {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
