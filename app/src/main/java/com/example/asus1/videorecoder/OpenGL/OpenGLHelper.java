package com.example.asus1.videorecoder.OpenGL;

public class OpenGLHelper {

    static {
        System.loadLibrary("OpenGLHelper");
    }

    public native long initOpenGL();
}
