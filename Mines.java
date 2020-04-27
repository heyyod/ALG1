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

import java.lang.Math;

// Onoma:       Konstantinos
// Epitheto:    Damaskinos
// AEM:         3414
// email:       kdamaskin@csd.auth.gr

// Sources Used:
// Quick Hull:  https://www.geeksforgeeks.org/quickhull-algorithm-convex-hull/
// Graph:       https://www.geeksforgeeks.org/implementing-generic-graph-in-java/ 
// Dijkstra:    https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//                       Code
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

class node {
    public node(String line) {
        if (line == "")
            return;

        String[] coord = line.split(" ");
        x = Integer.parseInt(coord[0]);
        y = Integer.parseInt(coord[1]);
        dist = Float.POSITIVE_INFINITY;
        next = null;
    }

    int x;
    int y;
    float dist; // shortest distance from start
    node next;

    void setPos(String line) {
        if (line == "")
            return;

        String[] coordinate = line.split(" ");
        x = Integer.parseInt(coordinate[0]);
        y = Integer.parseInt(coordinate[1]);
    }

    float distanceFrom(node n) {
        float result = (float) Math.sqrt((double) ((n.x - this.x) * (n.x - this.x) + (n.y - this.y) * (n.y - this.y)));
        return result;
    }
}

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
    Map<node, List<node>> graph = new HashMap<node, List<node>>();

    void addAndConnect(node a, node b) {
        if (!graph.containsKey(a))
            graph.put(a, new LinkedList<node>());

        if (!graph.containsKey(b))
            graph.put(b, new LinkedList<node>());

        graph.get(a).add(b);
        graph.get(b).add(a);
    }

    void getMinPath(node start, node goal) {
        Set<node> checkedNodes = new HashSet<node>();
        PriorityQueue<node> pq = new PriorityQueue<node>(graph.size(), new nodeComparator());

        start.dist = 0.0f;
        pq.add(start);

        while (checkedNodes.size() < graph.size()) {
            node minDistNode = pq.remove();
            checkedNodes.add(minDistNode);

            for (node neighbour : graph.get(minDistNode)) {
                if (!checkedNodes.contains(neighbour)) {
                    float newDist = minDistNode.dist + neighbour.distanceFrom(minDistNode);

                    if (newDist < neighbour.dist)
                        neighbour.dist = newDist;

                    if (minDistNode.next == null || neighbour.dist < minDistNode.next.dist)
                        minDistNode.next = neighbour;

                    if (neighbour.x == goal.x && neighbour.y == goal.y) {
                        // Goal point found!
                        System.out.printf("The shortest distance is %f\n", goal.dist);
                        System.out.printf("The shortest path is:");
                        node n = start;
                        while (n.next != null) {
                            System.out.printf("(%d,%d)-->", n.x, n.y);
                            n = n.next;
                        }
                        System.out.printf("(%d,%d)", n.x, n.y);
                        return;
                    }

                    pq.add(neighbour);
                }
            }
        }
    }
}

public class Mines {

    static boolean isAbove(node[] mines, int l, int r, int test) {
        int result = (mines[test].y - mines[l].y) * (mines[r].x - mines[l].x)
                - (mines[r].y - mines[l].y) * (mines[test].x - mines[l].x);
        if (result > 0)
            return true;
        else
            return false;
    }

    static int distance(node[] mines, int l, int r, int test) {
        int d = (mines[test].y - mines[l].y) * (mines[r].x - mines[l].x)
                - (mines[r].y - mines[l].y) * (mines[test].x - mines[l].x);
        return Math.abs(d);
    }

    static void quickHull(Graph graph, node[] mines, int l, int r, boolean checkUpperSide) {
        int maxDistIndex = -1;
        int maxDist = 0;

        for (int i = 0; i < mines.length; i++) {
            int distance = distance(mines, l, r, i);
            if (isAbove(mines, l, r, i) == checkUpperSide && distance > maxDist) {
                maxDistIndex = i;
                maxDist = distance;
            }
        }

        if (maxDistIndex == -1) {
            // no new point found to add in the hull
            // add the two nodes to the graph
            graph.addAndConnect(mines[l], mines[r]);
            return;
        }

        // Apply quickHull to the two new lines
        quickHull(graph, mines, l, maxDistIndex, checkUpperSide);
        quickHull(graph, mines, maxDistIndex, r, checkUpperSide);
    }

    public static void main(String[] args) {
        String file = "";
        if(args.length == 1) {
            file = args[0];
            System.out.printf("Opening file: %s\n", file);
        }
        else if(args.length == 0) {
            System.out.printf("ERROR: No arguments passed. You need to give a file.\n");
            return;
        }
        else if(args.length > 1) {
            System.out.printf("ERROR: Too many argument. Only 1 required for the file.\n");
            return;
        }

        // Open File
        FileReader fr;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e1) {
            System.out.println("File not found!");
            return;
        }
        Scanner fileScanner = new Scanner(fr);

        // Read data
        node start = new node(fileScanner.nextLine());
        node goal = new node(fileScanner.nextLine());
        
        Set<node> nodesSet = new HashSet<node>();
        nodesSet.add(start);
        nodesSet.add(goal);
        while (fileScanner.hasNextLine()) {
            node n = new node(fileScanner.nextLine());
            nodesSet.add(n);
        }
        fileScanner.close();

        // Find leftmost and rightmost node
        int minXindex = 0;
        int maxXindex = 0;
        node[] nodes = new node[nodesSet.size()];
        nodesSet.toArray(nodes);
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].x < nodes[minXindex].x) 
                minXindex = i;
            if (nodes[i].x > nodes[maxXindex].x)
                maxXindex = i;
        }
        
        // We need to find the quickhull of all our points including the
        // starting and goal point. This is because the greatest side of
        // a scalene triangle is always less than the sum of the two smaller
        // sides. So we only need to consider the connection of our start and
        // goal to the uppest and downest mines ONLY.
        Graph graph = new Graph(); // add the
        quickHull(graph, nodes, minXindex, maxXindex, true); // upper hull
        quickHull(graph, nodes, minXindex, maxXindex, false); // lower hull

        graph.getMinPath(start, goal);
    }
}