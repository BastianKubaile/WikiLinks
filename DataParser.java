package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Pattern;


public class DataParser {
    public static int countOfNormalPages = 0;
    public static int highestID = 0;
    public static boolean dataIsParsed = false;
    public static String linksPath = null;
    
    private static class Page{
	int pageID;
	String title;
	
	public Page(String value){
	    String[] values = value.split(",");
	    boolean isNormalPage = true;
	    if(values.length != 15) {
		values = value.split(",(?=(?:[^\']*\'[^\']*\')*[^\']*$)");
		if(values.length != 15){
		    String temp = value;
		    temp = temp.replaceAll(",'", ",\"");
		    temp = temp.replaceAll("',", "\",");
		    values = temp.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		    if(values.length != 15){
			System.out.println(value);
			String[] oldValues = value.split(",");
			values = new String[15];
			values[0] = oldValues[0];
			values[1] = oldValues[1];
			values[2] = "";
			for(int i = 2;i < oldValues.length-12;i++){
			    values[2] += oldValues[i] + ",";
			}
			values[2] = values[2].substring(0, values[2].length()-1);
			
		    }
		}
	    }
	    
	    if(!values[1].equals("0")) isNormalPage = false;
	    if(isNormalPage){
		values[2] = values[2].substring(1, values[2].length()-1);
		pageID = Integer.parseInt(values[0]);
		title = values[2];
		if(pageID >highestID) highestID = pageID;
		countOfNormalPages++;
	    }else{
		pageID = -1;
		title = null;
	    }
	}
    }
    
    private static void setupFiles(){
	Path dir = Paths.get("./parsed");
	if(!Files.exists(dir)){
	    try {
		Files.createDirectories(dir);
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	Path[] files = new Path[2];
	files[0] = Paths.get("./parsed/linksReformed.txt");
	files[1] = Paths.get("./parsed/meta.txt");
	try{
	    for(Path p: files){
		if(!Files.exists(p)){
		    Files.createFile(p);
		}
	    }
	}catch(Exception ex){
	    ex.printStackTrace();
	}	
    }
    
    private static Stack<Page> pageParse(String path){
	BufferedReader reader = null;
	Stack<Page> pages = new Stack<>();
	try{
	    reader = new BufferedReader(new FileReader(path));
	    
	    for(int i = 0; i < 53 && reader.ready(); i++){
		reader.readLine();
	    }
	    while(reader.ready()){
        	    String line = reader.readLine();
        	    if(!line.substring(0, 27).equals("INSERT INTO `page` VALUES (")) break;
        	    line = line.substring(27, line.length());
        	    String[] split = line.split(Pattern.quote("),("));
        	    split[split.length-1] = split[split.length-1].substring(0, split[split.length-1].length()-2);
        	    for(int i = 0; i < split.length; i++){
        		pages.push(new Page(split[i]));
        	    }
	    }
	    System.out.println(countOfNormalPages);
	    System.out.println(highestID);
	    System.out.print("The End!"); 
	}catch(IOException ex){
	    ex.printStackTrace();
	}finally {
	    if(reader != null){
		try{
		    reader.close();
		}catch(IOException ex){
		    ex.printStackTrace();
		}
	    }
	}
	return pages;
    }
    
    private static void linkParse(Stack<Page> pages, String linkPath, String writePath ){
	BufferedWriter writer = null;
	BufferedReader reader = null;
	
	try{
	    HashMap<String, Integer> idOfTitle = new HashMap<>(countOfNormalPages+1000000);
	    boolean[] pageIdExists = new boolean[highestID+1];
	    while(!pages.isEmpty()){
		Page currPage = pages.pop();
		if(currPage.pageID < 0){
		    continue;
		}
		pageIdExists[currPage.pageID] = true;
		idOfTitle.put(currPage.title, currPage.pageID);
	    }
	    pages = null;
	    System.gc();

	    
	    
	    reader = new BufferedReader(new FileReader(linkPath));
	    for(int i = 0; i < 40; i++){
		reader.readLine();
	    }
	    int row = 1;
	    Stack<Integer>[] links = new Stack[highestID+1];
	    
	    
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
        			if(links[from]==null){
        			    links[from] = new Stack<Integer>();
        			}
        			links[from].push(to);
        		    }
		    }catch(Exception ex){
			// This exception parsing with ),( doesn't work on all values, so this exception happens, but the values wouldn't get sotred anyway 
			// so ...
			System.out.println("Exception");
			System.out.println("Current value: " + values[i]);
			ex.printStackTrace();
		    }
		}
		System.out.println(row++ + " is the current row!");
	    }
	    
	    writer = new BufferedWriter(new FileWriter(writePath));
	    for(int i = 0; i < links.length; i++){
		if(links[i] == null) continue;
		writer.write(i + "");
		while(!links[i].isEmpty()){
		    writer.write("," + links[i].pop());
		}
		writer.newLine();
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
    
    
    public static boolean parseData(String... args){
	if(dataIsParsed) return true;
	String pagePath = args[0];
	linksPath = args[1];
	DataParser.setupFiles();
	DataParser.linkParse(DataParser.pageParse(pagePath), linksPath, ".\\parsed\\linksReformed.txt");
	dataIsParsed = true;
	linksPath = ".\\parsed\\linksReformed.txt";
	return true;
    }
    
    public static void logData(){
	dataIsParsed = true;
	BufferedWriter writer = null;
	try{
	    writer = new BufferedWriter(new FileWriter(".\\parsed\\meta.txt"));
	    writer.write(dataIsParsed + "");
	    writer.newLine();
	    writer.write(countOfNormalPages + "");
	    writer.newLine();
	    writer.write(highestID + "");
	    writer.newLine();
	    writer.write(linksPath);
	    writer.newLine();
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
    
    public static void startup(){
	BufferedReader reader = null;
	setupFiles();
	try{
	    reader = new BufferedReader(new FileReader(".\\parsed\\meta.txt"));
	    String[] lines = new String[4];
	    for(int i = 0; i < lines.length;i++){
		if(!reader.ready()){
		    dataIsParsed = false;
		    return;
		}
		lines[i] = reader.readLine();
	    }
	    dataIsParsed = Boolean.parseBoolean(lines[0]);
	    countOfNormalPages = Integer.parseInt(lines[1]);
	    highestID = Integer.parseInt(lines[2]);
	    linksPath = lines[3];
	    
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
	
    }
    

}
