package com.robert.tom;

public class JniSample {

    public native int sayHellofromC();

    public static void main(String[] args) {

        System.loadLibrary("JniSample");

        JniSample s = new JniSample();
        s.sayHellofromC();
    }
}
