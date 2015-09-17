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

public class Entry_game_ss {

	static String prefix="/host/heteroDatasets/iswc_experiments/game/";
	static int numAttributes1=11;
	public static void main(String[] args) throws IOException {
		boolean supervised=true;
		
		
		if(supervised){
			/*
			for(int i=0; i<10; i++)
				supervised_Phase2(30,30,i);
			makeBigBKFile();
			System.exit(-1);
			*/
			
			Parameters.maxpairs=80000;
			
			double[] res=EvalBK_ss();
			System.out.println("SS RR\tPC");
			System.out.println(res[1]+"\t"+res[0]);
			/*res=EvalBK();
			System.out.println("RR\tPC");
			System.out.println(res[1]+"\t"+res[0]);*/
		}
		else{
		Parameters.maxpairs=40000;
		Parameters.maxtokentuples=1000000000;
		Parameters.ut=0.0005;
		testCanopy_ss(0.98,10);
		}
	}
	public static void testCanopy_ss(double rr, int iter)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vg_db_ls1");
		CanopyClustering can=new CanopyClustering(prefix+"vgchartz.csv",prefix+"dbpedia.csv");
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
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vg_db_ls1");
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
	public static double[] EvalBK_ss()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vg_db_ls1");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		return block.return_metrics();
	}
	public static void makeBigBKFile()throws IOException{
		HashSet<String> bks=new HashSet<String>();
		for(File file:(new File(prefix+"30_30/")).listFiles()){
			Scanner in=new Scanner(file);
			if(in.hasNextLine()){
				String[] t=in.nextLine().split("\t");
				for(String t1:t)
					bks.add(t1);
			}
			in.close();
		}
		String b="";
		ArrayList<String> bbks=new ArrayList<String>(bks);
		for(int i=0; i<bbks.size()-1; i++)
			b+=bbks.get(i)+"\t";
		b+=bbks.get(bbks.size()-1);
		PrintWriter out =new PrintWriter(new File(prefix+"30_30/BK"));
		out.println(b);
		out.close();
	}
	
	
}
