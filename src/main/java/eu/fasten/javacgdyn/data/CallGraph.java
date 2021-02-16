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

public class CallGraph {

    private final ClassHierarchy classHierarchy;
    private final Graph graph;
    private int nodes;

    public CallGraph() {
        this.classHierarchy = new ClassHierarchy();
        this.graph = new Graph();
        this.nodes = 0;
    }

    public ClassHierarchy getClassHierarchy() {
        return classHierarchy;
    }

    public Graph getGraph() {
        return graph;
    }

    public int getNodesCount() {
        return nodes;
    }

    public boolean addCall(final Method source, final Method target) {
        var sourceId = classHierarchy.addMethod(nodes, source);
        nodes = Math.max(nodes, sourceId + 1);
        var targetId = classHierarchy.addMethod(nodes, target);
        nodes = Math.max(nodes, targetId + 1);
        return graph.addCall(sourceId, targetId);
    }
}
