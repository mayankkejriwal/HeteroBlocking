package gold;

import java.io.*;
import java.util.*;

public class PreprocessDbpediaBook {
	
	public static void main(String[] args)throws IOException{
		check();
	}
	
	public static void check()throws IOException{
		String dbpedia="/host/heteroDatasets/books/dbpediaMod3.csv";
		Scanner in=new Scanner(new FileReader(dbpedia));
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.split(",").length!=3)
				System.out.println(line);
		}
			
		in.close();
	}
	
	public static void legacy_3()throws IOException{
		String dbpedia="/host/heteroDatasets/books/dbpediaMod2.csv";
		Scanner in=new Scanner(new FileReader(dbpedia));
		PrintWriter out=new PrintWriter(new File("/host/heteroDatasets/books/dbpediaMod3.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			String[] tokens=line.split(",");
			if(tokens.length!=4)
				{System.out.println(line);
			continue;}
			
			String total=tokens[1]+","+tokens[2]+","+tokens[3];
			out.println(total);
			
		}
		
		in.close();
		out.close();
	}
	
	public static void legacy_2()throws IOException{
		String dbpedia="/host/heteroDatasets/books/dbpediaMod.csv";
		Scanner in=new Scanner(new FileReader(dbpedia));
		PrintWriter out=new PrintWriter(new File("/host/heteroDatasets/books/dbpediaMod2.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			line=line.replace("http://dbpedia.org/resource/","");
			line=line.replace("http://dbpedia.org/property/","");
			line=line.replace("http://dbpedia.org/ontology/","");
			line=line.replace("http://www.w3.org/","");
			String[] tokens=line.split(",");
			String total="";
			for(String t:tokens){
				t=t.replace("\"","'");
				total=total+t+",";
			}
			out.println(total.substring(0,total.length()-1));
			
		}
		
		in.close();
		out.close();
	}
	
	public static void legacy_1()throws IOException{
		String dbpedia="/host/heteroDatasets/books/dbpedia.csv";
		Scanner in=new Scanner(new FileReader(dbpedia));
		PrintWriter out=new PrintWriter(new File("/host/heteroDatasets/books/dbpediaMod.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.substring(0,1).equals("\""))
				line=line.substring(0,line.length());
			if(line.substring(line.length()-1,line.length()).equals("\""))
				line=line.substring(0,line.length()-1);
			String[] tokens=line.split("\",\"|\",|,\"");
			String total="";
			if(tokens.length==1)
				total=line+",";
			else{
				
			for(int k=0; k<tokens.length; k++){
				String t=tokens[k];
				
				t=t.replace(",",";");
				if(k<=2)
				total=total+t+",";
				else
					total=total+t+";";
			}
			}
			out.println(total.substring(0,total.length()-1));
			
		}
		
		in.close();
		out.close();
	}

}
