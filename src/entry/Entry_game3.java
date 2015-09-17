package entry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import christen.Parameters;

import phases.Phase1;
import phases.Phase2;
import baseline.CanopyClustering;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;
import general.PrintUtilities;

public class Entry_game3 {

static String prefix="/host/heteroDatasets/game3/";
	
	public static void main(String[] args) throws IOException{
		evalFeaturesFile();
		//Phase1_mod();
		//Parameters.maxpairs=10000;
		//Phase2_cont(12);
		//EvalBK();
	}
	
	public static void testCanopy(double rr, int seed)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"vgchartz.csv",prefix+"goldStandard_ibm_vgchartz");
		CanopyClustering can=new CanopyClustering(prefix+"ibm.csv",prefix+"vgchartz.csv");
		System.out.println("PC, canopy: "+can.getPC(rr,seed,gold));
	}
	
	public static void testCanopy() throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"vgchartz.csv",prefix+"goldStandard_ibm_vgchartz");
		CanopyClustering can=new CanopyClustering(prefix+"ibm.csv",prefix+"vgchartz.csv");
		can.printPCandRR(gold);
	}
	
	public static void EvalBK()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"vgchartz.csv",prefix+"goldStandard_ibm_vgchartz");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		block.print_metrics();
	}
	
	public static void Phase1_mod()throws IOException{
		
		new phases.Phase1_mod(prefix+"ibm.csv",prefix+"vgchartz.csv",prefix+"features");
	}
	
	public static void evalFeaturesFile() throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"vgchartz.csv",prefix+"goldStandard_ibm_vgchartz");
		Phase2 game=new Phase2(gold,prefix+"features_400");
	}
	
	public static void Phase2(int numAttributes1)throws IOException{
		
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		System.out.println(game.prunedDupFeats.size()+" "+game.prunedNondupFeats.size());
		//System.out.println(indexCount(game.prunedDupFeats,309)+" "+indexCount(game.prunedDupFeats,221));
		ArrayList<ArrayList<Integer>> pf=game.prunedDupFeats;
		ArrayList<ArrayList<Integer>> pnf=game.prunedNondupFeats;
		game.cleanUp();
		writeOutNewFeatures(pf,pnf);
		System.exit(0);
		String bk=GenerateBK.generateHeteroBKString(game.prunedDupFeats,game.prunedNondupFeats,numAttributes1);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
	}
	
	private static int indexCount(ArrayList<ArrayList<Integer>> dupFeats, int index){
		int count=0;
		for(ArrayList<Integer> feat:dupFeats)
			if((int)feat.get(index)==1)
				count++;
		return count;
		
	}
	
	private static void ibm_mod()throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"ibm.csv"));
		//PrintWriter out=new PrintWriter(new File(prefix+"ibm_mod.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			if(line.split(",").length!=12)
				System.out.println(line);
		}
		in.close();
	}
	
	private static void writeOutNewFeatures(ArrayList<ArrayList<Integer>> prunedDupFeats,ArrayList<ArrayList<Integer>> prunedNondupFeats)throws IOException{
		PrintWriter out=new PrintWriter(new File(prefix+"dupfeatures"));
		for(ArrayList<Integer> feat:prunedDupFeats){
			out.println(PrintUtilities.convertToString(feat));
		}
		out.close();
		out=new PrintWriter(new File(prefix+"nondupfeatures"));
		for(ArrayList<Integer> feat:prunedNondupFeats){
			out.println(PrintUtilities.convertToString(feat));
		}
		out.close();
	}
	public static void Phase2_cont(int numAttributes1)throws IOException{
		ArrayList<ArrayList<Integer>> dupfeats=new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> nondupfeats=new ArrayList<ArrayList<Integer>>();
		Scanner dup=new Scanner(new FileReader(prefix+"dupfeatures"));
		while(dup.hasNextLine()){
			String[] feat=dup.nextLine().split(" ");
			ArrayList<Integer> f=new ArrayList<Integer>();
			for(String a:feat)
				f.add(Integer.parseInt(a));
			dupfeats.add(f);
		}
		dup.close();
		dup=new Scanner(new FileReader(prefix+"nondupfeatures"));
		while(dup.hasNextLine()){
			String[] feat=dup.nextLine().split(" ");
			ArrayList<Integer> f=new ArrayList<Integer>();
			for(String a:feat)
				f.add(Integer.parseInt(a));
			nondupfeats.add(f);
		}
		dup.close();
		String bk=GenerateBK.generateHeteroBKString(dupfeats,nondupfeats,numAttributes1);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
		
	}
}
