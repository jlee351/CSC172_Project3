import java.util.ArrayList;
import java.io.*;
import java.util.*;

class Node{
    private String value;
    private ArrayList<Node> adj;

    public Node(String value){
        this.value = value;
        this.adj = new ArrayList<>();
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public void setAdj(ArrayList<Node> adj){
        this.adj = adj;
    }

    public ArrayList<Node> getAdj(){
        return adj;
    }

}

class Read{

    //checks if contains node
    public boolean containsNode(ArrayList<Node> list, Node n){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getValue().equals(n.getValue())){
                return true;
            }
        }
        return false;
    }

    //returns node
    public Node getNode(ArrayList<Node> list, Node n){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getValue().equals(n.getValue())){
                return n;
            }
        }
        return null;
    }

    //prints node and values
    public void print(ArrayList<Node> allNodes){
        for (int i = 0; i < allNodes.size(); i++){
            Node n = allNodes.get(i);
            System.out.print(n.getValue() + ": ");

            for (int j = 0; j < n.getAdj().size(); j++){
                System.out.print(n.getAdj().get(j).getValue() + " ");
            }

            System.out.println();
        }
    }

    //adds adjacent nodes
    public void addAdj(Node n1, Node n2){
        ArrayList<Node> list = n1.getAdj();
        list.add(n2);
        n1.setAdj(list);
    }

    //prases through text file
    public ArrayList<Node> parse(String FileName){
        ArrayList<Node> allNodes = new ArrayList<>();
        ArrayList<String> strNodes = new ArrayList<>();
        StopContagion s = new StopContagion();
        try{
            File in = new File(FileName);
            Scanner sc = new Scanner(in);
            while (sc.hasNextLine()){
                String line = sc.nextLine();
                String[] splitArr = line.split("\\s+");
                Node n1 = new Node(splitArr[0]);
                Node n2 = new Node(splitArr[1]);

                if (strNodes.contains(n1.getValue())){
                    n1 = allNodes.get(s.findNode(allNodes, n1));
                    s.removeNode(allNodes, n1);
                    addAdj(n1, n2);
                    allNodes.add(n1);
                }
                else{
                    strNodes.add(splitArr[0]);
                    addAdj(n1, n2);
                    allNodes.add(n1);
                }

                if (strNodes.contains(n2.getValue())){
                    n2 = allNodes.get(s.findNode(allNodes, n2));
                    s.removeNode(allNodes, n2);
                    addAdj(n2, n1);
                    allNodes.add(n2);
                }
                else{
                    strNodes.add(splitArr[1]);
                    addAdj(n2, n1);
                    allNodes.add(n2);
                }
            }
            sc.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return allNodes;
    }
}

public class StopContagion{

    public static void main(String[] args){
        Read r = new Read();
        StopContagion  s = new StopContagion();

        if(args[0].equals("-r")){
            ArrayList<Node> graph = r.parse(args[3]);
            int radius = Integer.parseInt(args[1]);
            int num_nodes = Integer.parseInt(args[2]);
            s.dismantle(graph, radius, num_nodes);
        }
        else if(args[0].equals("-d")){
            ArrayList<Node> graph = r.parse(args[2]);
            int num_nodes = Integer.parseInt(args[1]);
            s.outputDegree(s.inoculate(graph, num_nodes));
        }
    }

    //counts the degree; returns size of adjacent of a node
    public int countDeg(Node n){
        return n.getAdj().size();
    }

    //returns max degree in a list of node.
    public Node maxDeg(ArrayList<Node> list){
        Node maxNode = new Node("");
        for (Node n : list){
            if(countDeg(n) > countDeg(maxNode)){
                maxNode = n;
            }
        }
        return maxNode;
    }

    //find node n in list
    public int findNode(ArrayList<Node> list, Node n){
        for(int i = 0; i<list.size(); i++){
            if(list.get(i).getValue().equals(n.getValue())){
                return i;
            }
        }
        return -1;
    }

    //return node n in list
    public Node getNode(ArrayList<Node> list, Node n){
        for(int i = 0; i<list.size(); i++){
            if(list.get(i).getValue().equals(n.getValue())){
                return list.get(i);
            }
        }
        return new Node("");
    }

