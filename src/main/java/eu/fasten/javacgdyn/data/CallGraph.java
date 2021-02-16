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
