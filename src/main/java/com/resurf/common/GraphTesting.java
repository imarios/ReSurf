package com.resurf.common;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.ConnectedComponents;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphTesting {
  private void checkSingle() {
    Graph graph = new SingleGraph("Tutorial 1");
    graph.addNode("A" );
    graph.addNode("B" );
    graph.addNode("C" );
    graph.addEdge("AB", "A", "B");
    graph.addEdge("BC", "B", "C");
    graph.addEdge("CA", "C", "A");
    graph.display();

    for(Node n:graph) {
      System.out.println(n.getId());
    }

    for(Edge e:graph.getEachEdge()) {
      System.out.println(e.getId());
    }

    ConnectedComponents cc = new ConnectedComponents();
    cc.init(graph);

    System.out.printf("%d connected component(s) in this graph, so far.%n",
      cc.getConnectedComponentsCount());

    graph.removeEdge("AB");

    System.out.printf("Eventually, there are %d.%n",
      cc.getConnectedComponentsCount());

    graph.getNode("A").setAttribute("time", new ArrayList<Long>());

    System.out.println( graph.getNode("A").getDegree() );
  }
  public static void main(String[] args) {
    Graph graph = new MultiGraph("Tutorial 1");

    graph.addNode("a" );
    graph.addNode("b" );
    graph.addEdge("1", "a", "b");

    graph.addEdge("2", "a", "b");

    System.out.println(graph.getEdge("1"));

    graph.getEdge("2").setAttribute("00", new ArrayList<>(Arrays.asList(12L, 56L)));

    System.out.println(graph.getEdge("2").<ArrayList<Long>>getAttribute("00"));
  }
}
