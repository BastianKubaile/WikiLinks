package main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class WikiGraph {
    HashMap<Integer, int[]> adj = null;
    
    public WikiGraph(String readPath){
	adj = new HashMap<>(4530293);
	BufferedReader reader = null;
	try{
	    reader = new BufferedReader(new FileReader(readPath));
	    String curr = null;
	    int row = 0;
	    while(reader.ready()){
		row++;
		curr = reader.readLine();
		String[] ids = curr.split(Pattern.quote(",")); 
		Integer from = Integer.parseInt(ids[0]);
		int to = -1;
		int[] temp = new int[ids.length-1];
		for(int i = 1; i < ids.length; i++){
		    to = Integer.parseInt(ids[i]);
		    temp[i-1] = to;
		}
		if(adj.get(from) == null){
		    adj.put(from, temp);
		}
		if(row % 100000 == 0){
		    System.gc();
		    System.out.println(row +  " is the current row!");
		}
	    }
	}catch(IOException ex){
	    ex.printStackTrace();
	}finally{
	    if(reader != null){
		try{
		    reader.close();
		}catch(IOException ex){
		    ex.printStackTrace();
		}
	    }
	}
	System.out.println("Initialization endded!");
    }
    
    //Returns a way from pageID from to pageID to
    public Stack<Integer> search(int from, int to){
	
	Stack<Integer> result = new Stack<Integer>();
	boolean[] visitted = new boolean[10324280];
	class Node{
	    Node from; 
	    int id;
	    
	    public Node(Node from, int id){
		this.from = from;
		this.id = id;
	    }
	}
	Queue<Node> currItems = new Queue<>();
	currItems.enqueue(new Node(null, from));
	OUTER_LOOP: while(true){
	    	Node currNode = currItems.dequeue();
	    	int fromCurrNode = currNode.id;
	    	if(currNode.id == 68985){
	    	    System.out.println("We are at FormalAldehyd!");
	    	}
	    	if(currNode.id == 14217){
	    	    System.out.println("We are at Chemische Verbindung!");
	    	}
	    	if(currNode.id == 899){
	    	    System.out.println("We are at Chemisches Element!");
	    	}
	    	if(currNode.id == 12163){
	    	    System.out.println("We are at Chemische Reaktion!");
	    	}
	    	if(currNode.id == 5052){
	    	    System.out.println("We are at Thermodynamik!");
	    	}
	    	if(currNode.id == 9076889){
	    	    System.out.println("We are at Thermodynamik!");
	    	}
	    	int[] toCurrNode = adj.get(fromCurrNode);
	    	if(toCurrNode == null){
	    	    System.out.println(currNode.from  + " "  + currNode.id);
	    	    System.out.println(fromCurrNode);
	    	}
	    	for (int i = 0; i < toCurrNode.length; i++) {
	    	    if(toCurrNode[i] == to){
	    		result.push(to);
	    		while(currNode != null){
	    		    result.push(currNode.id);
	    		    currNode = currNode.from;
	    		}
	    		break OUTER_LOOP;
	    	    }
	    	    if(visitted[toCurrNode[i]]) continue;
		    currItems.enqueue(new Node(currNode, toCurrNode[i]));
		    visitted[toCurrNode[i]] = true;
		}
	}
	return result;
    }
    
    public static void main(String[] args) throws InterruptedException{
	new WikiGraph("C:\\Users\\Basti\\Documents\\wikilinks\\parsed\\temp.txt").test();
	
    }
    
    public void test() throws InterruptedException{
	System.out.println("Testing started!");
	Stack<Integer> res = this.search(2055060, 9076889);
	while(!res.isEmpty()){
	    System.out.println(res.pop());
	}
    }

}
