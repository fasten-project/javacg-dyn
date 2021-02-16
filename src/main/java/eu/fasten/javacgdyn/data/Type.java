package eu.fasten.javacgdyn.data;

import java.util.HashMap;
import java.util.Map;

public class Type {

    private final String typeName;
    private final Map<Integer, Method> methods;

    public Type(String typeName) {
        this.typeName = typeName;
        this.methods = new HashMap<>();
    }

    public String getTypeName() {
        return typeName;
    }

    public Map<Integer, Method> getMethods() {
        return methods;
    }

    public int addMethod(final int currentNodeCount, final Method method) {
        if (methods.containsValue(method)) {
            for (var entry : methods.entrySet()) {
                if (entry.getValue().equals(method)) {
                    return entry.getKey();
                }
            }
        } else {
            methods.put(currentNodeCount, method);
            return currentNodeCount;
        }
        throw new IllegalArgumentException("No method could be found nor added");
    }
}
