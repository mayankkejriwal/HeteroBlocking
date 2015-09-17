package general;

import java.util.*;
import java.io.*;

public class NullMiner {

	HashMap<Integer,HashSet<Integer>> NullRecords;	//key is the column, values are records with null in that column
	//double[][] ScoreMatrix;
	int minRecordsThreshold=10;
	int size=-1;
	String inputfilepath;
	
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/iswc_experiments/USA/";
		new NullMiner(prefix+"coding2_prop.csv",true);
		//PrintUtilities.printDoubleMatrix(a.ScoreMatrix);
	}
	
	
	//csv file must be property table formatted and with no header
	public NullMiner(String csvProp,boolean file)throws IOException{
		Scanner in=new Scanner(new File(csvProp));
		inputfilepath=csvProp;
		NullRecords=new HashMap<Integer,HashSet<Integer>>();
		int count=0;
		//int size=-1;
		while(in.hasNextLine()){
			String line=in.nextLine();
			CSVParser p=new CSVParser();
			String[] tokens=p.parseLine(line);
			if(size==-1)
				size=tokens.length;
			for(int i=0; i<tokens.length; i++){
				if(tokens[i].contains("null")){
					if(!NullRecords.containsKey(i))
						NullRecords.put(i,new HashSet<Integer>());
					NullRecords.get(i).add(count);
				}
			}
			count++;
				
			
		}
		in.close();
		removeSmallSets(minRecordsThreshold);
		//ScoreMatrix =new double[size][size];
		//populateScoreMatrix();
		if(file)
		printNullSeparationIndicesToFile();
		else
		printNullSeparationIndices();
	}
	
	public void printNullSeparationIndices(){
		for(int i=1; i<size; i++)
			for(int j=0; j<i; j++)
				if(NullRecords.containsKey(i)&&NullRecords.containsKey(j))
					if(computeScore(NullRecords.get(i),NullRecords.get(j))<0.1)
						System.out.println(i+" "+j);
				
	}
	
	public void printNullSeparationIndicesToFile()throws IOException{
		for(int i=1; i<size; i++)
			for(int j=0; j<i; j++)
				if(NullRecords.containsKey(i)&&NullRecords.containsKey(j))
					if(computeScore(NullRecords.get(i),NullRecords.get(j))<0.1)
						outputNullFiles(inputfilepath,i,j);
				
	}
	
	//removes all entries from NullRecords that have trivial number of associated records
	public void removeSmallSets(int minRecords){
		HashSet<Integer> toRemove=new HashSet<Integer>();
		for(int key:NullRecords.keySet())
			if(NullRecords.get(key).size()<minRecords)
				toRemove.add(key);
		
		for(int key:toRemove)
			NullRecords.remove(key);
	}
	
	private HashSet<Integer> HashSetIntersect(HashSet<Integer> a,HashSet<Integer> b){
		if(a==null||b==null)
			return null;
		else if(a.size()==0||b.size()==0)
			return new HashSet<Integer>();
		Iterator<Integer> q=a.iterator();
		HashSet<Integer> result=new HashSet<Integer>();
		while(q.hasNext()){
			int m=q.next();
			if(b.contains(m))
				result.add(m);
			
		}
		return result;
	}
	
	private HashSet<Integer> HashSetUnion(HashSet<Integer> a,HashSet<Integer> b){
		if(a==null||b==null)
			return null;
		HashSet<Integer> result=new HashSet<Integer>(a);
		for(int el:b)
			result.add(el);
		return result;
	}
	
	private double computeScore(HashSet<Integer> a,HashSet<Integer> b){
		HashSet<Integer> union=HashSetUnion(a,b);
		HashSet<Integer> intersection=HashSetIntersect(a,b);
		if(intersection==null||union==null||union.size()==0)
			return -1;
		return intersection.size()*1.0/union.size();
		
	}
	
	//note: will output two files so named by appending _[index] to inputfile name
	public void outputNullFiles(String inputfile, int index1, int index2)throws IOException{
		Scanner in=new Scanner(new File(inputfile));
		PrintWriter out1=new PrintWriter(new File(inputfile.substring(0,inputfile.length()-4)+"_"+index1+".csv"));
		PrintWriter out2=new PrintWriter(new File(inputfile.substring(0,inputfile.length()-4)+"_"+index2+".csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			String[] tokens=(new CSVParser()).parseLine(line);
			if(tokens[index1].contains("null"))
				out1.println(line);
			if(tokens[index2].contains("null"))
				out2.println(line);
		}
		
		out1.close();
		out2.close();
		in.close();
	}

}
