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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassHierarchy {

    private final Map<String, Type> types;

    public ClassHierarchy() {
        this.types = new HashMap<>();
    }

    public Collection<Type> getTypes() {
        return types.values();
    }

    public Type getType(final String type) {
        return types.getOrDefault(type, null);
    }

    public int addMethod(final int currentNodeCount, final Method method) {
        types.putIfAbsent(method.getType(), new Type(method.getType()));
        var type = types.get(method.getType());
        return type.addMethod(currentNodeCount, method);
    }
}
