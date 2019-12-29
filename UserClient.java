package main;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.json.JSONObject;

import edu.princeton.cs.algs4.Stack;

/** This program should be used be the user.
 * It has GUI interface that is used to setup the program and allow the search between
 * Wikipedia pages.
 */
public class UserClient extends JFrame {

    private JPanel contentPane;
    private JButton btnPage;
    final JFileChooser fc = new JFileChooser();
    private String pagePath;
    private String linkPath;
    private JButton btnLinks;
    private JButton btnParse;
    private boolean dataIsParsed = false;
    private int highestID = -1;
    private int numOfPages = -1;
    private JTextField tfFrom;
    private JTextField tfTo;
    private WikiGraph graph;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    DataParser.startup();
		    UserClient frame = new UserClient();
		    frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */
    public UserClient() {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 580, 326);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);
	dataIsParsed = DataParser.dataIsParsed;
	
	
    	if(!dataIsParsed){
        	btnPage = new JButton("Choose Page File");
        	btnPage.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent e) {
        		    int returnVal = fc.showOpenDialog(UserClient.this);
        		   if (returnVal == JFileChooser.APPROVE_OPTION) {
        		       File file = fc.getSelectedFile();
        		       try {
        			   pagePath = file.getCanonicalPath();
        		       } catch (IOException e1) {
        			   // TODO Auto-generated catch block
        			   e1.printStackTrace();
        		       }
        		       //This is where a real application would open the file.
        		       System.out.println("Opening: " + file.getName() + ".");
        		   } else {
        		       System.out.println("Open command cancelled by user." );
        		   }
        		}
        	});
        	btnPage.setBounds(10, 7, 214, 23);
        	contentPane.add(btnPage);
        	
        	btnLinks = new JButton("Choose File Links");
        	btnLinks.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent e) {
        		   int returnVal = fc.showOpenDialog(UserClient.this);
        		   if (returnVal == JFileChooser.APPROVE_OPTION) {
        		       File file = fc.getSelectedFile();
        		       try {
        			   linkPath = file.getCanonicalPath();
        		       } catch (IOException e1) {
        			   // TODO Auto-generated catch block
        			   e1.printStackTrace();
        		       }
        		       //This is where a real application would open the file.
        		       System.out.println("Opening: " + file.getName() + ".");
        		   } else {
        		       System.out.println("Open command cancelled by user." );
        		   }
        		}
        	});
        	btnLinks.setBounds(10, 41, 214, 23);
        	contentPane.add(btnLinks);
        	
        	btnParse = new JButton("Parse Data");
        	btnParse.addActionListener(new ActionListener() {
        		@Override
        		public void actionPerformed(ActionEvent e) {
        		    dataIsParsed = DataParser.parseData(pagePath, linkPath);
        		    highestID = DataParser.highestID;
        		    numOfPages = DataParser.countOfNormalPages;
        		}
        	});
        	btnParse.setBounds(10, 75, 214, 23);
        	contentPane.add(btnParse);
	}else{
	    graph = new WikiGraph(DataParser.linksPath);
	    
	    JLabel lblNewLabel = new JLabel("Path between 2 Articles:");
	    lblNewLabel.setBounds(10, 11, 544, 14);
	    contentPane.add(lblNewLabel);

	    JLabel lblNewLabel_1 = new JLabel("From:");
	    lblNewLabel_1.setBounds(10, 64, 46, 14);
	    contentPane.add(lblNewLabel_1);

	    tfFrom = new JTextField();
	    tfFrom.setBounds(90, 33, 181, 20);
	    contentPane.add(tfFrom);
	    tfFrom.setColumns(10);

	    JLabel lblTo = new JLabel("To:");
	    lblTo.setBounds(10, 36, 46, 14);
	    contentPane.add(lblTo);

	    tfTo = new JTextField();
	    tfTo.setBounds(90, 64, 181, 20);
	    contentPane.add(tfTo);
	    tfTo.setColumns(10);
	    
	    JLabel result = new JLabel("");
	    result.setBounds(10, 90, 400, 20);
	    contentPane.add(result);

	    JButton btnSart = new JButton("Let's Go");
	    btnSart.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
		    int from = getID(tfFrom.getText());
		    int to = getID(tfTo.getText());
		    Stack<Integer> stack = graph.search(from, to);
		    StringBuilder temp = new StringBuilder();
		    for(Integer integer : stack){
			temp.append(getArticle(integer)+ "->");
		    }
		    String text = temp.toString();
		    text = text.substring(0, text.length()-2);
		    result.setText(text);
		    
		}
	    });
	    btnSart.setBounds(281, 33, 89, 51);
	    contentPane.add(btnSart);
	}
	
	this.addWindowListener(new WindowAdapter()
	{
	    @Override
	    public void windowClosing(WindowEvent e)
	    {
		if(dataIsParsed){
		    DataParser.logData();
		}
	    }
	});
    }
    
    public static int getID(String article){
	article = article.replaceAll(" ", "%20");
	StringBuilder temp = null;
	Scanner scanner = null;
	try{     
	    	String staticURL = "https://de.wikipedia.org/w/api.php?action=query&titles=";
	    	URL url = new URL(staticURL + article + "&format=json");
	    	scanner = new Scanner(url.openStream());
        	temp = new StringBuilder();
        	while(scanner.hasNextLine()){
        	    temp.append(scanner.nextLine());
        	}
        	//I can't use JSON Object here because i can't select first child
        	String json = temp.toString();
        	if(!json.contains("\"pages\":{\"-1\":")){//Page actually exists
        	    System.out.println(temp);
        	    int start = json.indexOf("\"pageid\":");
        	    start += 9;
        	    int end = json.indexOf(",", start);
        	    String id = json.substring(start, end);
        	    return Integer.parseInt(id);
        	    
        	}
        	
        	staticURL = "https://de.wikipedia.org/w/api.php?action=query&list=search&srsearch=";
        	url = new URL(staticURL + article + "&format=json");
        	scanner = new Scanner(url.openStream());
        	temp = new StringBuilder();
        	while(scanner.hasNextLine()){
        	    temp.append(scanner.nextLine());
        	}
	}catch(Exception ex){
	    ex.printStackTrace();
	}finally{
	    if(scanner != null){
		try{
		    scanner.close();
		}catch(Exception ex){
		    ex.printStackTrace();
		}
	    }
	}
	return new JSONObject(temp.toString()).getJSONObject("query").getJSONArray("search").getJSONObject(0).getInt("pageid");
	
    }
    
    public static String getArticle(int id ){
	StringBuilder temp = new StringBuilder();
	Scanner scanner = null;
	try{
	    String staticURL = "https://de.wikipedia.org/w/api.php?action=query&pageids=";
	    URL url = new URL(staticURL + id + "&format=json");
	    scanner = new Scanner(url.openStream());
	    while(scanner.hasNextLine()){
		temp.append(scanner.nextLine());
	    }
	    JSONObject jsonObject = new JSONObject(temp.toString());
	    return jsonObject.getJSONObject("query").getJSONObject("pages").getJSONObject(id + "").getString("title");
	}catch(Exception ex){
	    ex.printStackTrace();
	}finally{
	    if(scanner != null){
		try{
		    scanner.close();
		}catch(Exception e){
		    e.printStackTrace();
		}
	    }
	}
	return "";
    }
}
