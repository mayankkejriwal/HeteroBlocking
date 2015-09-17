package entry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import phases.Phase1;
import phases.Phase2;
import baseline.CanopyClustering;
import evaluation.EvaluateBK;
import evaluation.ImportGoldStandard;
import featureSelection.GenerateBK;

public class Entry_tjct {
static String year="2009";
static String prefix="/host/heteroDatasets/treasury_jct/"+year+"/";
static String treasury="Treasury_"+year+".csv";
static String jct="JCT_"+year+".csv";
static String gs="goldStandard_jct_treasury";
static int numAttributes1=18;
	
	public static void main(String[] args) throws IOException{
		//testCanopy();
		//EvalBK();
		build12Matrix();
		//Phase2(numAttributes1);
	}
	
	public static void build12Matrix()throws IOException{
		double[][][] orig_matrix=new double[12][12][2];
		double[][] fmatrix=new double[12][12];
		for(int yr=2002; yr<=2013; yr++){
			resetStaticVars(yr);
			//Phase1();
			//Phase2();
			String bk=new String(prefix+"BK");
			for(int k=2002; k<=2013; k++){
				resetStaticVars(k);
				double[] res=EvalBK(bk);
				orig_matrix[yr-2002][k-2002]=res;
				//fmatrix[yr-2002][k-2002]=2*res[0]*res[1]/(res[0]+res[1]);
				fmatrix[yr-2002][k-2002]=res[0];
			}
		}
		print4FMatrix(fmatrix);
	}
	
	private static void print4FMatrix(double[][] fmatrix){
		System.out.println("Year\tScore\tAverage(over11)\tBest(over11)\tWorst(over11)");
		for(int i=0; i<12; i++){
			printRowStats(fmatrix[i],i+2002);
		}
	}
	
	private static void printRowStats(double[] row, int year){
		
		int diag=year-2002;
		double avg=0;
		double best=0;
		double worst=1.0;
		for(int i=0; i<row.length; i++){
			if(i==diag)
				continue;
			avg+=row[i];
			if(row[i]>best)
				best=row[i];
			if(row[i]<worst)
				worst=row[i];
		}
		avg=1.0*avg/11;
		System.out.println(year+"\t"+row[diag]+"\t"+avg+"\t"+best+"\t"+worst);
	}
	
	private static double[] EvalBK(String bk)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+jct,prefix+treasury,prefix+gs);
		EvaluateBK block=new EvaluateBK(bk,gold);
		return block.return_metrics();
	}	
	
	private static void resetStaticVars(int yr){
		year=(new Integer(yr)).toString();
		prefix="/host/heteroDatasets/treasury_jct/"+year+"/";
		treasury="Treasury_"+year+".csv";
		jct="JCT_"+year+".csv";
	}
	
	public static void testCanopy(double rr, int seed)throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+jct,prefix+treasury,prefix+gs);
		CanopyClustering can=new CanopyClustering(prefix+jct,prefix+treasury);
		System.out.println("PC, canopy: "+can.getPC(rr,seed,gold));
	}
	
	public static void testCanopy() throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+jct,prefix+treasury,prefix+gs);
		CanopyClustering can=new CanopyClustering(prefix+jct,prefix+treasury);
		can.printPCandRR(gold);
	}
	
	public static void EvalBK()throws IOException{
		
		ImportGoldStandard gold=new ImportGoldStandard(prefix+jct,prefix+treasury,prefix+gs);
		EvaluateBK block=new EvaluateBK(prefix+"BK",gold);
		block.print_metrics();
	}
	
	public static void Phase1()throws IOException{
		
		new phases.Phase1_mod(prefix+jct,prefix+treasury,prefix+"features");
	}
	
	public static void Phase2()throws IOException{
		
		Phase2 game=new Phase2(null,prefix+"features");
		game.generatePrunedFeatureSets();
		System.out.println(game.prunedDupFeats.size()+" "+game.prunedNondupFeats.size());
		//System.out.println(indexCount(game.prunedDupFeats,309)+" "+indexCount(game.prunedDupFeats,221));
		String bk=GenerateBK.generateHeteroBKString(game.prunedDupFeats,game.prunedNondupFeats,numAttributes1);
		PrintWriter out=new PrintWriter(new File(prefix+"BK"));
		out.println(bk);
		out.close();
	}

}
