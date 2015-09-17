package gold;

import general.CSVParser;

import java.io.*;
import java.util.*;

import christen.Parameters;
public class GenerateColombia {

	static String prefix="/host/heteroDatasets/iswc_experiments/Venezuela/";
	
	
	public static void orig()throws IOException{
		Scanner in=new Scanner(new File(prefix+"coding1_prop.csv"));
		HashMap<Integer,HashSet<Integer>> index=new HashMap<Integer,HashSet<Integer>>();
		int count=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			CSVParser p=new CSVParser();
			//System.out.println(count);
			String l=p.parseLine(line)[3];
			String[] tokens=l.split(Parameters.splitstring);
			for(String token:tokens)
				if(isInteger(token)){
					if(!index.containsKey(Integer.parseInt(token)))
						index.put(Integer.parseInt(token),new HashSet<Integer>());
					index.get(Integer.parseInt(token)).add(count);
				}
			count++;
		}
		in.close();
		in=new Scanner(new File(prefix+"coding2_prop.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard"));
		count=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			CSVParser p=new CSVParser();
			String l=p.parseLine(line)[2];
			String[] tokens=l.split(Parameters.splitstring);
			for(String token:tokens)
				if(isInteger(token)){
					if(index.containsKey(Integer.parseInt(token)))
						for(int i:index.get(Integer.parseInt(token)))
							out.println(i+" "+count);
				}
			count++;
		}
		
		 
		in.close();
		out.close();
	
	}
	public static void main(String[] args) throws IOException {
		
		orig_ss();
		
	}
	
	
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	public static void orig_ss()throws IOException{
		
		
		Scanner in=new Scanner(new File(prefix+"coding1_prop.csv"));
		HashMap<Integer,HashSet<Integer>> index=new HashMap<Integer,HashSet<Integer>>();
		int count=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			CSVParser p=new CSVParser();
			String l=p.parseLine(line)[2];
			String[] tokens=l.split(Parameters.splitstring);
			for(String token:tokens)
				if(isInteger(token)){
					if(!index.containsKey(Integer.parseInt(token)))
						index.put(Integer.parseInt(token),new HashSet<Integer>());
					index.get(Integer.parseInt(token)).add(count);
				}
			count++;
		}
		in.close();
		in=new Scanner(new File(prefix+"coding2_prop_7.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard_n_7"));
		count=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			CSVParser p=new CSVParser();
			String l=p.parseLine(line)[2];
			String[] tokens=l.split(Parameters.splitstring);
			for(String token:tokens)
				if(isInteger(token)){
					if(index.containsKey(Integer.parseInt(token)))
						for(int i:index.get(Integer.parseInt(token)))
							out.println(i+" "+count);
				}
			count++;
		}
		
		 
		in.close();
		out.close();
	
	}

}
