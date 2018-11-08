package com.netflix.nicobar.core.module;

import java.util.*;
import java.util.Map.Entry;
import junit.framework.TestCase;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/**
 * @author victor.
 * @since 2018/10/19
 */
public class GraphUtilsTest extends TestCase {

    public static void main(String[] args) {
        test();
    }
    public static void test() {
        Map<String, Set<String>> archiveDependencies = new HashMap<String, Set<String>>();
        archiveDependencies.put("A", new HashSet<String>(Arrays.asList("B", "C")));
        archiveDependencies.put("B", new HashSet<String>(Arrays.asList("D", "C")));
        archiveDependencies.put("C", new HashSet<String>(Arrays.asList("D", "F")));
        archiveDependencies.put("D", new HashSet<String>(Arrays.asList("E", "F")));
        archiveDependencies.put("E", new HashSet<String>());
        archiveDependencies.put("F", new HashSet<String>());

        DirectedGraph<String, DefaultEdge> candidateGraph = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        Map<String, Set<String>> moduleIdentifiers = new HashMap<>();
        // moduleIdentifiers.put("A", new HashSet<String>(Arrays.asList("B", "E")));
        GraphUtils.addAllVertices(candidateGraph, moduleIdentifiers.keySet());
        for (Entry<String, Set<String>> entry : moduleIdentifiers.entrySet()) {
            String scriptModuleId = entry.getKey();
            Set<String> dependencyNames = entry.getValue();
            GraphUtils.addOutgoingEdges(candidateGraph, scriptModuleId, dependencyNames);
        }

        GraphUtils.swapVertices(candidateGraph, archiveDependencies);
        Set<String> iv = GraphUtils.getIncomingVertices(candidateGraph, "B");
        System.out.println("B 被依赖 " + iv);
        Set<String> ov = GraphUtils.getOutgoingVertices(candidateGraph, "B");
        System.out.println("==================================");
        System.out.println("B 依赖 " + ov);
        System.out.println("==================================");
        Set<String> allOv = GraphUtils.getOutgoingVerticesRecursion(candidateGraph, "B");
        System.out.println("B 依赖 " + allOv);
        System.out.println("==================================");
        Set<DefaultEdge> allEdges = candidateGraph.getAllEdges("A", "D");
        System.out.println("allEdges = " + allEdges);
        System.out.println("==================================");
        CycleDetector cycleDetector=  new CycleDetector(candidateGraph);
        System.out.println("是否有环 "+ cycleDetector.detectCycles());
        System.out.println("B是否有环 "+ cycleDetector.detectCyclesContainingVertex("B"));
        System.out.println("==================================");
        TopologicalOrderIterator<String, DefaultEdge> toi = new TopologicalOrderIterator<String, DefaultEdge>(candidateGraph);
        System.out.println("TopologicalOrder");
        while (toi.hasNext()) {
            System.out.println( toi.next() );
        }

        System.out.println("广度优先");
        BreadthFirstIterator<String, DefaultEdge> bfi = new BreadthFirstIterator<String, DefaultEdge>(candidateGraph, "B");
        while (bfi.hasNext()) {
            System.out.println( bfi.next() );
        }
        System.out.println("深度优先");
        DepthFirstIterator<String, DefaultEdge> dfi = new DepthFirstIterator<String, DefaultEdge>(candidateGraph, "B");
        while (dfi.hasNext()) {
            System.out.println( dfi.next() );
        }
        System.out.println("Closest");
        ClosestFirstIterator<String, DefaultEdge> cfi = new ClosestFirstIterator<String, DefaultEdge>(candidateGraph, "B");
        while (cfi.hasNext()) {
            System.out.println( cfi.next() );
        }

        System.out.println("==================================");
        // iterate over the graph in reverse dependency order
        Set<String> leaves = GraphUtils.getLeafVertices(candidateGraph);
        while (!leaves.isEmpty()) {
            for (String scriptModuleId : leaves) {
                System.out.println("当前: " + scriptModuleId);

                // find dependents and add them to the to be compiled set
                Set<String> dependents = GraphUtils
                        .getIncomingVertices(candidateGraph, scriptModuleId);
                for (String dependentScriptModuleId : dependents) {
                    System.out.println("\t被依赖: " + dependentScriptModuleId);
                }
            }
            System.out.println("-------------------------------------");
            GraphUtils.removeVertices(candidateGraph, leaves);
            leaves = GraphUtils.getLeafVertices(candidateGraph);
        }
    }

    public static void subGraph( DirectedGraph<String, DefaultEdge> directedGraph, Set<String> subNode ) {
        DirectedSubgraph<String, DefaultEdge>  directedSubGraph = new DirectedSubgraph<>(directedGraph, subNode, null);
        System.out.println(directedSubGraph.vertexSet());
        System.out.println(directedSubGraph.edgeSet());

    }

}