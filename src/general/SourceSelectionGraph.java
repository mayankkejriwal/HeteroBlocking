package general;

import java.io.*;
import java.util.*;

import christen.Parameters;

public class SourceSelectionGraph {

	static String prefix="/host/heteroDatasets/iswc_experiments/government/ss/";
	String folderpath1;	//must have / at the end
	String folderpath2;
	File[] files1;
	File[] files2; //collection of files in the folder
	
	double[][] scores;	//symmetric matrix, diagonal contains +1 since other entries are either neg or 0
	
	public SourceSelectionGraph(String folder1, String folder2)throws IOException{
		folderpath1=folder1;
		folderpath2=folder2;
		files1=(new File(folder1)).listFiles();
		files2=(new File(folder2)).listFiles();
		scores=new double[files1.length][files2.length];
		populateScoreMatrix();
	}
	
	//but with negative scores! To run minHungarian...
	private void populateScoreMatrix()throws IOException{
		for(int i=0; i<scores.length; i++)
			for(int j=0; j<scores[i].length; j++)
				scores[i][j]=-1*computeScore(i,j);
			
		
				
		
	}
	
	public int[] runHungarian(){
		HungarianAlgorithm p=new HungarianAlgorithm(scores);
		int[] l=p.execute();
		return l;
	}
	
	public void printMatchOutput(int[] l){
		for(int i=0; i<files1.length; i++){
			System.out.println(files1[i].getName()+"\t"+files2[l[i]].getName());
		}
	}
	
	
	private double computeScore(int i, int j)throws IOException{
		Scanner in=new Scanner(files1[i]);
		HashMap<String,Double> doc1_counts=new HashMap<String,Double>();
		HashMap<String,Double> target1_counts=new HashMap<String,Double>();
		
		
		while(in.hasNextLine()){
			String line=in.nextLine();
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
		in.close();
		normalize(doc1_counts);
		
		in=new Scanner(files2[i]);
		while(in.hasNextLine()){
			String line=in.nextLine();
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
		in.close();
		normalize(target1_counts);
		
		double score=0.0;
		
		for(String key:doc1_counts.keySet())
			if(target1_counts.containsKey(key))
				score+=(target1_counts.get(key)*doc1_counts.get(key));
		
		return score;
	}
	
	private void normalize(HashMap<String,Double> doc_counts){
		double total=0.0;
		for(String key:doc_counts.keySet()){
			total+=doc_counts.get(key);
		}
		
		for(String key:doc_counts.keySet()){
			doc_counts.put(key,doc_counts.get(key)/total);
		}
		
	}
	
	public static void main(String[] args)throws IOException {
		SourceSelectionGraph g=new SourceSelectionGraph(prefix+"jct/",prefix+"treasury/");
		System.out.println("Graph created");
		g.printMatchOutput(g.runHungarian());

	}

}
