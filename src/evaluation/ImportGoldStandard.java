package evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

import christen.TFIDF;
import featureSelection.GenerateFeatures;

public class ImportGoldStandard {

	HashMap<Integer,HashSet<Integer>> duplicate_records; //no transitive closure complications allowed: only disjoint duplicates!
	
	public int num_dups=0; //the total number of duplicate pairs present
	public int total_pairs=0;
	public TFIDF data1;
	public TFIDF data2;
	
	//For TFIDF, we assume schema line is missing. 
	   //only ordinary pairing (not clustering) is allowed
		public  ImportGoldStandard(String dataset1,String dataset2, String goldfile) throws IOException{
			data1=new TFIDF(dataset1, false);
			
			data2=new TFIDF(dataset2, false);
			total_pairs=data2.corpussize*data1.corpussize;
			Scanner in=new Scanner(new File(goldfile));	//the gold standard file
			
			
				
				duplicate_records=new HashMap<Integer,HashSet<Integer>>();
				while(in.hasNextLine()){
					String[] d=in.nextLine().split(" ");
					if(!duplicate_records.containsKey(Integer.valueOf(d[0])))
						duplicate_records.put(Integer.valueOf(d[0]),new HashSet<Integer>());
					duplicate_records.get(Integer.valueOf(d[0])).add(Integer.valueOf(d[1]));
				}
				num_dups=calcHashMapSize();
			
			
			
			in.close();
		}
		
		private int calcHashMapSize(){
			int size=0;
			for(int key:duplicate_records.keySet())
				size+=duplicate_records.get(key).size();
				
			return size;
		}
		
		public boolean contains(int i, int j){
			
				if(duplicate_records.containsKey(i))
					if(duplicate_records.get(i).contains(j))
						return true;
					
				return false;
			
			
		}
		
		public ArrayList<Integer> randomNonDuplicateFeatVec(){
			Random p=new Random(System.currentTimeMillis());
			int q1=-1;
			int q2=-1;
			do{
				q1=p.nextInt(data1.corpussize);
			
				q2=p.nextInt(data2.corpussize);}
			while(contains(q1,q2));
			return GenerateFeatures.getFeatureWeights(data1.getTuples().get(q1),data2.getTuples().get(q2));
			
		}
		
		
		
		public ArrayList<Integer> randomDuplicateFeatVec(){
			Random p=new Random(System.currentTimeMillis());
			ArrayList<Integer> keys=new ArrayList<Integer>(duplicate_records.keySet());
			int q1=p.nextInt(keys.size());
			q1=keys.get(q1);
			ArrayList<Integer> set=new ArrayList<Integer>(duplicate_records.get(q1));
			int q2=p.nextInt(set.size());
			q2=set.get(q2);
			return GenerateFeatures.getFeatureWeights(data1.getTuples().get(q1),data2.getTuples().get(q2));
			
		}
	
}
