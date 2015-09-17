package entry;

import java.io.*;
import java.util.*;

import phases.Phase2;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;
import featureSelection.GenerateFeatures;

public class Entry_health2 {

	public static void main(String[] args) throws IOException{
		
		printDuplicateIndices();
		evalClassifiedPairs();
		//output_classifier();
		//EvalBK();
		//Phase2();
		//Phase1_mod();
		//preprocess();

	}
	
	public static void output_classifier()throws IOException{
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
	
	public static void EvalBK()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"healthcare_mod.csv",prefix+"ontology_mod1.csv",prefix+"goldStandard_healthcare_ontology");
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
	
	public static void preprocess()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		Scanner in=new Scanner(new File(prefix+"ontology_mod.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"ontology_mod1.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			line=line.replace("\"\"","\"");
			out.println(line);
		}
		in.close();
		out.close();
	}
	
	public static void Phase1_mod()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		new phases.Phase1_mod(prefix+"healthcare_mod.csv",prefix+"ontology_mod1.csv",prefix+"features");
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
			threshold=Double.parseDouble(in.nextLine().split(" ")[1])*1.2;
		in.close();
		
		//read in tuples
		ArrayList<String> tuples1=new ArrayList<String>();
		ArrayList<String> tuples2=new ArrayList<String>();
		in=new Scanner(new FileReader(prefix+"healthcare_mod.csv"));
		while(in.hasNextLine())
			tuples1.add(in.nextLine());
		in.close();
		in=new Scanner(new FileReader(prefix+"ontology_mod1.csv"));
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
				if(GenerateFeatures.isDuplicate(tuples1.get(i),tuples2.get(j),threshold))
				{	
					
					out.println(i+" "+j);
					
					
				}
			}
		out.close();
		
	}

	public static void evalClassifiedPairs()throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		ArrayList<String> gold=new ArrayList<String>();
		
		Scanner in=new Scanner(new File(prefix+"goldStandard_healthcare_ontology"));
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

}
