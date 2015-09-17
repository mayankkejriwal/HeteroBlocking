package phases;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;



import christen.Parameters;
import evaluation.ImportGoldStandard;

public class Phase2 {

	ArrayList<Integer> dupIndex1=new ArrayList<Integer>();
	ArrayList<Integer> dupIndex2=new ArrayList<Integer>();
	ArrayList<Integer> nondupIndex1=new ArrayList<Integer>();
	ArrayList<Integer> nondupIndex2=new ArrayList<Integer>();
	ArrayList<Integer> dupIndexCount=new ArrayList<Integer>();
	ArrayList<Integer> nondupIndexCount=new ArrayList<Integer>();
	ArrayList<String> dup1=new ArrayList<String>();
	ArrayList<String> dup2=new ArrayList<String>();
	ArrayList<String> nondup1=new ArrayList<String>();
	ArrayList<String> nondup2=new ArrayList<String>();
	ArrayList<Double> dup_scores=new ArrayList<Double>();
	ArrayList<Double> nondup_scores=new ArrayList<Double>();
	ArrayList<ArrayList<Integer>> dupFeats=new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> nondupFeats=new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Integer>> prunedDupFeats=new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Integer>> prunedNondupFeats=new ArrayList<ArrayList<Integer>>();
	
	
	ImportGoldStandard gold;
	
	public Phase2(ArrayList<String> reducerOutput){
		
			nondupIndex1=null;
			nondupIndex2=null;
			dupIndex1=null;
			dupIndex2=null;
			
		
		for(String line:reducerOutput){
			String[] orig=line.split("\t");
			double score=Double.parseDouble(orig[0]);
			if(score>=Parameters.ut){
				int index=containsPair(orig[2],orig[3],true);
				if(index==-1){
					populateFeat(orig[1],true);
					dup_scores.add(score);
					dup1.add(orig[2]);
					dup2.add(orig[3]);
					dupIndexCount.add(1);
				}
				else dupIndexCount.set(index,dupIndexCount.get(index)+1);
			}
			else if(score<=Parameters.lt){
				int index=containsPair(orig[2],orig[3],false);
				if(index==-1){
					populateFeat(orig[1],false);
					nondup_scores.add(score);
					nondup1.add(orig[2]);
					nondup2.add(orig[3]);
					nondupIndexCount.add(1);
				}
				else nondupIndexCount.set(index,nondupIndexCount.get(index)+1);
			
			}
		}
		
	}
	
	public Phase2(ImportGoldStandard gold, String evalFile)throws IOException{
		this.gold=gold;
		Scanner in=new Scanner(new FileReader(evalFile));
		while(in.hasNextLine()){
			String l1=in.nextLine();
			String l2=null;
			String l3=null;
			String l4=null;
			if(in.hasNextLine())
				l2=in.nextLine();
			if(in.hasNextLine())
				l3=in.nextLine();
			if(in.hasNextLine())
				l4=in.nextLine();
			
			String[] s1=l1.split("\t");
			int el1=Integer.parseInt(l2.split(" ")[0]);
			int el2=Integer.parseInt(l2.split(" ")[1]);
			double score=Double.parseDouble(s1[0]);
			if(score>=Parameters.ut){
				
				int index=containsPair(el1,el2,true);
				if(index==-1){
					populateFeat(s1[1],true);
					
					dup1.add(l3);
					dup2.add(l4);
					dup_scores.add(score);
					dupIndex1.add(el1);
					dupIndex2.add(el2);
					dupIndexCount.add(1);
				}
				else dupIndexCount.set(index,dupIndexCount.get(index)+1);
			}
			else if(score<=Parameters.lt){
				
				int index=containsPair(el1,el2,false);
				if(index==-1){
					populateFeat(s1[1],false);
					
					nondup1.add(l3);
					nondup2.add(l4);
					nondup_scores.add(score);
					nondupIndex1.add(el1);
					nondupIndex2.add(el2);
					nondupIndexCount.add(1);
				}
				else nondupIndexCount.set(index,nondupIndexCount.get(index)+1);
			}
			
		}
		in.close();
		if(gold!=null){
		evaluateStatisticsAll();
		evaluatePrunedStatistics();
		}
	}
	
	//nulls everything except pruned sets
	public void cleanUp(){
		dupIndex1=null;
		dupIndex2=null;
		nondupIndex1=null;
		nondupIndex2=null;
		dupIndexCount=null;
		nondupIndexCount=null;
		dup1=null;
		dup2=null;
		nondup1=null;
		nondup2=null;
		dup_scores=null;
		nondup_scores=null;
		dupFeats=null;
		nondupFeats=null;
		gold=null;
	}
	
