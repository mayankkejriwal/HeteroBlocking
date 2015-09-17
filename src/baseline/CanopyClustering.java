package baseline;

import java.io.*;
import java.util.*;



import evaluation.ImportGoldStandard;

public class CanopyClustering {
	
	Tokens data1;
	Tokens data2;
	 int total_pairs;
	 
	
	public CanopyClustering(String dataset1, String dataset2)throws IOException{
		data1=new Tokens(dataset1);
		data2=new Tokens(dataset2);
		total_pairs=data1.data.corpussize*data2.data.corpussize;
		
	}
	
	private ArrayList<Integer> buildIndexSet(int limit){
		ArrayList<Integer> res=new ArrayList<Integer>();
		for(int i=0; i<limit; i++)
			res.add(i);
		return res;
	}
	
	//will return Pairs Completeness for a given Reduction Ratio
	public double getPC(double rr, int seed, ImportGoldStandard gold){
		
		HashMap<Integer,HashSet<Integer>> pairs=new HashMap<Integer,HashSet<Integer>>();
		
		int num_pairs=(int)(total_pairs*(1-rr));
		int orig=num_pairs;
		ArrayList<Integer> IndexSet=buildIndexSet(data1.data.corpussize);
		Random p=new Random(seed);
		int iter=0;
		while(num_pairs>0&&iter<10){
			//System.out.println(num_pairs);
			int index1=-1;
			if(IndexSet.size()>0)
				index1=IndexSet.remove(p.nextInt(IndexSet.size()));
			else
				break;
			HashSet<Integer> canopy=data2.getCluster(data1,index1);
													
			if(canopy==null||canopy.size()==0)
				continue;
			if(!pairs.containsKey(index1))
				pairs.put(index1,new HashSet<Integer>());
			int l=0;
			
			for(int m2:canopy)
					if(!pairs.get(index1).contains(m2)){
						pairs.get(index1).add(m2);
						l++;
						}
				
			num_pairs-=l;
			if(l>0)
				iter=0;
			else
				iter++;
			System.out.println(IndexSet.size());
		}
		double nrr=1-(orig-num_pairs)*1.0/total_pairs;
		System.out.println("Actual RR is "+nrr);
		return evalAgainstGold(pairs,gold);
	}
	
	//note that this prints out rr and pc at current val of parameters.
	public void printPCandRR(ImportGoldStandard gold){
		HashMap<Integer,HashSet<Integer>> pairs=new HashMap<Integer,HashSet<Integer>>();
		
		int pairs_gen=0;
		for(int index1=0; index1<data1.data.corpussize; index1++){
			//System.out.println(pairs_gen);
			
			HashSet<Integer> canopy=data2.getCluster(data1,index1);
													
			if(canopy==null||canopy.size()==0)
				continue;
			if(!pairs.containsKey(index1))
				pairs.put(index1,new HashSet<Integer>());
			
			
			for(int m2:canopy)
					if(!pairs.get(index1).contains(m2)){
						pairs.get(index1).add(m2);
						pairs_gen++;
						}
				
			
			
		}
		
		double RR=1-1.0*pairs_gen/total_pairs;
		System.out.println("Pairs Completeness\tReduction Ratio");
		
		double PC=evalAgainstGold(pairs,gold);
		System.out.println(PC+","+RR);
	}
	
	private double evalAgainstGold(HashMap<Integer,HashSet<Integer>> pairs, ImportGoldStandard gold){
		
		//int total=0;
		int correct=0;
		for(int key:pairs.keySet()){
			//total+=pairs.get(key).size();
			for(int val:pairs.get(key))
				if(gold.contains(key,val))
					correct++;
		}
		return (double)1.0*correct/gold.num_dups;
	}

	//will return Pairs Completeness and actual RR for a given RR
	public double[] getPCRR(double rr, int seed, ImportGoldStandard gold){
		
		HashMap<Integer,HashSet<Integer>> pairs=new HashMap<Integer,HashSet<Integer>>();
		
		int num_pairs=(int)(total_pairs*(1-rr));
		int orig=num_pairs;
		System.out.println("num_pairs "+num_pairs);
		ArrayList<Integer> IndexSet=buildIndexSet(data1.data.corpussize);
		Random p=new Random(seed);
		int iter=0;
		while(num_pairs>0&&iter<10){
			//System.out.println(num_pairs);
			int index1=-1;
			if(IndexSet.size()>0)
				index1=IndexSet.remove(p.nextInt(IndexSet.size()));
			else
				break;
			HashSet<Integer> canopy=data2.getCluster(data1,index1);
													
			if(canopy==null||canopy.size()==0)
				continue;
			if(!pairs.containsKey(index1))
				pairs.put(index1,new HashSet<Integer>());
			int l=0;
			
			for(int m2:canopy)
					if(!pairs.get(index1).contains(m2)){
						pairs.get(index1).add(m2);
						l++;
						}
				
			num_pairs-=l;
			if(l>0)
				iter=0;
			else
				iter++;
		//	System.out.println(IndexSet.size());
		}
		double nrr=1-(orig-num_pairs)*1.0/total_pairs;
		///System.out.println("Actual RR is "+nrr);
		double[] result=new double[2];
		
		result[0]= evalAgainstGold(pairs,gold);
		result[1]=nrr;
		return result;
	}

}
