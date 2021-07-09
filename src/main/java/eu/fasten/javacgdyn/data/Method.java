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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Method implements Serializable {

    private final String packageName;
    private final String className;
    private final String name;
    private final FastenJavaURI[] parameters;
    private final FastenJavaURI returnType;

    private final int startLine;
    private final int endLine;

    public Method(final String packageName, final String className, final String name,
                  final FastenJavaURI[] parameters, final FastenJavaURI returnType,
                  final int startLine, final int endLine) {
        this.packageName = packageName;
        this.className = className;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;

        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getType() {
        return this.packageName + "." + this.className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return Arrays.stream(parameters).map(FastenURI::toString).toArray(String[]::new);
    }

    public String getReturnType() {
        return returnType.toString();
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public FastenURI toFastenURI() {
        final var javaURIRaw = FastenJavaURI.create(null, null, null,
                packageName, className, name, parameters, returnType);
        final var javaURI = javaURIRaw.canonicalize();

        return FastenURI.createSchemeless(javaURI.getRawForge(), javaURI.getRawProduct(),
                javaURI.getRawVersion(),
                javaURI.getRawNamespace(), javaURI.getRawEntity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return this.toString().equals(method.toString());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(packageName, className, name, returnType, startLine, endLine);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return this.toFastenURI().toString();
    }
}