	//-1 if doesn't contain, otherwise index
	private int containsPair(int i, int j, boolean dup){
		ArrayList<Integer> p1=null;
		ArrayList<Integer> p2=null;
		if(dup){
			p1=dupIndex1;
			p2=dupIndex2;
		}
		else{
			p1=nondupIndex1;
			p2=nondupIndex2;
		}
		for(int p=0; p<p1.size(); p++){
			if((int)p1.get(p)==i)
				if((int)p2.get(p)==j)
					return p;
			if((int)p1.get(p)==j)
				if((int)p2.get(p)==i)
					return p;
		}
		
		return -1;
		
	}
	
	//-1 if doesn't contain, otherwise index: compares actual string values
		private int containsPair(String i, String j, boolean dup){
			ArrayList<String> p1=null;
			ArrayList<String> p2=null;
			if(dup){
				p1=dup1;
				p2=dup2;
			}
			else{
				p1=nondup1;
				p2=nondup2;
			}
			for(int p=0; p<p1.size(); p++){
				if(p1.get(p).equals(i))
					if(p2.get(p).equals(j))
						return p;
				if(p1.get(p).equals(j))
					if(p2.get(p).equals(i))
						return p;
			}
			
			return -1;
			
		}
		
	private void evaluatePrunedStatistics(){
		
		System.out.println("Duplicate Precision\tDupsRequested\tDupsFound");
		HashMap<Double,HashSet<Integer>> scoremap=new HashMap<Double,HashSet<Integer>>();
		for(int i=0; i<dup_scores.size(); i++){
			if(!scoremap.containsKey(dup_scores.get(i)))
				scoremap.put(dup_scores.get(i),new HashSet<Integer>());
			scoremap.get(dup_scores.get(i)).add(i);
		}
		ArrayList<Double> scores=new ArrayList<Double>(scoremap.keySet());
		Collections.sort(scores);
		for(Parameters.dupDemanded=1000; Parameters.dupDemanded<=5000; Parameters.dupDemanded+=500){
			
			int count=0;
			int dupCorrect=0;
			
			for(int i=scores.size()-1; i>=0; i--){
				for(int j:scoremap.get(scores.get(i))){
					if(gold.contains(dupIndex1.get(j),dupIndex2.get(j)))
							dupCorrect++;
				}
				count+=scoremap.get(scores.get(i)).size();
				if(count>=Parameters.dupDemanded)
					break;
			}
			
			double precision=1.0*dupCorrect/count;
			System.out.println(precision+"\t"+Parameters.dupDemanded+"\t"+count);
				
		}
		
		
		System.out.println("NonDuplicate Precision\tNonDupsRequested\tNonDupsFound");
		scoremap=new HashMap<Double,HashSet<Integer>>();
		for(int i=0; i<nondup_scores.size(); i++){
			if(!scoremap.containsKey(nondup_scores.get(i)))
				scoremap.put(nondup_scores.get(i),new HashSet<Integer>());
			scoremap.get(nondup_scores.get(i)).add(i);
		}
		scores=new ArrayList<Double>(scoremap.keySet());
		Collections.sort(scores);
		for(Parameters.nondupDemanded=1000; Parameters.nondupDemanded<=5000; Parameters.nondupDemanded+=500){
			
			int count=0;
			int nondupCorrect=0;
			
			for(int i=0; i<scores.size(); i++){
				for(int j:scoremap.get(scores.get(i))){
					if(!gold.contains(nondupIndex1.get(j),nondupIndex2.get(j)))
							nondupCorrect++;
				}
				count+=scoremap.get(scores.get(i)).size();
				if(count>=Parameters.nondupDemanded)
					break;
			}
			
			double precision=1.0*nondupCorrect/count;
			System.out.println(precision+"\t"+Parameters.nondupDemanded+"\t"+count);
				
		}
		
		
	}
		
