package entry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import christen.Parameters;

import baseline.CanopyClustering;

import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;

import phases.Phase1;
import phases.Phase2;

public class Entry_game {
	
	public static void main(String[] args) throws IOException{
		Phase2();
		//evalFeaturesFile();
	}
	
	public static void testCanopy(double rr, int seed)throws IOException{
		String prefix="/host/heteroDatasets/game/";
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vgchartz_dbpedia");
		CanopyClustering can=new CanopyClustering(prefix+"vgchartz.csv",prefix+"dbpedia.csv");
		System.out.println("PC, canopy: "+can.getPC(rr,seed,gold));
	}
	
	public static void testCanopy() throws IOException{
		String prefix="/host/heteroDatasets/game/";
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vgchartz_dbpedia");
		CanopyClustering can=new CanopyClustering(prefix+"vgchartz.csv",prefix+"dbpedia.csv");
		can.printPCandRR(gold);
	}
	
	public static void EvalBK()throws IOException{
		String prefix="/host/heteroDatasets/game/";
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vgchartz_dbpedia");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		block.print_metrics();
	}
	
	public static void Phase1()throws IOException{
		String prefix="/host/heteroDatasets/game/";
		new Phase1(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"features");
	}
	
	public static void Phase1_mod()throws IOException{
		String prefix="/host/heteroDatasets/game/";
		new phases.Phase1_mod(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"features");
	}
	
	public static void Phase2()throws IOException{
		String prefix="/host/heteroDatasets/game/";
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		game.cleanUp();
		String bk=GenerateBK.generateHeteroBKString(game.prunedDupFeats,game.prunedNondupFeats,11);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
	}
	
	public static void evalFeaturesFile() throws IOException{
		String prefix="/host/heteroDatasets/game/";
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"vgchartz.csv",prefix+"dbpedia.csv",prefix+"goldStandard_vgchartz_dbpedia");
		Phase2 game=new Phase2(gold,prefix+"features");
	}
	
	private static int indexCount(ArrayList<ArrayList<Integer>> dupFeats, int index){
		int count=0;
		for(ArrayList<Integer> feat:dupFeats)
			if((int)feat.get(index)==1)
				count++;
		return count;
		
	}

}
