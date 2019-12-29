package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Pattern;

public class LinkReformer {

    public static void main(String[] args) {
	String readPath = "C:\\Users\\Basti\\Documents\\wikilinks\\parsed\\links.txt";
	String writePath = "C:\\Users\\Basti\\Documents\\wikilinks\\parsed\\temp.txt";
	/*Links reformed stores the links, displaying the pageid of 1 origin link and
	 * then giving all the pageids of the pages the first page is linking to.
	 */
	BufferedReader reader = null;
	BufferedWriter writer = null;
	try{
	    reader = new BufferedReader(new FileReader(readPath));
	    @SuppressWarnings("unchecked")
	    Stack<Integer>[] links = new Stack[10324280]; 
	    int row = 0;
	    while(reader.ready()){
		String currLine = reader.readLine();
		String[] temp = currLine.split(Pattern.quote(",")) ;
		int from = Integer.parseInt(temp[0]);
		int to = Integer.parseInt(temp[1]);
		if(links[from]==null){
		    links[from] = new Stack<>();
		}
		links[from].push(to);
		row++;
		if(row % 1000000 == 0){
		    System.gc();
		    System.out.println(row +  " is the current row!");
		}
	    }
	    
	    writer = new BufferedWriter(new FileWriter(writePath));
	    for(int i = 0; i < links.length;i++){
		Stack<Integer> s = links[i]; 
		if(s == null) continue;
		writer.write(i + "");
		for(Integer tmp: s){
		    writer.write("," + tmp);
		}
		writer.newLine();
	    }
	    
	}catch(IOException ex){
	    ex.printStackTrace();
	}finally{
	    if(reader!=null){
		try{
		    reader.close();
		}catch(IOException ex){
		    ex.printStackTrace();
		}
	    }
	    if(writer!=null){
		try{
		    writer.close();
		}catch(IOException ex){
		    ex.printStackTrace();
		}
	    }
	}

    }

}