	private void evaluateStatisticsAll(){
		System.out.println("UT\tDuplicate Precision\tDuplicate Recall");
		for(Parameters.ut=0.03; Parameters.ut<=1.009; Parameters.ut+=0.01){
			int dupCorrect=0;
			int duptotal=0;
			for(int i=0; i<dupIndex1.size(); i++)
				if(dup_scores.get(i)>=Parameters.ut){
					if(gold.contains(dupIndex1.get(i),dupIndex2.get(i)))
						dupCorrect++;
					duptotal++;
				}
			if(duptotal==0)
			{
				System.out.println("Warning! No duplicates above threshold "+Parameters.ut);
				continue;
			}
			double dupprecision=1.0*dupCorrect/(duptotal);
			double duprecall=1.0*dupCorrect/(gold.num_dups);
			System.out.println(Parameters.ut+"\t"+dupprecision+"\t"+duprecall);
		}
		
		System.out.println("LT\tNon-Duplicate Precision");
		for(Parameters.lt=0.01; Parameters.lt>=0.0015; Parameters.lt-=0.001){
			int nondupCorrect=0;
			int nonduptotal=0;
			for(int i=0; i<nondupIndex1.size(); i++)
				if(nondup_scores.get(i)<Parameters.lt){
					if(!gold.contains(nondupIndex1.get(i),nondupIndex2.get(i)))
						nondupCorrect++;
					nonduptotal++;
				}
			if(nonduptotal==0)
			{
				System.out.println("Warning! No non-duplicates below threshold "+Parameters.lt);
				continue;
			}
			double nondupprecision=1.0*nondupCorrect/(nonduptotal);
			
			System.out.println(Parameters.lt+"\t"+nondupprecision);
		}
	}
	
	private void evaluateStatistics(){
		int dupCorrect=0;
		int nondupCorrect=0;
		System.out.println("Num Unique Dups "+dupIndex1.size());
		System.out.println("Num Unique Non-Dups "+nondupIndex1.size());
		
		
		for(int i=0; i<dupIndex1.size(); i++)
			if(gold.contains(dupIndex1.get(i),dupIndex2.get(i)))
				dupCorrect++;
		
		for(int i=0; i<nondupIndex1.size(); i++)
			if(!gold.contains(nondupIndex1.get(i),nondupIndex2.get(i)))
				nondupCorrect++;
		
		double dupprecision=1.0*dupCorrect/(dupIndex1.size());
		double duprecall=1.0*dupCorrect/(gold.num_dups);
		double nondupprecision=1.0*nondupCorrect/(nondupIndex1.size());
		double nonduprecall=1.0*nondupCorrect/(gold.total_pairs-gold.num_dups);
		
		
		System.out.println("Duplicate Precision "+dupprecision);
		System.out.println("Duplicate Recall "+duprecall);
		System.out.println("Non-Duplicate Precision "+nondupprecision);
		System.out.println("Non-Duplicate Recall "+nonduprecall);
		//analyzeFeats(5);
		
	}
	
	public void printScoreMapStatistics(HashMap<Double, HashSet<Integer>> scoreMap,ArrayList<Double> sortedscores){
		int count45=0;
		int count56=0;
		int count67=0;
		int count7t=0;
		for(double a:sortedscores)
			if(a>=0.07&&a<0.15)
				count45+=scoreMap.get(a).size();
			else if(a>=0.15&&a<0.2)
			count56+=scoreMap.get(a).size();
			else if(a>=0.2&&a<0.5)
				count67+=scoreMap.get(a).size();
			else if(a>=0.5)
				count7t+=scoreMap.get(a).size();
		System.out.println(count45+" "+count56+" "+count67+" "+count7t);
		System.exit(-1);
	}
	
	public void generatePrunedFeatureSets(){
		
		HashMap<Double, HashSet<Integer>> scoreMap=new HashMap<Double,HashSet<Integer>>();
		for(int i=0; i<dup_scores.size(); i++){
			if(!scoreMap.containsKey(dup_scores.get(i)))
				scoreMap.put(dup_scores.get(i),new HashSet<Integer>());
			scoreMap.get(dup_scores.get(i)).add(i);
		}
		ArrayList<Double> sortedscores=new ArrayList<Double>(scoreMap.keySet());
		Collections.sort(sortedscores);
		//printScoreMapStatistics(scoreMap,sortedscores);
		
		int count=0;
		for(int i=0; i<sortedscores.size() && count<Parameters.dupDemanded; i++){
			addToPrunedFeatures(scoreMap.get(sortedscores.get((sortedscores.size()-1)-i)),true);
			count+=scoreMap.get(sortedscores.get((sortedscores.size()-1)-i)).size();
		}
		
		scoreMap=new HashMap<Double,HashSet<Integer>>();
		for(int i=0; i<nondup_scores.size(); i++){
			if(!scoreMap.containsKey(nondup_scores.get(i)))
				scoreMap.put(nondup_scores.get(i),new HashSet<Integer>());
			scoreMap.get(nondup_scores.get(i)).add(i);
		}
		sortedscores=new ArrayList<Double>(scoreMap.keySet());
		Collections.sort(sortedscores);
		count=0;
		for(int i=0; i<sortedscores.size() && count<Parameters.nondupDemanded-1; i++){
			addToPrunedFeatures(scoreMap.get(sortedscores.get(i)),false);
			count+=scoreMap.get(sortedscores.get(i)).size();
		}
		

		ArrayList<Integer> zeros=new ArrayList<Integer>(dupFeats.get(0).size());
		for(int i=0; i<dupFeats.get(0).size(); i++)
			zeros.add(0);
		prunedNondupFeats.add(zeros);
	}
	

