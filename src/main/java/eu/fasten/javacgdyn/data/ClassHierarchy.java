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
