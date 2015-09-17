package general;

import java.util.*;
import java.io.*;

import christen.Parameters;

public class SourceSelection {

	HashMap<String,Double> doc1_counts;
	HashMap<String,Double> target1_counts;
	HashMap<String,Double> target2_counts;
	boolean target1;
	
	public SourceSelection(String file1, String target1, String target2)throws IOException{
		Scanner in1=new Scanner(new File(file1));
		doc1_counts=new HashMap<String,Double>();
		target1_counts=new HashMap<String,Double>();
		target2_counts=new HashMap<String,Double>();
		while(in1.hasNextLine()){
			String line=in1.nextLine();
			String[] tokens=(new CSVParser()).parseLine(line);
			for(String token:tokens){
				String[] t=token.toLowerCase().split(Parameters.splitstring);
				for(String tt:t){
					if(tt.equals("null"))
						continue;
					if(!doc1_counts.containsKey(tt))
						doc1_counts.put(tt,0.0);
					doc1_counts.put(tt,doc1_counts.get(tt)+1);
				}
			}
		}
		
		in1.close();
		
		in1=new Scanner(new File(target1));
		while(in1.hasNextLine()){
			String line=in1.nextLine();
			String[] tokens=(new CSVParser()).parseLine(line);
			for(String token:tokens){
				String[] t=token.toLowerCase().split(Parameters.splitstring);
				for(String tt:t){
					if(tt.equals("null"))
						continue;
					if(!target1_counts.containsKey(tt))
						target1_counts.put(tt,0.0);
					target1_counts.put(tt,target1_counts.get(tt)+1);
				}
			}
		}
		
		in1.close();
		
		in1=new Scanner(new File(target2));
		while(in1.hasNextLine()){
			String line=in1.nextLine();
			String[] tokens=(new CSVParser()).parseLine(line);
			for(String token:tokens){
				String[] t=token.toLowerCase().split(Parameters.splitstring);
				for(String tt:t){
					if(tt.equals("null"))
						continue;
					if(!target2_counts.containsKey(tt))
						target2_counts.put(tt,0.0);
					target2_counts.put(tt,target2_counts.get(tt)+1);
				}
			}
		}
		
		in1.close();
		normalize();
		makeDecision();
	}
	
	private void normalize(){
		double total=0.0;
		for(String key:doc1_counts.keySet()){
			total+=doc1_counts.get(key);
		}
		
		for(String key:doc1_counts.keySet()){
			doc1_counts.put(key,doc1_counts.get(key)/total);
		}
		
		total=0.0;
		for(String key:target1_counts.keySet()){
			total+=target1_counts.get(key);
		}
		
		for(String key:target1_counts.keySet()){
			target1_counts.put(key,target1_counts.get(key)/total);
		}
		
		total=0.0;
		for(String key:target2_counts.keySet()){
			total+=target2_counts.get(key);
		}
		
		for(String key:target2_counts.keySet()){
			target2_counts.put(key,target2_counts.get(key)/total);
		}
		
	}
	
	private void makeDecision(){
		double score1=0.0;
		double score2=0.0;
		for(String key:doc1_counts.keySet()){
			if(target1_counts.containsKey(key))
				score1+=(target1_counts.get(key)*doc1_counts.get(key));
			if(target2_counts.containsKey(key))
				score2+=(target2_counts.get(key)*doc1_counts.get(key));
		}
		System.out.println(score1+" "+score2);
		if(score1<score2)
			target1=false;
		else
			target1=true;
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/iswc_experiments/Venezuela/";
		new SourceSelection(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv",prefix+"coding2_prop_7.csv");
	}
	
	
	
}
