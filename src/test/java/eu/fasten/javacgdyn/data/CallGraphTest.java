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

package eu.fasten.javacgdyn.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CallGraphTest {

    private static CallGraph callgraph;

    @BeforeAll
    public static void setUp() {
        callgraph = new CallGraph();

        var source = new Method("package.name", "Class", "method1",
                new FastenJavaURI[]{FastenJavaURI.createWithoutFunction("/" + "java.lang" + "/" + "int"),
                        FastenJavaURI.createWithoutFunction("/" + "java.lang" + "/" + "boolean")},
                FastenJavaURI.createWithoutFunction("/" + "java.lang" + "/" + "V"), 1, 5);
        var target = new Method("different.name", "Target", "callee",
                new FastenJavaURI[]{}, FastenJavaURI.createWithoutFunction("/" + "java.lang" + "/" + "int"), 6, 7);
        var target2 = new Method("different.name", "Target", "callee2",
                new FastenJavaURI[]{}, FastenJavaURI.createWithoutFunction("/" + "java.lang" + "/" + "boolean"), 8, 17);
        callgraph.addCall(source, target);
        callgraph.addCall(source, target2);
        callgraph.addCall(source, target2);
    }

    @Test
    void getClassHierarchy() {
        assertEquals(2, callgraph.getClassHierarchy().getTypes().size());
    }

    @Test
    void getGraph() {
        assertEquals(2, callgraph.getGraph().getGraph().size());
    }

    @Test
    void getNodes() {
        assertEquals(3, callgraph.getNodesCount());
    }
}