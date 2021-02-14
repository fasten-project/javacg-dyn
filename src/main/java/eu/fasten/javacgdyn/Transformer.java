/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.fasten.javacgdyn;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Set;
import java.util.regex.Pattern;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String name = className.replace("/", ".");
        ClassPool pool = ClassPool.getDefault();

        var excl = Set.of(
                "^java.*", "^jdk.*", "^sun.*", "^com.sun.*",
                "^eu.fasten.javacgdyn.MethodStack$", "^eu.fasten.javacgdyn.Transformer$", "^org.xml.sax.*",
                "^org.apache.maven.surefire.*", "^org.apache.tools.*", "^org.mockito.*",
                "^org.easymock.internal.*",
                "^org.junit.*", "^junit.framework.*", "^org.hamcrest.*", "^org.objenesis.*",
                "^edu.washington.cs.mut.testrunner.Formatter"
        );

        var pattern = Pattern.compile(String.join("|", excl));

        if (!pattern.matcher(name).matches()) {
            try {
                var clazz = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
                if (!clazz.isInterface()) {
                    for (var method : clazz.getDeclaredBehaviors()) {
                        if (!method.isEmpty()) {
                            transformMethod(clazz.getName(), method, classfileBuffer.length);
                        }
                    }
                    clazz.detach();
                    return clazz.toBytecode();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    private void transformMethod(final String className, final CtBehavior method, final int length) throws CannotCompileException {
        var name = className.substring(className.lastIndexOf('.') + 1);
        var methodName = method.getName();
        var signature = method.getSignature();

        if (method.getName().equals(name))
            methodName = "<init>";

        int start = method.getMethodInfo().getLineNumber(0);
        int end = start;

        for (int ps = 1; ps < length; ps++) {
            var line = method.getMethodInfo().getLineNumber(ps);
            if (line == -1) {
                break;
            }
            end = Math.max(line, end);
        }

        System.out.println("METHOD: " + methodName + " Start: " + start + " End: " + end);

        var command = "eu.fasten.javacgdyn.MethodStack.push(\"" +
                className +
                "\",\"" +
                methodName +
                "\",\"" +
                signature +
                "\");";
        method.insertBefore(command);
        method.insertAfter("eu.fasten.javacgdyn.MethodStack.pop();");
    }
}
