// Onoma:       Konstantinos
// Epitheto:    Damaskinos
// AEM:         3414
// email:       kdamaskin@csd.auth.gr
// Project:     Algorithms Project A

// Sources Used:
// Quick Hull:  https://www.geeksforgeeks.org/quickhull-algorithm-convex-hull/
// Graph:       https://www.geeksforgeeks.org/implementing-generic-graph-in-java/ 
// Dijkstra:    https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.lang.Math;

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                       Code
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

// This class represents a point in our problem.
// Either the start or goal, or a mine.
class node {
    public node(String line) {
        if (line == "")
            return;

        String[] coord = line.split(" ");
        x = Integer.parseInt(coord[0]);
        y = Integer.parseInt(coord[1]);
        dist = Double.POSITIVE_INFINITY;
        prev = null;
    }

    int x;
    int y;
    double dist; // shortest distance from start
    node prev;

    double distanceFrom(node n) {
        double distSqrd = (n.x - this.x) * (n.x - this.x) + (n.y - this.y) * (n.y - this.y);
        double result = Math.sqrt(distSqrd);
        return result;
    }
}

// Comparator for the priority queue we use to find the min path
class nodeComparator implements Comparator<node> {
    public int compare(node a, node b) {
        if (a.dist < b.dist)
            return -1;
        else if (a.dist == b.dist)
            return 0;
        else
            return 1;
    }
}

class Graph {
    Map<node, List<node>> adjList = new HashMap<node, List<node>>();

    void addVertex(node n) {
        adjList.put(n, new LinkedList<node>());
    }

    void addEdge(node a, node b) {
        adjList.get(a).add(b);
        adjList.get(b).add(a);
    }

    Set<node> vertices() {
        return adjList.keySet();
    }

    void getMinPath(node start, node goal) {
        Set<node> checkedNodes = new HashSet<node>();
        PriorityQueue<node> pq = new PriorityQueue<node>(adjList.size(), new nodeComparator());
        
        double minDistToGoal = Double.POSITIVE_INFINITY;
        node goalPrev = null;

        start.dist = 0.0f;
        pq.add(start);

        while (checkedNodes.size() < adjList.size()) {
            node minDistNode = null;
            if(!pq.isEmpty())
                minDistNode = pq.remove();
            else
                break;

            checkedNodes.add(minDistNode);
            for (node neighbour : adjList.get(minDistNode)) {
                if (!checkedNodes.contains(neighbour)) {
                    double newDist = minDistNode.dist + neighbour.distanceFrom(minDistNode);

                    if (newDist < neighbour.dist) {
                        // found a better path for this node
                        neighbour.dist = newDist;
                        neighbour.prev = minDistNode;
                    }

                    if (neighbour.x == goal.x && neighbour.y == goal.y 
                        && neighbour.dist < minDistToGoal) {
                        // if this node is the goal, save the previous node
                        // and the distance
                        minDistToGoal = neighbour.dist;
                        goalPrev = neighbour.prev;
                    }

                    pq.add(neighbour);
                }
            }
        }

        // set goal dist and prev node to the minimum path
        goal.dist = minDistToGoal;
        goal.prev = goalPrev;
        printMinPath(goal);
    }

    void printMinPath(node goal) {
        // Remove trailing zeros
        String dist = String.format("%.5f", goal.dist);
        while(dist.endsWith("0")) {
            dist = dist.substring(0, dist.length() - 1);
        }
        System.out.printf("The shortest distance is %s\n", dist);

        // We add the nodes to a stack since we only know the path
        // from the goal to the start
        Stack<node> path = new Stack<node>();
        node n = goal;
        while(n != null) {
            path.push(n);
            n = n.prev;
        }

        System.out.printf("The shortest path is:");
        while(path.size()>1 ) {
            n = path.pop();
            System.out.printf("(%d,%d)-->", n.x, n.y);
        }
        n = path.pop();
        System.out.printf("(%d,%d)\n", n.x, n.y);
        return;
    }
}

public class Mines {
    // returns a value proportional to the distance 
    // between the point p and the line joining the 
    // points p1 and p2 
    // positive if above the line, negative if below
    static long distanceFromLineSegment(node l, node r, node test) {
        long d = (test.y - l.y) * (r.x - l.x) - (r.y - l.y) * (test.x - l.x);
        return d;
    }

    static boolean isOnDesiredSide(long dist, boolean side) {
        return ((-dist > 0) == side);
    }

    static void quickHull(Graph graph, node l, node r, boolean checkUpperSide) {
        node maxDistNode = null;
        long maxDist = 0;

        // Look for further point from the line segment
        for (node n : graph.vertices()) {
            long dist = distanceFromLineSegment(l, r, n);
            // check if it's on the desired side
            if ((dist != 0) && isOnDesiredSide(dist, checkUpperSide)) {
                dist = dist >= 0 ? dist : -dist;
                if( dist >= maxDist) {
                    maxDistNode = n;
                    maxDist = dist;
                }
            } 
        }

        if (maxDistNode == null) {
            // no new point found to add in the convex hull
            // we connect the two nodes
            graph.addEdge(l, r);
            return;
        }

        // Apply quickHull to the two new lines
        quickHull(graph, l, maxDistNode, checkUpperSide);
        quickHull(graph, maxDistNode, r, checkUpperSide);
    }

    public static void main(String[] args) {
        String file = args[0];

        // Open File
        FileReader fr;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e1) {
            System.out.println("File not found!");
            return;
        }
        Scanner fileScanner = new Scanner(fr);

        // Read data into the graph
        Graph graph = new Graph();
        node start = new node(fileScanner.nextLine());
        node goal = new node(fileScanner.nextLine());
        graph.addVertex(start);
        graph.addVertex(goal);
        while (fileScanner.hasNextLine()) {
            graph.addVertex(new node(fileScanner.nextLine()));
        }
        fileScanner.close();

        // We need to find the quickhull of all our points including the
        // starting and goal point. This is because the greatest side of
        // a scalene triangle is always less than the sum of the two smaller
        // sides. So we only need to consider the connection of our start and
        // goal to the uppest and downest mines ONLY.
        quickHull(graph, start, goal, true); // upper hull
        quickHull(graph, start, goal, false); // lower hull

        graph.getMinPath(start, goal);
    }
}