package featureSelection;

import java.io.IOException;
import java.util.ArrayList;

import FeatureSelection.FeatureAnalysis;
import FeatureSelection.Fisher;

import christen.Parameters;

public class GenerateBK {

	public static String generateHeteroBKString(ArrayList<ArrayList<Integer>> dupFeatures, 
			ArrayList<ArrayList<Integer>> nondupFeatures, int numAttributes1)throws IOException{
		String res="";
		Fisher c=new Fisher(dupFeatures.get(0).size(),dupFeatures,nondupFeatures);
		c.computeStatistics();
		FeatureAnalysis d=new FeatureAnalysis(c);
		
		//compute  Blocking Keys
		LearnDisjunct e=null;
		int numAttributes2=dupFeatures.get(0).size()/(Parameters.num_feats*numAttributes1);
		
		
		System.out.println(numAttributes2);
		e=new LearnDisjunct(d, 1, 2, numAttributes1, numAttributes2, Parameters.recall);
		ArrayList<String> codes=null;
		if(Parameters.DNF){
			e.populateDNF_Features(Parameters.conjuncts);
			codes=e.codesDNF();
		}else{
			e.populateDisjunction_Features();
			codes=e.codes();
		}
		//print to file
		
		for(int i=0; i<codes.size()-1; i++)
			res+=(codes.get(i)+"\t");
		
		res=res+codes.get(codes.size()-1);
		
		return res;
		
	}
}
