package org.test.annotated.dep2;

import org.test.annotated.common.AppInterface;

public class Dep2 implements AppInterface {

    public static void target(){
        Object obj = new Object();
        obj.hashCode();
    }

    public void m1(){}

    public void m2() {
    }
}
