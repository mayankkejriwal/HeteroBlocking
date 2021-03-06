package phases;

import general.CSVParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import christen.Parameters;
import featureSelection.GenerateFeatures;

//reducer is different in that it writes out to file continuously rather than
//populate struct first, then write out all at once. Use for reduce-memory intensive
public class Phase1_mod {

	HashMap<String,HashSet<String>> context;
	ArrayList<String> data1;
	ArrayList<String> data2;
	static int numAttributes1;
	static int numAttributes2;
	
	//constructor serves as mapper. context will get populated, 
	//and reducer will get called in final statement of constructor
	//Some changes compared to original program:
	// 1.converts line to lowercase 3. trims token and checks for emptiness
	public Phase1_mod(String dataset1, String dataset2, String output)throws IOException{
		
		context=new HashMap<String,HashSet<String>>();
		Scanner in1=new Scanner(new FileReader(new File(dataset1)));
		data1=new ArrayList<String>();
		
		
		int index=0;
		while(in1.hasNextLine()){
			String line=in1.nextLine();
			System.out.println(index);
			data1.add(new String(line));
			line=line.toLowerCase();
			for(String forb:Parameters.forbiddenwords)
				line=line.replace(forb,"");
			line=line.replace("\t"," ");
			/*
			if(line.substring(line.length()-1,line.length()).equals(","))
				line=line+" ";
			String[] res=line.split("\"");
			  if(res.length>1){
			   	String total="";
				for(int j=1; j<res.length; j+=2)
					res[j]=res[j].replace(","," ");
				for(int p=0; p<res.length; p++)
					total+=res[p];
				line=total;
						
			  }
		
			  String[] cols=line.split(",");*/
			CSVParser test=new CSVParser();
			String[] cols=test.parseLine(line);
			  numAttributes1=cols.length;
			  for(int i=0; i<cols.length; i++){
				String[] tokens=cols[i].split(Parameters.splitstring);
				for(int j=0; j<tokens.length; j++){
					if(tokens[j].trim().length()==0||tokens[j].trim().equals("null"))
						continue;
					String word=new String(tokens[j]);
					String v=new String(line);
					if(!context.containsKey(word))
						context.put(word, new HashSet<String>());
					context.get(word).add(v+"\t1\t"+index);
					//System.out.println(v+";"+index);
				}
			  }
			  index++;
		}
		
		in1.close();
		
		
		
		
		data2=new ArrayList<String>();
		Scanner in2=new Scanner(new FileReader(new File(dataset2)));
		
		 index=0;
		while(in2.hasNextLine()){
			String line=in2.nextLine();
			
			data2.add(new String(line));
			line=line.toLowerCase();
			for(String forb:Parameters.forbiddenwords)
				line=line.replace(forb,"");
			line=line.replace("\t"," ");
			/*
			if(line.substring(line.length()-1,line.length()).equals(","))
				line=line+" ";
			String[] res=line.split("\"");
			  if(res.length>1){
			   	String total="";
				for(int j=1; j<res.length; j+=2)
					res[j]=res[j].replace(","," ");
				for(int p=0; p<res.length; p++)
					total+=res[p];
				line=total;
						
			  }
		
			  String[] cols=line.split(",");*/
			CSVParser test=new CSVParser();
			String[] cols=test.parseLine(line);
			  numAttributes2=cols.length;
			  for(int i=0; i<cols.length; i++){
				String[] tokens=cols[i].split(Parameters.splitstring);
				for(int j=0; j<tokens.length; j++){
					if(tokens[j].trim().length()==0||tokens[j].trim().equals("null"))
						continue;
					String word=new String(tokens[j]);
					String v=new String(line);
					if(!context.containsKey(word))
						context.put(word, new HashSet<String>());
					context.get(word).add(v+"\t2\t"+index);
					//System.out.println(v+";"+index);
				}
			  }
			  index++;
		}
		
		in2.close();
		
		//printBlocks("bel-air hotel");
		Reducer(output);
	}
	
