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

import java.util.Stack;

@SuppressWarnings("unused")
public class MethodStack {

    private static final Stack<String> stack = new Stack<>();
    private static final StringBuilder cg = new StringBuilder();

    public static void push(String className, String methodName, String signature) {
        stack.push(className + "." + methodName + signature);
    }

    public static void pop() {
        var target = stack.pop();
        if (!stack.isEmpty()) {
            cg.append(stack.peek()).append(" -> ").append(target).append("\n");
        } else {
            System.out.println(cg.toString());
        }
    }
}
