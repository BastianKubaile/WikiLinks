package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LinkParser {
    
    public static void main(String[] args){
	String pagePath = "C:\\Users\\Basti\\Documents\\wikilinks\\parsed\\page.txt";
	String linkPath = "C:\\Users\\Basti\\Documents\\wikilinks\\dewiki-20180501-pagelinks.sql";
	String writePath = "C:\\Users\\Basti\\Documents\\wikilinks\\parsed\\links.txt";
	BufferedReader reader = null;
	BufferedWriter writer = null;
	
	try{
	    reader = new BufferedReader(new FileReader(pagePath));
	    HashMap<String, Integer> idOfTitle = new HashMap<>(4000000);
	    boolean[] pageIdExists = new boolean[10324280];
	    while(reader.ready()){
		String currLine = reader.readLine();
		int del = currLine.indexOf(",");
		int pageID = Integer.parseInt(currLine.substring(0, del));
		String title = new String(currLine.substring(del+1, currLine.length()).getBytes(), "UTF-8");
		if(pageID==1459741){
		    System.out.println("HI");
		    System.out.println(currLine);
		}
		pageIdExists[pageID] = true;
		idOfTitle.put(title, pageID);
	    }
	    System.out.println(pageIdExists[1035836]);
	    System.out.println(pageIdExists[1459741]);
	    System.out.println(idOfTitle.get("Likörwein"));
	    System.out.println(idOfTitle.get("Aufspritung"));

	    try{
		reader.close();
	    }catch(IOException ex){
		ex.printStackTrace();
	    }
	    reader = new BufferedReader(new FileReader(linkPath));
	    writer = new BufferedWriter(new FileWriter(writePath));
	    for(int i = 0; i < 40; i++){
		reader.readLine();
	    }
	    int row = 1;
	    while(reader.ready()){
		String currLine = reader.readLine();
		if(currLine.length() < 20 || !currLine.substring(0, 31).equals("INSERT INTO `pagelinks` VALUES ")){
		    System.out.println("END: " + currLine);
		    break;
		}
		currLine = currLine.substring(32, currLine.length());
		String[] values = currLine.split(Pattern.quote("),("));
		values[values.length-1] = values[values.length-1].substring(0, values[values.length-1].length()-2);// Removes ; of last element 
		for(int i = 0; i < values.length; i++){
		    try{			
        		    String temp = values[i];
        		    int from = Integer.parseInt(temp.substring(0, temp.indexOf(",")));
        		    temp = temp.substring(temp.indexOf(",")+1, temp.lastIndexOf(","));
        		    temp = temp.substring(temp.indexOf(",")+2, temp.length()-1);
        		    Integer to = idOfTitle.get(temp);
        		    if(from < pageIdExists.length && pageIdExists[from] && to != null){
        			writer.write(from + "");
        			writer.write(",");
        			writer.write(to.intValue() + "");
        			writer.newLine();
        		    }
		    }catch(Exception ex){
			// This exception parsing with ),( doesn't work on all values, so this exception happens, but the values wouldn't get sotred anyway 
			// so ...
			System.out.println("Exception");
			System.out.println("Current value: " + values[i]);
			ex.printStackTrace();
		    }
		}
		System.out.println(row++ + "Is the current row!");
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
	    if(writer != null){
		try{
		    writer.close();
		}catch(IOException ex){
		    ex.printStackTrace();
		}
	    }
	}
    }

}