	public void Reducer(String output)throws IOException{
		
		
		System.out.println("Number of unique tokens "+context.keySet().size());
		ArrayList<String> keys=new ArrayList<String>(context.keySet());
		Collections.sort(keys);
		PrintWriter out=new PrintWriter(new File(output));
		for(String key:keys){
			System.out.println(key+" "+context.get(key).size());
			if(context.get(key).size()>Parameters.maxmapperoutput)
			{
				
				
				continue;
			}
			
			ArrayList<String> tuples=new ArrayList<String>(context.get(key));
			ArrayList<String> tuples1=new ArrayList<String>();
			ArrayList<String> tuples2=new ArrayList<String>();
			ArrayList<Integer> index1=new ArrayList<Integer>();
			ArrayList<Integer> index2=new ArrayList<Integer>();
			for(int i=0; i<tuples.size(); i++){
				
				String[] m=tuples.get(i).split("\t");
				//System.out.println(m+"\n"+m[0]+" "+m[1]);
				if((int)Integer.parseInt(m[1])==1)
				{
					tuples1.add(m[0]);
					index1.add(Integer.parseInt(m[2]));
				}
				else if((int)Integer.parseInt(m[1])==2)
				{
					tuples2.add(m[0]);
					index2.add(Integer.parseInt(m[2]));
				}
				else
					System.out.println("Error in tuple parsing in reducer!");
			}
			
			
			
			if(tuples1.size()==0||tuples2.size()==0||tuples1.size()*tuples2.size()>Parameters.maxtokentuples)
				continue;
			
			//System.out.println(tuples1.size()*tuples2.size());
			GenerateFeatures f=new GenerateFeatures(tuples1,tuples2);
			//System.out.println("Done generating features");
			
			
			ArrayList<ArrayList<Integer>> dupFeatures=f.dupFeatures;
			ArrayList<ArrayList<Integer>> nondupFeatures=f.nondupFeatures;
			ArrayList<Double> dup_scores=f.dup_scores;
			ArrayList<Double> nondup_scores=f.nondup_scores;
			ArrayList<Integer> dups1=f.dup1;
			ArrayList<Integer> dups2=f.dup2;
			ArrayList<Integer> nondups1=f.nondup1;
			ArrayList<Integer> nondups2=f.nondup2;		

			for(int i=0; i<dup_scores.size(); i++){
				out.println(dup_scores.get(i)+"\t"+convertToString(dupFeatures.get(i)));
				
				out.println(index1.get(dups1.get(i))+" "+index2.get(dups2.get(i)));
				out.println(tuples1.get(dups1.get(i)));
				out.println(tuples2.get(dups2.get(i)));
				
				
			}
			for(int i=0; i<nondup_scores.size(); i++){
				out.println(nondup_scores.get(i)+"\t"+convertToString(nondupFeatures.get(i)));
				
				out.println(index1.get(nondups1.get(i))+" "+index2.get(nondups2.get(i)));
				out.println(tuples1.get(nondups1.get(i)));
				out.println(tuples2.get(nondups2.get(i)));
				
				
				
			}
			
			
			
		}
		
		
		
				
		out.close();
		
	}
	
	private static String convertToString(ArrayList<Integer> feature){

	    String res="";
	    for(int i=0; i<feature.size(); i++)
	    	res+=(feature.get(i)+" ");
	    return res.substring(0,res.length()-1);

	   }
	
	@SuppressWarnings("unused")
	private void printBlocks(String val){
		
		for(String m:context.keySet()){
			if(hashSetContainsString(context.get(m),val)){
			System.out.println("Blocking Key "+m);
			for(String j:context.get(m))
				System.out.println(j);
			System.out.println();
			}
		}
	}
	
	private boolean hashSetContainsString(HashSet<String> hash, String string){
		for(String p:hash)
			if(p.contains(string))
				return true;
		return false;
	}
	
}
