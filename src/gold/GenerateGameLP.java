package gold;

import general.CSVParser;

import java.io.*;
import java.util.*;

public class GenerateGameLP {

	/**
	 * Generate Gold standard iff 
	 */
	
	static String prefix="/host/heteroDatasets/iswc_experiments/game/";
	public static void main(String[] args)throws IOException {
		//printColumn2VGChartz();
		generateGoldLinkSpec1();

	}
	
	//link spec1
	private static void generateGoldLinkSpec1()throws IOException{
		Scanner vg=new Scanner(new FileReader(prefix+"vgchartz.csv"));
		Scanner db=new Scanner(new FileReader(prefix+"dbpedia.csv"));
		HashMap<String, Integer> db2=new HashMap<String,Integer>();
		HashMap<String, Integer> vg2=new HashMap<String,Integer>();
		int count=0;
		while(vg.hasNextLine()){
			String line=vg.nextLine();
			String t2=(new CSVParser()).parseLine(line)[2].toLowerCase();
			if(t2.contains("xb")||t2.contains("x360"))
				vg2.put(line,count);
			count++;
		}
		vg.close();
		count=0;
		while(db.hasNextLine()){
			String line=db.nextLine();
			String t2=(new CSVParser()).parseLine(line)[2].toLowerCase();
			if(t2.contains("xbox"))
				db2.put(line,count);
			count++;
		}
		
		
		
		db.close();
		
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard_vg_db_ls1"));
		for(String v:vg2.keySet()){
			String[] t5=(new CSVParser()).parseLine(v)[5].toLowerCase().split(",");
			for(String d:db2.keySet()){
				String[] t3=(new CSVParser()).parseLine(d)[3].toLowerCase().split(",");
				if(containsCommonElement(t5,t3))
					out.println(vg2.get(v)+" "+db2.get(d));
			}
		}
		out.close();
	}
	
	
	private static boolean containsCommonElement(String[] a, String[] b){
		for(String a1: a)
			for(String b1: b)
				if(a1.equals(b1))
					return true;
		return false;
	}
	
	//starting count from 0, print col 2.
	private static void printColumn2VGChartz()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"vgchartz.csv"));
		HashSet<String> platforms=new HashSet<String>();
		System.out.println("I'm here");
		while(in.hasNextLine()){
			//System.out.println("here");
			String line=in.nextLine();
			//System.out.println(line);
			CSVParser p=new CSVParser();
			String q=p.parseLine(line)[5];
			String[] t=q.split(",");
			for(String tt:t)
				platforms.add(tt.trim());
			
		}
		
		in.close();
		printSortedSet(platforms);
	}

	
	private static void printSortedSet(HashSet<String> set){
		ArrayList<String> array=new ArrayList<String>(set);
		Collections.sort(array);
		for(String p:array)
			System.out.println(p);
	}
	
}