	private void addToPrunedFeatures(HashSet<Integer> q, boolean dup){
		if(dup){
			for(int i:q)
				prunedDupFeats.add(dupFeats.get(i));
		}
		else{
			for(int i:q)
				prunedNondupFeats.add(nondupFeats.get(i));
			
		}
	}
	
	private void populateFeat(String feat, boolean dup){
		ArrayList<Integer> q=new ArrayList<Integer>();
		String[] d=feat.split(" ");
		for(int i=0; i<d.length; i++)
			q.add(Integer.parseInt(d[i]));
		if(dup)
			dupFeats.add(q);
		else
			nondupFeats.add(q);
	}
	
	@SuppressWarnings("unused")
	private void analyzeNonDups(){
		System.out.println("Count "+sum(nondupIndexCount));
		System.out.println("Min Score "+findMaxOrMin(nondup_scores,false));
		System.out.println("Max Score "+findMaxOrMin(nondup_scores,true));
	}
	
	private double findMaxOrMin(ArrayList<Double> score, boolean max){
		if(score.size()==0)
			return 0.0;
		ArrayList<Double> p=new ArrayList<Double>(score);
		Collections.sort(p);
		if(max){
			return p.get(p.size()-1);
		}
		else
			return p.get(0);
	}
	
	private double countFeatPercent(int index, boolean dup){
		int count=0;
		ArrayList<Integer> q=null;
		if(dup)
			q=dupFeats.get(index);
		else
			q=nondupFeats.get(index);
		
		for(int i=0; i<q.size(); i++)
			count+=q.get(i);
		
		return count*100.0/q.size();
	
	
	}
	
	private void analyzeFeats(int granularity){
		int[] percentiles=new int[100/granularity];
		int[] count=new int[100/granularity];
		for(int i=0; i<dupFeats.size(); i++){
			if(gold.contains(dupIndex1.get(i),dupIndex2.get(i)))
				percentiles[(int) (countFeatPercent(i,true)/granularity)]++;
			count[(int) (countFeatPercent(i,true)/granularity)]++;
		}
		
		System.out.println("Percentile \t Total \t Correct");
		for(int i=0; i<100/granularity; i++)
			System.out.println((i+1)*granularity+" \t \t"+count[i]+" \t "+percentiles[i]);
		printStatistics();
	
	}
	
	public double[] printStatistics(){
		double dupMean=0;
		double dupVar=0;
		double nondupMean=0;
		double nondupVar=0;
		double[] result=new double[2];
		for(int i=0; i<dupFeats.size(); i++){
			dupMean+=sum(dupFeats.get(i));
			dupVar+=Math.pow(sum(dupFeats.get(i)),2);
		}
		
		for(int i=0; i<nondupFeats.size(); i++){
			nondupMean+=sum(nondupFeats.get(i));
			nondupVar+=Math.pow(sum(nondupFeats.get(i)),2);
			
		}
		dupMean=dupMean/dupFeats.size();
		dupVar=dupVar/dupFeats.size()-Math.pow(dupMean,2);
		nondupMean=nondupMean/nondupFeats.size();
		nondupVar=nondupVar/nondupFeats.size()-Math.pow(nondupMean,2);
		double num=dupFeats.get(0).size()*1.0/100;
		//System.out.println("Duplicate Mean variance percents= "+dupMean/num+" "+Math.sqrt(dupVar)/num);
		//System.out.println("Non-Duplicate Mean variance percents= "+nondupMean/num+" "+Math.sqrt(nondupVar)/num);
		//System.out.println("total feats num "+dupFeats.get(0).size());
		System.out.println("Mean: "+dupMean+"  Variance: "+dupVar);
		result[0]=dupMean;
		result[1]=Math.sqrt(dupVar);
		
		return result;
	
	}
	
	private int sum(ArrayList<Integer> q){
		
		int sum=0;
		for(int k=0; k<q.size(); k++)
			sum+=q.get(k);
		return sum;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		
	}


}
