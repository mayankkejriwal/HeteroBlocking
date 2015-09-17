package entry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import phases.Phase1;
import phases.Phase2;
import baseline.CanopyClustering;
import christen.Parameters;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;


public class Entry_game2 {
	
	static String prefix="/host/heteroDatasets/game2/";
	
	public static void main(String[] args) throws IOException{
		//Phase1_mod();
		evalFeaturesFile();
		//Phase2(12);
	}
	
	public static void testCanopy(double rr, int seed)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"dbpedia.csv",prefix+"goldStandard_ibm_dbpedia");
		CanopyClustering can=new CanopyClustering(prefix+"ibm.csv",prefix+"dbpedia.csv");
		System.out.println("PC, canopy: "+can.getPC(rr,seed,gold));
	}
	
	public static void testCanopy() throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"dbpedia.csv",prefix+"goldStandard_ibm_dbpedia");
		CanopyClustering can=new CanopyClustering(prefix+"ibm.csv",prefix+"dbpedia.csv");
		can.printPCandRR(gold);
	}
	
	public static void EvalBK()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"dbpedia.csv",prefix+"goldStandard_ibm_dbpedia");
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		block.print_metrics();
	}
	
	public static void Phase1()throws IOException{
		
		new Phase1(prefix+"ibm.csv",prefix+"dbpedia.csv",prefix+"features");
	}
	
	public static void Phase1_mod()throws IOException{
		
		new phases.Phase1_mod(prefix+"ibm.csv",prefix+"dbpedia.csv",prefix+"features");
	}
	
	public static void evalFeaturesFile() throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+"ibm.csv",prefix+"dbpedia.csv",prefix+"goldStandard_ibm_dbpedia");
		Phase2 game=new Phase2(gold,prefix+"features_400");
	}
	
	public static void Phase2(int numAttributes1)throws IOException{
		
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		System.out.println(game.prunedDupFeats.size()+" "+game.prunedNondupFeats.size());
		//System.out.println(indexCount(game.prunedDupFeats,309)+" "+indexCount(game.prunedDupFeats,221));
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
	
	private static void checkFeatsNum(int numFeats)throws IOException{
		Scanner in=new Scanner(new FileReader(prefix+"features"));
		ArrayList<String> lines=new ArrayList<String>();
		while(in.hasNextLine()){
			lines.add(in.nextLine());
		}
		in.close();
		for(int i=0; i<lines.size(); i+=4  ){
			String[] feats=lines.get(i).split("\t")[1].split(" ");
			if(feats.length!=numFeats)
			{
				System.out.println(feats.length);
				System.out.println(lines.get(i+2));
				System.out.println(lines.get(i+3));
			}
		}
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

}
