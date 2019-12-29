package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Pattern;




public class PagesParser {
    static int countOfNormalPages = 0;
    static int highestID = 0;
    
    static class Page{
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
    
    public static void main(String[] args){
	String path = "C:\\Users\\Basti\\Documents\\wikilinks\\dewiki-20180501-page.sql";
	String writePath = "C:\\Users\\Basti\\Documents\\wikilinks\\parsed\\page.txt";
	BufferedReader reader = null;
	BufferedWriter writer = null;
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
	    System.out.print("The End!");
	    System.out.println(countOfNormalPages);
	    System.out.println(highestID);
	    writer = new BufferedWriter(new FileWriter(writePath));
	    while(!pages.isEmpty()){
		Page currPage = pages.pop();
		if(currPage.pageID == -1) continue;
		String temp = new String(currPage.title.getBytes(), "UTF-8");
		writer.write(currPage.pageID + "," + temp);
		writer.newLine();
	    }
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
