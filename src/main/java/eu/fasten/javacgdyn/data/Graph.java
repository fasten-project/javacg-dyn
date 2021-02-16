package eu.fasten.javacgdyn.data;

import java.util.HashSet;
import java.util.Set;

public class Graph {

    private final Set<Call> graph;

    public Graph() {
        this.graph = new HashSet<>();
    }

    public Set<Call> getGraph() {
        return graph;
    }

    public boolean addCall(final int source, final int target) {
        return graph.add(new Call(source, target));
    }
}
