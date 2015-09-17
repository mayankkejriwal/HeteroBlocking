package entry;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import phases.Phase1;
import phases.Phase2;
import featureSelection.GenerateBK;

public class JCT {

	static String prefix="/host/heteroDatasets/jct/";
	
	public static void main(String[] args) throws IOException{
		
		Phase1();
		Phase2(18);
	}

	public static void Phase1()throws IOException{
		
		new Phase1(prefix+"JCT_2005.csv",prefix+"JCT_2007.csv",prefix+"features");
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
}
