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
        prev = null;
    }

    int x;
    int y;
    double dist; // shortest distance from start
    node prev;

    void setPos(String line) {
        if (line == "")
            return;

        String[] coordinate = line.split(" ");
        x = Integer.parseInt(coordinate[0]);
        y = Integer.parseInt(coordinate[1]);
    }

    double distanceFrom(node n) {
        double distSqrd = (n.x - this.x) * (n.x - this.x) + (n.y - this.y) * (n.y - this.y);
        double result = Math.sqrt(distSqrd);
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
                    double newDist = minDistNode.dist + neighbour.distanceFrom(minDistNode);

                    if (newDist < neighbour.dist) {
                        neighbour.dist = newDist;
                        neighbour.prev = minDistNode;
                    }

                    if (neighbour.x == goal.x && neighbour.y == goal.y) {
                        printMinPath(goal);
                        return;
                    }

                    pq.add(neighbour);
                }
            }
        }
    }

    void printMinPath(node goal) {
        System.out.printf("The shortest distance is %.5f\n", goal.dist);

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

    static boolean isAbove(node l, node r, node test) {
        int result = (test.y - l.y) * (r.x - l.x) - (r.y - l.y) * (test.x - l.x);
        if (result > 0)
            return true;
        else
            return false;
    }

    static int distance(node l, node r, node test) {
        int d = (test.y - l.y) * (r.x - l.x) - (r.y - l.y) * (test.x - l.x);
        return Math.abs(d);
    }

    static void quickHull(Graph graph, Set<node> nodes, node l, node r, boolean checkUpperSide) {
        node maxDistNode = null;
        int maxDist = 0;

        for (node n : nodes) {
            int distance = distance(l, r, n);
            if (isAbove(l, r, n) == checkUpperSide && distance > maxDist) {
                maxDistNode = n;
                maxDist = distance;
            }
        }

        if (maxDistNode == null) {
            // no new point found to add in the hull
            // add the two nodes to the graph
            nodes.remove(l);
            nodes.remove(r);
            graph.addAndConnect(l, r);
            return;
        }

        // Apply quickHull to the two new lines
        quickHull(graph, nodes, l, maxDistNode, checkUpperSide);
        quickHull(graph, nodes, maxDistNode, r, checkUpperSide);
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

        // Read data
        node start = new node(fileScanner.nextLine());
        node goal = new node(fileScanner.nextLine());
        
        Set<node> nodes = new HashSet<node>();
        nodes.add(start);
        nodes.add(goal);
        while (fileScanner.hasNextLine()) {
            nodes.add(new node(fileScanner.nextLine()));
        }
        fileScanner.close();
        
        // We need to find the quickhull of all our points including the
        // starting and goal point. This is because the greatest side of
        // a scalene triangle is always less than the sum of the two smaller
        // sides. So we only need to consider the connection of our start and
        // goal to the uppest and downest mines ONLY.
        Graph graph = new Graph(); // add the
        quickHull(graph, nodes, start, goal, true); // upper hull
        quickHull(graph, nodes, start, goal, false); // lower hull

        graph.getMinPath(start, goal);
    }
}