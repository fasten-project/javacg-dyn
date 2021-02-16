package eu.fasten.javacgdyn.data;

import java.util.Objects;

public class Call {

    private final int source;
    private final int target;

    public Call(final int source, final int target) {
        this.source = source;
        this.target = target;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Call call = (Call) o;
        return source == call.source && target == call.target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
