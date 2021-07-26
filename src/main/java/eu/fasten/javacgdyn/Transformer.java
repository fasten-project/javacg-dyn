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
import java.net.URL;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.regex.Pattern;
import eu.fasten.javacgdyn.utils.MavenUtils;
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
                        transformMethod(clazz.getName(), method, classfileBuffer.length, clazz.getURL().toString());
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

    private void transformMethod(final String className, final CtBehavior method, final int length, final String classUrl) throws CannotCompileException, NotFoundException, IOException {

        String product = null;
        String version = null;
        if (classUrl.startsWith("jar:file:")) {
            var path = new URL(classUrl).getPath();
            var parts = path.split("/");
            int c = 0;
            while (!parts[c].equals("repository")) {
                c++;
            }
            product = parts[c + 1] + ":" + parts[c + 2];
            version = parts[c + 3];
        } else if (classUrl.startsWith("file:")) {
            var path = new URL(classUrl).getPath();
            var parts = path.split("/");
            int c = 0;
            while (!parts[c].equals("target")) {
                c++;
            }
            var builder = new StringBuilder();
            for (int i = 0; i < c; i++) {
                builder.append("/").append(parts[i]);
            }
            var coordinate = MavenUtils.extractMavenCoordinate(Path.of(builder.toString()));
            if (coordinate != null) {
                product = coordinate.split(":")[0] + ":" + coordinate.split(":")[1];
                version = coordinate.split(":")[3];
            }
        }
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

        var m = new Method(product, version, packageName, name, methodName, parameters, returnType, start, end);
        var pushCommand = "eu.fasten.javacgdyn.MethodStack.push(\"" + MethodStack.serialize(m) + "\");";
        var popCommand = "eu.fasten.javacgdyn.MethodStack.pop();";
        method.insertBefore(pushCommand);
        method.insertAfter(popCommand);
    }
}
