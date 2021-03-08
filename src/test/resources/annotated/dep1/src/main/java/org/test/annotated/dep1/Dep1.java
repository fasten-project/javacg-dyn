package org.test.annotated.dep1;

import org.test.annotated.dep2.Dep2;

public class Dep1 {
    public static void source(){
        //org.test.annotated.dep2/Dep2.target()void
        Dep2.target();
    }
}
