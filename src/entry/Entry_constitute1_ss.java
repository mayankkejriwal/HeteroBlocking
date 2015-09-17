package entry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import phases.Phase2;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;
import java.util.*;

import christen.Parameters;

public class Entry_constitute1_ss {

	static String prefix="/host/heteroDatasets/cikm_experiments/juan_constitute1/";
	static int numAttributes1=5;
	
	public static void main(String[] args) throws IOException {
		//Phase1_mod();
		//supervised_Phase2(20,20);
		Parameters.maxpairs=150000;
		Phase2();
		EvalBK();
		
	}
	
	public static void Phase1_mod()throws IOException{
		
		new phases.Phase1_mod(prefix+"coding1_prop.csv",prefix+"coding2_prop_ss.csv",prefix+"features");
	}
	
	public static void Phase2()throws IOException{
		
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		System.out.println(game.prunedDupFeats.size()+" "+game.prunedNondupFeats.size());
		
		String bk=GenerateBK.generateHeteroBKString(game.prunedDupFeats,game.prunedNondupFeats,numAttributes1);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
	}
	
	
	public static void supervised_Phase2(int numDups, int numNondups)throws IOException{
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop_ss.csv",prefix+"goldStandard_ss");
		ArrayList<ArrayList<Integer>> prunedDupFeats=new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> prunedNondupFeats=new ArrayList<ArrayList<Integer>>();
		
		for(int i=0; i<numDups; i++)
			prunedDupFeats.add(gold.randomDuplicateFeatVec());
		for(int i=0; i<numNondups; i++)
			prunedNondupFeats.add(gold.randomNonDuplicateFeatVec());
		
		System.out.println(prunedDupFeats.size()+" "+prunedNondupFeats.size());
		String bk=GenerateBK.generateHeteroBKString(prunedDupFeats,prunedNondupFeats,numAttributes1);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
	}
	
	public static void EvalBK()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop_ss.csv",prefix+"goldStandard_ss");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		block.print_metrics();
	}

}
