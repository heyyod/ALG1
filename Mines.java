import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.lang.Math;

// Onoma:       Konstantinos
// Epitheto:    Damaskinos
// AEM:         3414
// email:       kdamaskin@csd.auth.gr

// Code Used:
// Quick Hull: https://www.geeksforgeeks.org/quickhull-algorithm-convex-hull/

class node {
    public node(String line) {
        if(line == "") {
            return;
        }

        String[] coord = line.split(" ");
        x = Integer.parseInt(coord[0]);
        y = Integer.parseInt(coord[1]);
        dist = 0;
        canVisit = false;
    }
    int x;
    int y;
    int dist; // shortest distance from start
    boolean canVisit;

    void setPos(String line) {
        if(line == "") {
            return;
        }

        String[] coord = line.split(" ");
        x = Integer.parseInt(coord[0]);
        y = Integer.parseInt(coord[1]);
    }
}

public class Mines {
    
    static boolean isAbove(node[] mines, int l, int r, int test) {
        int result = (mines[test].y - mines[l].y) * (mines[r].x - mines[l].x) - 
                     (mines[r].y - mines[l].y) * (mines[test].x - mines[l].x); 
        if( result > 0 ) return true;
        else return false;
    }

    static int distance(node[] mines, int l, int r, int test) {
        int d = (mines[test].y - mines[l].y) * (mines[r].x - mines[l].x) - 
                (mines[r].y - mines[l].y) * (mines[test].x - mines[l].x);
        return Math.abs(d);
    }

    static void quickHull(Set<node> ch, node[] mines, int l, int r, boolean checkUpperSide) {
        int maxDistIndex = -1;
        int maxDist = 0;

        for(int i = 0; i < mines.length; i++) {
            int distance = distance(mines, l, r, i);
            if(isAbove(mines, l, r, i) == checkUpperSide && distance > maxDist) {
                maxDistIndex = i;
                maxDist = distance;
            }
        }

        if(maxDistIndex == -1){
            // no new point found to add
            mines[l].canVisit = true;
            mines[r].canVisit = true;
            ch.add(mines[l]);
            ch.add(mines[r]);
            return;
        }

        // Apply quickHull to the two new lines
        quickHull(ch, mines, l, maxDistIndex, checkUpperSide);
        quickHull(ch, mines, maxDistIndex, r, checkUpperSide);
    }

    public static void main(String[] args) {
        // String file = args[0];
        String file = "data.txt";

        // Open File
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new FileReader(file));
        } catch (Exception e) {
            System.out.println("File not found!");
            return;
        }

        // Read data
        node start = new node(fileScanner.nextLine());
        node goal = new node(fileScanner.nextLine());
        Set<node> mines = new HashSet<node>();
        while (fileScanner.hasNextLine()) {
            node n = new node(fileScanner.nextLine());
            mines.add(n);
        }
        fileScanner.close();

        // Find leftmost and rightmost mine
        int minXindex = 0;
        int maxXindex = 0;
        node[] minesA = new node[mines.size()];
        mines.toArray(minesA);
        for (int i = 0; i < minesA.length; i++) {
            if (minesA[i].x < minesA[minXindex].x) {
                minXindex = i;
            }
            if (minesA[i].x > minesA[maxXindex].x) {
                maxXindex = i;
            }
        }

        Set<node> convexHull = new HashSet<node>();
        quickHull(convexHull, minesA, minXindex, maxXindex, true);
        quickHull(convexHull, minesA, minXindex, maxXindex, false);
        for(node n : mines) {
            System.out.printf("%d %d %b\n", n.x, n.y, n.canVisit);
        }
    }
}