    //remove nodes
    public ArrayList<Node> removeNode(ArrayList<Node> list, Node n){
        int index = findNode(list, n);
        list.remove(index);
        return list;
    }

    //removes all nodes
    public ArrayList<Node> removeNodeAll(ArrayList<Node> list, Node n){
        int index = findNode(list, n);
        list.remove(index);
        for (Node node : list){
            ArrayList<Node> adj = node.getAdj();
            int k = findNode(adj, n);
            if(k>-1){
                adj.remove(k);
            }
        }
        return list;
    }

    public int sumDegree(ArrayList<Node> list){
        int sum = 0;
        for (Node node : list){
            sum += (node.getAdj().size()-1);
        }
        return sum;
    }

    //To inoculate a network is to detect those nodes, or graph vertices, that exert the maximum influence on the whole network.
    public ArrayList<Node> inoculate(ArrayList<Node> list, int num){
        ArrayList<Node> output = new ArrayList<>();
        int count = 0;
        while(count < num){
            Node maxNode = maxDeg(list);
            output.add(getNode(list, maxNode));
            removeNodeAll(list, maxNode);
            count++;
        }
        return output;
    }

    //prints countDeg
    public void outputDegree(ArrayList<Node> list){
        for(int i = 0; i<list.size(); i++){
            Node n = list.get(i);
            System.out.print(n.getValue() + " " + countDeg(n));
            System.out.println();
        }
    }

    //Ball(i, r) is the set of nodes whose shortest path from node i does not exceed r.
    public ArrayList<Node> ball(Node s, int r){
        LinkedList<Node> q = new LinkedList<>();
        HashMap<Node, Boolean> visted = new HashMap<>();
        ArrayList<Node> circle = new ArrayList<>();

        visted.put(s, true);

        q.add(s);

        while(!q.isEmpty() && r>0){
            s = q.poll();
            Iterator<Node> i = s.getAdj().listIterator();
            while(i.hasNext()){
                Node v = i.next();
                if(!visted.containsKey(v)){
                    visted.put(v, true);
                    q.add(v);
                    circle.add(v);
                }
            }
            r--;
        }
        return circle;
    }

    //δBall(i, r) is the set of nodes whose shortest path from node i is exactly r.
    public ArrayList<Node> sigma(Node s, int r){
        LinkedList<Node> q = new LinkedList<>();
        HashMap<Node, Boolean> visted = new HashMap<>();
        ArrayList<Node> circle = new ArrayList<>();
        visted.put(s, true);
        q.add(s);

        while(!q.isEmpty() && r>0){
            s = q.poll();
            Iterator<Node> i = s.getAdj().listIterator();
            while(i.hasNext()){
                Node v = i.next();
                if(!visted.containsKey(v)){
                    visted.put(v, true);
                    q.add(v);
                    if(r==1) circle.add(v);
                }
            }
            r--;
        }
        return circle;
    }

    //collective influence of every node
    public int colInfluence(Node n, int r){
        ArrayList<Node> circle = sigma(n, r);
        int sum = sumDegree(circle);
        int currDegree = countDeg(n)-1;
        int ci = currDegree*sum;
        return ci;
    }

    //most influence
    public String mostInfluence(HashMap<String, Integer> map){
        int temp = -1;
        String node = "";
        for(String n : map.keySet()){
            int curr = map.get(n);
            if(curr > temp){
                temp = curr;
                node = n;
            }
        }
        return node;
    }

    //dismantle a network
    public ArrayList<Node> dismantle(ArrayList<Node> list, int r, int num_nodes){
        HashMap<String, Integer> map = new HashMap<>();
        while(!list.isEmpty() && num_nodes > 0){
            num_nodes--;
            for (Node node : list){
                int ci = colInfluence(node,r);
                map.put(node.getValue(), ci);
            }

            String maxValue = mostInfluence(map);
            Node maxNode = new Node(maxValue);
            System.out.println(maxValue + " " + map.get(maxValue));
            map.remove(maxValue);
            removeNodeAll(list, maxNode);

            ArrayList<Node> affectNodes = ball(maxNode, r+1);
            for (Node node : affectNodes){
                int ci = colInfluence(node,r);
                map.put(node.getValue(), ci);
            }
        }
        return list;
    }
}
