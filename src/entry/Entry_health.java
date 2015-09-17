package entry;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import christen.Parameters;

import phases.Phase2;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;
import featureSelection.GenerateFeatures;
import general.CSVParser;

public class Entry_health {

	
	public static void main(String[] args) throws IOException{
		
		//Phase1_mod();
		//Phase2();
		//EvalBK();
		//analyze_feats();
		//printDuplicateIndices();
		//evalClassifiedPairs();
		classifyPairs();
	}
	
	public static void EvalBK()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"healthcare_mod.csv",prefix+"npi_mod.csv",prefix+"goldStandard_healthcare_npi");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		block.print_metrics();
	}
	
	
	public static void Phase2()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		game.cleanUp();
		String bk=GenerateBK.generateHeteroBKString(game.prunedDupFeats,game.prunedNondupFeats,2);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
	}
	
	public static void Phase1_mod()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		new phases.Phase1_mod(prefix+"healthcare_mod.csv",prefix+"npi_mod.csv",prefix+"features");
	}
	
	public static void analyze_feats()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		game.cleanUp();
		//String bk=GenerateBK.generateHeteroBKString(game.prunedDupFeats,game.prunedNondupFeats,11);
		
		PrintWriter out=new PrintWriter(new File(prefix+"Classifier"));
		out.println(calcMinAvgMax(game.prunedDupFeats));
		out.println(calcMinAvgMax(game.prunedNondupFeats));
		out.close();
	}
	
	public static void classifyPairs()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		
		//read in classifier in threshold
		Scanner in=new Scanner(new FileReader(prefix+"Classifier"));
		double threshold=0.0;
		if(in.hasNextLine())
			threshold=Double.parseDouble(in.nextLine().split(" ")[1])/3.0;
		in.close();
		
		//read in tuples
		ArrayList<String> tuples1=new ArrayList<String>();
		ArrayList<String> tuples2=new ArrayList<String>();
		in=new Scanner(new FileReader(prefix+"healthcare_mod.csv"));
		while(in.hasNextLine())
			tuples1.add(in.nextLine());
		in.close();
		in=new Scanner(new FileReader(prefix+"npi_mod.csv"));
		while(in.hasNextLine())
			tuples2.add(in.nextLine());
		in.close();
		
		//find duplicates in blocks
		PrintWriter out=new PrintWriter(new File(prefix+"duplicates"));
		int count=0;
		
		EvaluateBK p=new EvaluateBK(prefix+"BK",tuples1,tuples2);
		HashMap<Integer, HashSet<Integer>> pairs=p.return_pairs();
		int total=0;
		for(int m:pairs.keySet())
			total+=pairs.get(m).size();
		System.out.println("Total: "+total);
		for(int i: pairs.keySet())
			for(int j: pairs.get(i)){
				count++;
				System.out.println(count+"...");
				if(alphaNumericClassifier(tuples1.get(i),tuples2.get(j)))
				{	
					
					out.println(tuples1.get(i));
					out.println(tuples2.get(j));
					out.println();
				}
			}
		out.close();
		
	}
	
	private static String calcMinAvgMax(ArrayList<ArrayList<Integer>> feats){
		double min=0;
		double max=0;
		double avg=0;
		for(ArrayList<Integer> feat:feats){
				int sum=0;
				for(int f:feat)
					sum+=f;
				avg+=sum;
				if(sum<min)
					min=sum;
				if(sum>max)
					max=sum;
			}
		if(avg!=0)
			avg/=feats.size();
		String result=min+" "+avg+" "+max;
		return result;
	}

	public static void printDuplicateIndices()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		
		//read in classifier in threshold
		Scanner in=new Scanner(new FileReader(prefix+"Classifier"));
		double threshold=0.0;
		if(in.hasNextLine())
			threshold=Double.parseDouble(in.nextLine().split(" ")[1])/3.0;
		in.close();
		threshold=0.1;
		//read in tuples
		ArrayList<String> tuples1=new ArrayList<String>();
		ArrayList<String> tuples2=new ArrayList<String>();
		in=new Scanner(new FileReader(prefix+"healthcare_mod.csv"));
		while(in.hasNextLine())
			tuples1.add(in.nextLine());
		in.close();
		in=new Scanner(new FileReader(prefix+"npi_mod.csv"));
		while(in.hasNextLine())
			tuples2.add(in.nextLine());
		in.close();
		
		//find duplicates in blocks
		PrintWriter out=new PrintWriter(new File(prefix+"duplicates_index"));
		int count=0;
		
		EvaluateBK p=new EvaluateBK(prefix+"BK",tuples1,tuples2);
		HashMap<Integer, HashSet<Integer>> pairs=p.return_pairs();
		int total=0;
		for(int m:pairs.keySet())
			total+=pairs.get(m).size();
		System.out.println("Total: "+total);
		for(int i: pairs.keySet())
			for(int j: pairs.get(i)){
				count++;
				System.out.println(count+"...");
				if(alphaNumericClassifier(tuples1.get(i),tuples2.get(j)))
				{	
					
					out.println(i+" "+j);
					
					
				}
			}
		out.close();
		
	}
	
	public static void evalClassifiedPairs()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		ArrayList<String> gold=new ArrayList<String>();
		
		Scanner in=new Scanner(new File(prefix+"goldStandard_healthcare_npi"));
		while(in.hasNextLine())
			gold.add(in.nextLine());
		
		in.close();
		
		int count=0;
		int correct=0;
		in=new Scanner(new File(prefix+"duplicates_index"));
		while(in.hasNextLine()){
			count++;
			if(gold.contains(in.nextLine()))
				correct++;
		}
		in.close();
		System.out.println("Recall: "+(1.0*correct/gold.size()));
		System.out.println("Precision: "+(1.0*correct/count));
	}

	private static boolean isAlphaNumeric(String token){
		if(!(token.contains("0")||token.contains("1")||token.contains("2")||token.contains("3")||
				token.contains("7")||token.contains("6")||token.contains("5")||token.contains("4")||
						token.contains("8")||token.contains("9")))
			return false;
		if(!isInteger(token))
			return true;
		else
			return false;
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
	
	public static boolean alphaNumericClassifier(String tuple1, String tuple2)throws IOException{
		CSVParser t1=new CSVParser();
		
		//HashSet<String> f1=new HashSet<String>();
		for(String d1:t1.parseLine(tuple1))
			{
			String[] tokens=d1.split(Parameters.splitstring);
			for(String token:tokens)
				if(isAlphaNumeric(token))
					if(tuple2.contains(token))
						return true;
			}
		return false;
		
		
	}
}
