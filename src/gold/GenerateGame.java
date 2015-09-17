package gold;

import java.util.*;
import java.io.*;

public class GenerateGame {

	
	public static void main(String[] args)throws IOException {
		String dbpedia1="/home/mayankkejriwal/Dropbox/datasets/KLK/game/clean/dbpedia.csv";
		Scanner in1=new Scanner(new FileReader(dbpedia1));
		if(in1.hasNextLine())
			in1.nextLine();
		ArrayList<String> tuples1=new ArrayList<String>();
		while(in1.hasNextLine()){
			String total=in1.nextLine();
			String[] res=total.split("\"");
			if(res.length!=1){
				total="";
				for(int j=1; j<res.length; j+=2)
					res[j]=res[j].replace(",","");
				for(int p=0; p<res.length; p++)
					total+=res[p];
				}
			tuples1.add(total);
		}
		in1.close();
		
		String dbpedia2="/host/heteroDatasets/game/dbpedia.csv";
		ArrayList<String> tuples2=new ArrayList<String>();
		HashMap<String,Integer> subject=new HashMap<String,Integer>();
		Scanner in2=new Scanner(new FileReader(dbpedia2));
		
		while(in2.hasNextLine()){
			String line=in2.nextLine();
			String subj=line.split(",")[0];
			tuples2.add(line);
			subject.put(subj,tuples2.size()-1);
		}
		in2.close();
		
		Scanner in3=new Scanner(new FileReader("/home/mayankkejriwal/Dropbox/datasets/KLK/game/clean/record_linkage_baseline_vgchartz_dbpedia.txt"));
		PrintWriter out=new PrintWriter(new File("/host/heteroDatasets/game/goldStandard"));
		while(in3.hasNextLine()){
			String[] m=in3.nextLine().split(" ");
			int db=Integer.parseInt(m[1]);
			String subj=tuples1.get(db).split(",")[0];
			String tuple2=tuples2.get(subject.get(subj));
			if(!tuple2.split(",")[0].equals(subj))
				System.out.println("Error! In tuple 1: "+subj+" In tuple 2: "+tuple2);
			out.println(m[0]+" "+subject.get(subj));
		}
		
		out.close();
		in3.close();

	}

}
