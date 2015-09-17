package featureSelection;

import general.CSVParser;
import general.PrintUtilities;

import java.io.IOException;
import java.util.ArrayList;
import christen.Parameters;
import christen.TFIDF;



public class GenerateFeatures {
	
	public ArrayList<Double> dup_scores;
	public ArrayList<Double> nondup_scores;
	
	//dup1, dup2, nondup2, nondup2 are wrt to db.
	public ArrayList<Integer> dup1;
	public ArrayList<Integer> dup2;
	
	public ArrayList<Integer> nondup1;
	public ArrayList<Integer> nondup2;
	
	public ArrayList<ArrayList<Integer>> dupFeatures;
	public ArrayList<ArrayList<Integer>> nondupFeatures;
	
	int numFeatures=-1; //total number of features
	
	int numAttributes1=-1;
	int numAttributes2=-1;
	
	TFIDF db1;
	TFIDF db2;
	
	
	
	//the tuples passed in must come from subsets (containing common tokens) from the databases to be linked
	//we do not implement any windowing mechanism here
	public GenerateFeatures(ArrayList<String> tuples1, ArrayList<String> tuples2)
	throws IOException{
		db1=new TFIDF(tuples1);
		numAttributes1=db1.numatts;
		
		
		db2=new TFIDF(tuples2);
		numAttributes2=db2.numatts;
		//System.out.println(numAttributes1+" "+numAttributes2);
		
		numFeatures=Parameters.num_feats*numAttributes1*numAttributes2;
		//System.out.println(numFeatures);
		dup_scores=new ArrayList<Double>();
		nondup_scores=new ArrayList<Double>();
		dup1=new ArrayList<Integer>();
		dup2=new ArrayList<Integer>();
		nondup1=new ArrayList<Integer>();
		nondup2=new ArrayList<Integer>();
		
		
		for(int i=0; i<tuples1.size(); i++)
			for(int j=0; j<tuples2.size(); j++){
				
				double score=db1.getScoreTF(db2,i,j);
				if(score>=Parameters.ut){
					dup_scores.add(score);
					dup1.add(i);
					dup2.add(j);
				
				}
				else if(score<=Parameters.lt && score>0.0)
				{
					nondup_scores.add(score);
					nondup1.add(i);
					nondup2.add(j);
				}
				
		}
			
		setUpFeatures();
	}
	
	public void setUpFeatures(){
		dupFeatures=new ArrayList<ArrayList<Integer>>(dup1.size());
		nondupFeatures=new ArrayList<ArrayList<Integer>>(nondup1.size());
		
		//System.out.println("constructing duplicate features");
		for(int i=0; i<dup1.size(); i++)
			dupFeatures.add(getFeatureWeights_opt(i,0));
		
		//System.out.println("constructing non duplicate features");
		for(int i=0; i<nondup1.size(); i++)
			nondupFeatures.add(getFeatureWeights_opt(i,1));
		
		checkFeatureLength();
		
	}
	
