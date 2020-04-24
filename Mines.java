import java.io.FileReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

// Onoma:       Konstantinos
// Epitheto:    Damaskinos
// AEM:         3414
// email:       kdamaskin@csd.auth.gr

class node {
    public node(String line) {
        if(line == "") {
            return;
        }

        String[] coord = line.split(" ");
        x = Integer.parseInt(coord[0]);
        y = Integer.parseInt(coord[1]);
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
    public static void main(String[] args) {
        //String file = args[0];
        String file = "data.txt";
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(new FileReader(file));
        } catch (Exception e) {
            System.out.println("File not found!");
            return;
        } 

        node start = new node(fileScanner.nextLine());
        node goal = new node(fileScanner.nextLine());
        Set<node> mines = new HashSet<node>();
        while(fileScanner.hasNextLine()) {
            node n = new node(fileScanner.nextLine());
            mines.add(n);
        }
        fileScanner.close();

        System.out.println(start.x);
        System.out.println(goal.x);
        for(node n : mines) {
            System.out.println(n.x);
        }
    }
}