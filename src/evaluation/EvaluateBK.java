package evaluation;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import christen.Parameters;





public class EvaluateBK {
	
	ImportGoldStandard gold;	//must be explicitly for 'pairs' not 'clusters'
	GenerateBlocks block_obj;
	
	private HashMap<String, HashSet<Integer>> blocks1;
	private HashMap<String,HashSet<Integer>> blocks2;
	
	ArrayList<String> tuples1;
	ArrayList<String> tuples2;
	
	public EvaluateBK(String BKFile, ArrayList<String> tuples1, ArrayList<String> tuples2)throws IOException{
		this.gold=null;
		this.tuples1=tuples1;
		this.tuples2=tuples2;
		ArrayList<String> BKs=new ArrayList<String>();
		Scanner in=new Scanner(new FileReader(BKFile));
		String[] disjuncts=null;
		if(in.hasNextLine())
			disjuncts=in.nextLine().split("\t");
		for(String disjunct:disjuncts)
			BKs.add(disjunct);
		block_obj=new GenerateBlocks(BKs, null);
		in.close();
		
		
		build_blocks();
	}
	
	public EvaluateBK(String BKFile, ImportGoldStandard gold)throws IOException{
		this.gold=gold;
		ArrayList<String> BKs=new ArrayList<String>();
		Scanner in=new Scanner(new FileReader(BKFile));
		String[] disjuncts=null;
		if(in.hasNextLine())
			disjuncts=in.nextLine().split("\t");
		for(String disjunct:disjuncts)
			BKs.add(disjunct);
		block_obj=new GenerateBlocks(BKs, null);
		in.close();
		tuples1=gold.data1.getTuples();
		tuples2=gold.data2.getTuples();
		build_blocks();
	}
	
	private void build_blocks(){
		
		blocks1=new HashMap<String,HashSet<Integer>>();
		for(int i=0; i<tuples1.size(); i++){
			block_obj.setLine(tuples1.get(i),true);
			//System.out.println(tuples.get(i));
			
			for(int j=0; j<block_obj.num_clauses; j++){
				
				HashSet<String> bks=block_obj.block(j);
				if(bks==null)
					continue;
				for(String bk:bks)
					if(bk==null)
						continue;
					else if(blocks1.containsKey(bk))
						blocks1.get(bk).add(i);
					else{
						blocks1.put(bk, new HashSet<Integer>());
						blocks1.get(bk).add(i);
					}
			}
			
		}
		
		blocks2=new HashMap<String,HashSet<Integer>>();
		for(int i=0; i<tuples2.size(); i++){
			block_obj.setLine(tuples2.get(i),false);
			//System.out.println(i);
			for(int j=0; j<block_obj.num_clauses; j++){
				
				HashSet<String> bks=block_obj.block(j);
				if(bks==null)
					continue;
				for(String bk:bks)
					if(bk==null||!blocks1.containsKey(bk))
						continue;
					else if(blocks2.containsKey(bk))
						blocks2.get(bk).add(i);
					else{
						blocks2.put(bk, new HashSet<Integer>());
						blocks2.get(bk).add(i);
					}
			}
		}
		
		
		System.out.println("Num blocks 1, 2: "+blocks1.keySet().size()+", "+blocks2.keySet().size());
		System.out.println("Done blocking...leaving build_blocks()");
	}
	
	public HashMap<Integer,HashSet<Integer>> return_pairs(){
		HashMap<Integer, HashSet<Integer>> pairs=new HashMap<Integer, HashSet<Integer>>();
		for(String bk: blocks1.keySet())
			{
			if(!blocks2.containsKey(bk))
				continue;
			else if(blocks1.get(bk).size()*blocks2.get(bk).size()>Parameters.maxpairs)
				continue;
			
			ArrayList<Integer> list1=new ArrayList<Integer>(blocks1.get(bk));
			ArrayList<Integer> list2=new ArrayList<Integer>(blocks2.get(bk));
			
			for(int i=0; i<list1.size(); i++)
				for(int j=0; j<list2.size(); j++)
					if(pairContains(pairs,list1.get(i),list2.get(j))==1){
						pairs.get(list1.get(i)).add(list2.get(j));
					}
					else if(pairContains(pairs,list1.get(i),list2.get(j))==0){
						pairs.put(list1.get(i),new HashSet<Integer>());
						pairs.get(list1.get(i)).add(list2.get(j));
					}
		}
		System.out.println("Done generating Pairs");
		return pairs;
	}
	//will print Pairs Completeness (recall) and Reduction Ratio
	//we will adopt the standard defn of reduction ratio, so pairs are only counted once
	public void print_metrics(){
		HashMap<Integer, HashSet<Integer>> pairs=return_pairs();
		int total=gold.total_pairs;
		int truepos=gold.num_dups;
		
		int count=0;
		for(int i:pairs.keySet())
			for(int j:pairs.get(i))
			if(goldContains(i,j))
				count++;
		System.out.println("Pairs Completeness\tReduction Ratio");
		System.out.println((double) count/truepos+"\t"+(1.0-(double) countHashMap(pairs)/total));
		
	}
	
	public double[] return_metrics(){
		HashMap<Integer, HashSet<Integer>> pairs=return_pairs();
		int total=gold.total_pairs;
		int truepos=gold.num_dups;
		double[] res=new double[2];
		int count=0;
		for(int i:pairs.keySet())
			for(int j:pairs.get(i))
			if(goldContains(i,j))
				count++;
		//System.out.println("Pairs Completeness\tReduction Ratio");
		//System.out.println((double) count/truepos+"\t"+(1.0-(double) countHashMap(pairs)/total));
		res[0]=(double) 1.0*count/truepos;
		res[1]=(1.0-(double) countHashMap(pairs)/total);
		return res;
	}
	
	private int countHashMap(HashMap<Integer, HashSet<Integer>> pairs){
		int count=0;
		for(int i:pairs.keySet()){
			count+=pairs.get(i).size();
		}
		return count;
	}
	
	
	//returns 2 if pair is present, 1 if only k1 is present, 0 if neither
	private int pairContains(HashMap<Integer,HashSet<Integer>> pairs, int k1, int k2){
		
		
			if(pairs.containsKey(k1))
				if(pairs.get(k1).contains(k2))
					return 2;
				else
					return 1;
		
		return 0;
	}
	
	private boolean goldContains(int element1, int element2){
		if(gold.contains(element1, element2))
			return true;
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean contains(ArrayList<Integer> index1, ArrayList<Integer> index2, int i, int j){
		for(int p=0; p<index1.size(); p++)
			if((int)index1.get(p)==i)
				if((int)index2.get(p)==j)
					return true;
				else
					continue;
			else if((int)index1.get(p)==j)
				if((int)index2.get(p)==i)
					return true;
				else
					continue;
		return false;
	}
	
	public void randomlyPrintBlock(int blocknum){
		HashMap<String, HashSet<Integer>> blocks=null;
		ArrayList<String> tuples=null;
		if(blocknum==1){
			blocks=blocks1;
			tuples=gold.data1.getTuples();
		}
		else if(blocknum==2){
			blocks=blocks2;
			tuples=gold.data2.getTuples();
		}
		else
			System.out.println("Error in randomlyPrintBlock");
		for(String bk: blocks.keySet()){
			
			for(int tuple:blocks.get(bk))
				System.out.println(tuples.get(tuple));
			System.out.println("Key: "+bk);
			//System.exit(0);
		}
	}
	
	public static void main(String[] args)throws IOException{
		
			
			
		
	}
	
	
}