	private void checkFeatureLength(){
		for(int i=0; i<dupFeatures.size(); i++)
			if(dupFeatures.get(i).size()!=numFeatures)
				System.out.println("ANOMALY LENGTH : " +dupFeatures.get(i).size()+" "+numFeatures);
		
		for(int i=0; i<nondupFeatures.size(); i++)
			if(nondupFeatures.get(i).size()!=numFeatures)
				System.out.println("ANOMALY LENGTH (nondup): " +nondupFeatures.get(i).size()+" "+numFeatures);

	
	}
	
	
	public static ArrayList<Integer> getFeatureWeights(String record1, String record2){
				int[] weight=null;
				String r1=null;
				String r2=null;
				for(String forb:Parameters.forbiddenwords)
				{
					r1=record1.replace(forb,"");
					r2=record2.replace(forb,"");
				}
				String[] tokens1=null;
				String[] tokens2=null;
				
				try {
				 tokens1=(new CSVParser()).parseLine(r1);
				 
					tokens2=(new CSVParser()).parseLine(r2);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
					
				ArrayList<Integer> result=new ArrayList<Integer>();
				for(int code=1;code<=Parameters.num_feats;code++){
				if(code==1)
					weight=HeteroBK.ExactMatch(tokens1, tokens2);
				else if(code==2)
					weight=HeteroBK.CommonToken(tokens1, tokens2);
				else if(code==3)
					weight=HeteroBK.CommonInteger(tokens1, tokens2);
				else if(code==4)
					weight=HeteroBK.CommonOrOffByOneInteger(tokens1, tokens2);
				else if(code==5)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,3);
				else if(code==6)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,5);
				else if(code==7)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,7);
				else if(code==8)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,2);
				else if(code==9)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,4);
				else if(code==10)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,6);
				else if(code>10&&code<=18){
					int p=code-11;
					String[] vals={"000","001","010","011","100","101","110","111"};
					String val=vals[p];
					boolean reverse= val.charAt(0)=='0' ? false : true;
					boolean mod= val.charAt(1)=='0' ? false : true;
					boolean four= val.charAt(2)=='0' ? false : true;
					weight=HeteroBK.soundex(tokens1, tokens2, reverse, mod, four);
					
				}
				else if(code==19){
					weight=HeteroBK.CommonAlphaNumeric(tokens1, tokens2);
				}
				else if(code>19&&code<=28){
					int p=code-20;
					String[] vals={"soundex","caverphone1","caverphone2","colognephonetic","doublemetaphone"
							,"matchrating","metaphone","nysiis","refinedsoundex"};
					weight=HeteroBK.phonetic(tokens1,tokens2,vals[p]);
				}
				
					
				concatenate(result,weight);
				}
				return result;
			}
			
			//if opt is 0, then dup, otherwise nondup. Returns the entire concatenated ArrayList
	private ArrayList<Integer> getFeatureWeights_opt(int index, int opt){
				int[] weight=null;
				String[] tokens1=null;
				String[] tokens2=null;
				int index1=-1;
				int index2=-1;
				if(opt==0){
					index1=dup1.get(index);
					index2=dup2.get(index);	
					try {
				 tokens1=(new CSVParser()).parseLine(db1.getTuples().get(index1));
				 
					tokens2=(new CSVParser()).parseLine(db2.getTuples().get(index2));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				else{
					index1=nondup1.get(index);
					index2=nondup2.get(index);	
					try {
						 tokens1=(new CSVParser()).parseLine(db1.getTuples().get(index1));
						 
							tokens2=(new CSVParser()).parseLine(db2.getTuples().get(index2));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				ArrayList<Integer> result=new ArrayList<Integer>();
				for(int code=1;code<=Parameters.num_feats;code++){
				if(code==1)
					weight=HeteroBK.ExactMatch(tokens1, tokens2);
				else if(code==2)
					weight=HeteroBK.CommonToken(tokens1, tokens2);
				else if(code==3)
					weight=HeteroBK.CommonInteger(tokens1, tokens2);
				else if(code==4)
					weight=HeteroBK.CommonOrOffByOneInteger(tokens1, tokens2);
				else if(code==5)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,3);
				else if(code==6)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,5);
				else if(code==7)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,7);
				else if(code==8)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,2);
				else if(code==9)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,4);
				else if(code==10)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,6);
				else if(code>10&&code<=18){
					int p=code-11;
					String[] vals={"000","001","010","011","100","101","110","111"};
					String val=vals[p];
					boolean reverse= val.charAt(0)=='0' ? false : true;
					boolean mod= val.charAt(1)=='0' ? false : true;
					boolean four= val.charAt(2)=='0' ? false : true;
					weight=HeteroBK.soundex(tokens1, tokens2, reverse, mod, four);
					
				}
				else if(code==19){
					weight=HeteroBK.CommonAlphaNumeric(tokens1, tokens2);
				}
				else if(code>19&&code<=28){
					int p=code-20;
					String[] vals={"soundex","caverphone1","caverphone2","colognephonetic","doublemetaphone"
							,"matchrating","metaphone","nysiis","refinedsoundex"};
					weight=HeteroBK.phonetic(tokens1,tokens2,vals[p]);
				}
				
					
				concatenate(result,weight);
				}
				return result;
			}

	//if feat_sum>=threshold, true, else false
	public static boolean isDuplicate(String string1, String string2, double threshold){
		int[] weight=null;
		String[] tokens1=null;
		String[] tokens2=null;
		try {
			 tokens1=(new CSVParser()).parseLine(string1);
			 
				tokens2=(new CSVParser()).parseLine(string2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		ArrayList<Integer> result=new ArrayList<Integer>();
		for(int code=1;code<=Parameters.num_feats;code++){
		if(code==1)
			weight=HeteroBK.ExactMatch(tokens1, tokens2);
		else if(code==2)
			weight=HeteroBK.CommonToken(tokens1, tokens2);
		else if(code==3)
			weight=HeteroBK.CommonInteger(tokens1, tokens2);
		else if(code==4)
			weight=HeteroBK.CommonOrOffByOneInteger(tokens1, tokens2);
		else if(code==5)
			weight=HeteroBK.CommonNFirst(tokens1, tokens2,3);
		else if(code==6)
			weight=HeteroBK.CommonNFirst(tokens1, tokens2,5);
		else if(code==7)
			weight=HeteroBK.CommonNFirst(tokens1, tokens2,7);
		else if(code==8)
			weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,2);
		else if(code==9)
			weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,4);
		else if(code==10)
			weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,6);
		else if(code>10&&code<=18){
			int p=code-11;
			String[] vals={"000","001","010","011","100","101","110","111"};
			String val=vals[p];
			boolean reverse= val.charAt(0)=='0' ? false : true;
			boolean mod= val.charAt(1)=='0' ? false : true;
			boolean four= val.charAt(2)=='0' ? false : true;
			weight=HeteroBK.soundex(tokens1, tokens2, reverse, mod, four);
			
		}
		else if(code==19){
			weight=HeteroBK.CommonAlphaNumeric(tokens1, tokens2);
		}
		else if(code>19&&code<=28){
			int p=code-20;
			String[] vals={"soundex","caverphone1","caverphone2","colognephonetic","doublemetaphone"
					,"matchrating","metaphone","nysiis","refinedsoundex"};
			weight=HeteroBK.phonetic(tokens1,tokens2,vals[p]);
		}
		
		concatenate(result,weight);
		}
		int sum=0;
		for(int feat:result)
			sum+=feat;
		if(sum>=threshold)
			return true;
		else
			return false;
	}

	private static ArrayList<Integer> getFeatureWeightsTest(String string1, String string2){
				int[] weight=null;
				String[] tokens1=null;
				String[] tokens2=null;
				try {
					 tokens1=(new CSVParser()).parseLine(string1);
					 
						tokens2=(new CSVParser()).parseLine(string2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				PrintUtilities.printArrayString(tokens1);
				PrintUtilities.printArrayString(tokens2);
				
				ArrayList<Integer> result=new ArrayList<Integer>();
				for(int code=1;code<=Parameters.num_feats;code++){
				if(code==1)
					weight=HeteroBK.ExactMatch(tokens1, tokens2);
				else if(code==2)
					weight=HeteroBK.CommonToken(tokens1, tokens2);
				else if(code==3)
					weight=HeteroBK.CommonInteger(tokens1, tokens2);
				else if(code==4)
					weight=HeteroBK.CommonOrOffByOneInteger(tokens1, tokens2);
				else if(code==5)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,3);
				else if(code==6)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,5);
				else if(code==7)
					weight=HeteroBK.CommonNFirst(tokens1, tokens2,7);
				else if(code==8)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,2);
				else if(code==9)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,4);
				else if(code==10)
					weight=HeteroBK.CommonTokenNGram(tokens1, tokens2,6);
				else if(code>10&&code<=18){
					int p=code-11;
					String[] vals={"000","001","010","011","100","101","110","111"};
					String val=vals[p];
					boolean reverse= val.charAt(0)=='0' ? false : true;
					boolean mod= val.charAt(1)=='0' ? false : true;
					boolean four= val.charAt(2)=='0' ? false : true;
					weight=HeteroBK.soundex(tokens1, tokens2, reverse, mod, four);
					
				}
				else if(code==19){
					weight=HeteroBK.CommonAlphaNumeric(tokens1, tokens2);
				}
				else if(code>19&&code<=28){
					int p=code-20;
					String[] vals={"soundex","caverphone1","caverphone2","colognephonetic","doublemetaphone"
							,"matchrating","metaphone","nysiis","refinedsoundex"};
					weight=HeteroBK.phonetic(tokens1,tokens2,vals[p]);
				}
				
				
				System.out.println(weight.length);
				PrintUtilities.printArrayInt(weight);	
				concatenate(result,weight);
				}
				return result;
			}
	
			

			private static void concatenate(ArrayList<Integer> d,int[] weight){
				for(int i=0; i<weight.length; i++)
					d.add(weight[i]);
					
			}
			
			public static void main(String[] args){
				getFeatureWeightsTest("wacky world of sports,null,wii,sega",
		"9674,wacky world of sports,wii,2009,sports,sega,0.07,0,0,0.01,0.07");
			}
	
}
