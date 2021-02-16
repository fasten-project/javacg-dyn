package eu.fasten.javacgdyn.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CallGraphTest {

    private static CallGraph callgraph;

    @BeforeAll
    public static void setUp() {
        callgraph = new CallGraph();

        var source = new Method("package.name", "Class", "method1",
                new String[]{"int", "boolean"}, "V", 1, 5);
        var target = new Method("different.name", "Target", "callee",
                new String[]{}, "int", 6, 7);
        var target2 = new Method("different.name", "Target", "callee2",
                new String[]{}, "boolean", 8, 17);
        callgraph.addCall(source, target);
        callgraph.addCall(source, target2);
        callgraph.addCall(source, target2);
    }

    @Test
    void getClassHierarchy() {
        assertEquals(2, callgraph.getClassHierarchy().getTypes().size());
    }

    @Test
    void getGraph() {
        assertEquals(2, callgraph.getGraph().getGraph().size());
    }

    @Test
    void getNodes() {
        assertEquals(3, callgraph.getNodesCount());
    }
}