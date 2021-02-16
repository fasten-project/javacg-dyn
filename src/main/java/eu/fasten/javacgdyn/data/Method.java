package eu.fasten.javacgdyn.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Method implements Serializable {

    private final String packageName;
    private final String className;
    private final String name;
    private final String[] parameters;
    private final String returnType;

    private final int startLine;
    private final int endLine;

    public Method(final String packageName, final String className, final String name,
                  final String[] parameters, final String returnType,
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
        return parameters;
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
        return this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(packageName, className, name, returnType, startLine, endLine);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        return this.packageName + "/" + this.className + "." + this.name +
                "(" + String.join(",", this.parameters) + ")" +
                this.returnType;
    }
}
