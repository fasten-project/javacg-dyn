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

import eu.fasten.javacgdyn.data.Method;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.regex.Pattern;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String name = className.replace("/", ".");
        ClassPool pool = ClassPool.getDefault();

        var pattern = Pattern.compile(String.join("|",
                Profiler.config.getProperty("excl").split(",")));

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
            } catch (IOException | CannotCompileException | NotFoundException e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    private void transformMethod(final String className, final CtBehavior method, final int length) throws CannotCompileException, NotFoundException, IOException {
        var name = className.substring(className.lastIndexOf('.') + 1);
        var packageName = className.substring(0, className.lastIndexOf('.'));
        var methodName = method.getName();

        if (method.getMethodInfo().isConstructor())
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

        var parameters = Arrays.stream(method.getParameterTypes())
                .map(CtClass::getName)
                .toArray(String[]::new);

        var returnType = method.getMethodInfo().isMethod()
                ? ((CtMethod) method).getReturnType().getName()
                : "void";

        var m = new Method(packageName, name, methodName, parameters, returnType, start, end);
        var command = "eu.fasten.javacgdyn.MethodStack.push(\"" + MethodStack.serialize(m) + "\");";
        method.insertBefore(command);
        method.insertAfter("eu.fasten.javacgdyn.MethodStack.pop();");
    }
}
