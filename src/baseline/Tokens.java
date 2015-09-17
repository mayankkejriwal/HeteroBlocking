package baseline;

import general.CSVParser;

import java.util.*;
import java.io.*;

import christen.Parameters;
import christen.TFIDF;

public class Tokens {
	
	TFIDF data;
	HashMap<String,HashSet<Integer>> tokens;
	private HashSet<String> forbidden;
	
	
	public Tokens(String dataset1)throws IOException{
		Scanner in=new Scanner(new FileReader(dataset1));
		ArrayList<String> tuples=new ArrayList<String>();
		tokens=new HashMap<String,HashSet<Integer>>();
		forbidden=new HashSet<String>();
		int i=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			
			
			tuples.add(line);
			
			String[] tks=(new CSVParser()).parseLine(line);
			for(String tk:tks){
				if(forbidden.contains(tk))
					continue;
				if(!tokens.containsKey(tk))
					tokens.put(tk,new HashSet<Integer>());
				if(tokens.get(tk).size()==Parameters.maxtokentuples-1){
					forbidden.add(tk);
					tokens.remove(tk);
				}
				else
					tokens.get(tk).add(i);
			}
			i++;
		}
		in.close();
		data=new TFIDF(tuples);
		System.out.println("Size of tokens: "+tokens.size());
	}
	
	public HashSet<Integer> getCluster(Tokens data1, int index){
		HashSet<Integer> tuples=new HashSet<Integer>();
		String[] tks=null;
		try{
		tks=(new CSVParser()).parseLine(data1.data.getTuples().get(index));
		}
		catch(Exception e){
			System.out.println("Exception in Tokens.getCluster!!");
		}
		
		for(String token:tks){
			if(forbidden.contains(token)||!tokens.containsKey(token))
				continue;
			
			mergeSets(tuples,tokens.get(token));
			if(tuples.size()>Parameters.maxpairs)
				return null;
			
		}
		return data1.data.pruneMap(data,index,tuples,Parameters.ut);
	}
	
	private void mergeSets(HashSet<Integer> big, HashSet<Integer> small){
		for(int a:small)
			big.add(a);
	}

}
