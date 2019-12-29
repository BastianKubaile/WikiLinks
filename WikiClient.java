package main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.princeton.cs.algs4.Stack;

public class WikiClient {
    static WikiGraph graph = new WikiGraph();
    
    public static void path(int from, int to, String file){
	Stack<Integer> stack = graph.search(from, to);
	BufferedWriter writer = null;
	try{
	    writer = new BufferedWriter(new FileWriter(file));
	    while(!stack.isEmpty()){
		writer.write(stack.pop() + "");
	    }
	}catch(IOException ex){
	    ex.printStackTrace();
	}finally{
	    if(writer != null){
		try{
		    writer.close();
		}catch(IOException ex){
		    ex.printStackTrace();
		}
	    }
	}
    }
    
    public static String path(int from, int to){
	StringBuffer temp = new StringBuffer();
	Stack<Integer> stack = graph.search(from, to);
	while(!stack.isEmpty()){
	    temp.append(stack.pop() + "");
	}
	return temp.toString();
    }
    
    public static void main(String[] args){
	System.out.println(path(2055060, 9076889));
    }

}
