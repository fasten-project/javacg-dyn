package org.test.annotated.app;

import org.test.annotated.common.AppInterface;
import org.test.annotated.dep1.InterfaceImplementor;
import org.test.annotated.dep1.Dep1;

public class App {

    public static void main(String[] args) {
        //org.test.annotated.dep1/Dep1.source()void
        Dep1.source();
        //org.test.annotated.app/App.<init>()void
        App app = new App();
        //org.test.annotated.app/App.m1()void
        app.m1();
        //org.test.annotated.dep1/InterfaceImplementor.<init>()void
        AppInterface appInterface = new InterfaceImplementor();
        //org.test.annotated.dep1/InterfaceImplementor.m2()void
        appInterface.m2();
    }

    public void m1(){
    }
}
