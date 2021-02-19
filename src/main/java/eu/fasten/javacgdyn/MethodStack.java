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

import static eu.fasten.javacgdyn.Profiler.callGraph;

import eu.fasten.javacgdyn.data.Method;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Stack;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MethodStack {

    private static final Stack<Method> stack = new Stack<>();
    public static Pattern rootProject;

    public static void push(final String serializedMethod) {
        var method = deserialize(serializedMethod);
        stack.push(method);
    }

    public static void pop() {
        var target = stack.pop();
        if (!stack.isEmpty() && (rootProject.matcher(stack.peek().toString()).matches())) {
            callGraph.addCall(stack.peek(), target);
        }
    }

    public static Method deserialize(final String s) {
        byte[] data = Base64.getDecoder().decode(s);

        Method method = null;
        try {
            var objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
            method = (Method) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Exception while deserializing");
        }
        return method;
    }

    public static String serialize(final Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
