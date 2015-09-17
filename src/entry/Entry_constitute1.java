package entry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import phases.Phase2;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;
import java.util.*;

import baseline.CanopyClustering;

import christen.Parameters;

public class Entry_constitute1 {

	static String prefix="/host/heteroDatasets/iswc_experiments/Colombia/";
	static int numAttributes1=11;
	
	public static void main(String[] args) throws IOException {
		boolean supervised=true;
		
		if(supervised){
			Parameters.maxpairs=1000000;
			double[] res=EvalBK_ss();
			System.out.println("SS RR\tPC");
			System.out.println(res[1]+"\t"+res[0]);
			res=EvalBK();
			System.out.println("RR\tPC");
			System.out.println(res[1]+"\t"+res[0]);
		}
		else{
		Parameters.maxpairs=100000000;
		Parameters.maxtokentuples=1000000000;
		Parameters.ut=0.0005;
		testCanopy(0.99,10);
		}
	}
	
	public static void testCanopy(double rr, int iter)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv",prefix+"goldStandard");
		CanopyClustering can=new CanopyClustering(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv");
		double[] pcrr=new double[2]; 
		for(int i=0; i<iter; i++){
			double[] q=can.getPCRR(rr,(int) (System.currentTimeMillis()+i),gold);
			System.out.println(iter+" PC, RR canopy: "+q[0]+"\t"+q[1]);
			pcrr[0]+=q[0];
			pcrr[1]+=q[1];
	
		}
		pcrr[0]/=iter;
		pcrr[1]/=iter;
		System.out.println("Final (average) PC, RR canopy: "+pcrr[0]+"\t"+pcrr[1]);
	}
	
	public static void testCanopy_ss(double rr, int iter)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop_7.csv",prefix+"goldStandard_n_7");
		CanopyClustering can=new CanopyClustering(prefix+"coding1_prop.csv",prefix+"coding2_prop_7.csv");
		double[] pcrr=new double[2]; 
		for(int i=0; i<iter; i++){
			double[] q=can.getPCRR(rr,(int) (System.currentTimeMillis()+i),gold);
			System.out.println(iter+" PC, RR canopy: "+q[0]+"\t"+q[1]);
			pcrr[0]+=q[0];
			pcrr[1]+=q[1];
	
		}
		pcrr[0]/=iter;
		pcrr[1]/=iter;
		System.out.println("Final (average) PC, RR canopy: "+pcrr[0]+"\t"+pcrr[1]);
	}

	public static void Phase1_mod()throws IOException{
		
		new phases.Phase1_mod(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv",prefix+"features");
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
	
	
	public static void supervised_Phase2(int numDups, int numNondups,int num)throws IOException{
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv",prefix+"goldStandard");
		ArrayList<ArrayList<Integer>> prunedDupFeats=new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> prunedNondupFeats=new ArrayList<ArrayList<Integer>>();
		
		for(int i=0; i<numDups; i++)
			prunedDupFeats.add(gold.randomDuplicateFeatVec());
		for(int i=0; i<numNondups; i++)
			prunedNondupFeats.add(gold.randomNonDuplicateFeatVec());
		
		System.out.println(prunedDupFeats.size()+" "+prunedNondupFeats.size());
		String bk=GenerateBK.generateHeteroBKString(prunedDupFeats,prunedNondupFeats,numAttributes1);
		PrintWriter out=new PrintWriter(new File(prefix+"30_30/BK"+num));
		out.println(bk);
		out.close();
	}
	
	public static double[] EvalBK()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv",prefix+"goldStandard");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		return block.return_metrics();
	}

	public static double[] EvalBK_ss()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop_7.csv",prefix+"goldStandard_n_7");
		ImportGoldStandard gold1=new ImportGoldStandard(prefix+"coding1_prop.csv",prefix+"coding2_prop.csv",prefix+"goldStandard");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		double[] res= block.return_metrics();
		double pairs=(1.0-res[1])*gold.total_pairs;
		res[1]=1.0-(pairs/gold1.total_pairs);
		
		return res;
	}
	
	

}
