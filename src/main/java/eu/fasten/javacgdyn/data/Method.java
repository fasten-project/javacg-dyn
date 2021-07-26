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

    private final String product;
    private final String version;
    private final String packageName;
    private final String className;
    private final String name;
    private final String[] parameters;
    private final String returnType;

    private final int startLine;
    private final int endLine;

    public Method(final String product, final String version, final String packageName, final String className, final String name,
                  final String[] parameters, final String returnType,
                  final int startLine, final int endLine) {
        this.product = product;
        this.version = version;
        this.packageName = packageName;
        this.className = className;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;

        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getProduct() {
        return product;
    }

    public String getVersion() {
        return version;
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
        return this.parameters.clone();
    }

    public String getReturnType() {
        return returnType;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        if (startLine != method.startLine) return false;
        if (endLine != method.endLine) return false;
        if (!Objects.equals(product, method.product))
            return false;
        if (!Objects.equals(version, method.version))
            return false;
        if (!Objects.equals(packageName, method.packageName))
            return false;
        if (!Objects.equals(className, method.className))
            return false;
        if (!Objects.equals(name, method.name)) return false;
        if (!Arrays.equals(parameters, method.parameters)) return false;
        return Objects.equals(returnType, method.returnType);
    }

    @Override
    public int hashCode() {
        int result = product != null ? product.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameters);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + startLine;
        result = 31 * result + endLine;
        return result;
    }

    @Override
    public String toString() {
        return this.product + "@" + this.version + "/" + this.packageName + "/" + this.className
                + "." + this.name + "(" + String.join(",", this.parameters) + ")" +
                this.returnType;
    }
}
