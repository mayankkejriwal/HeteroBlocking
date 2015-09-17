package gold;

import java.io.*;
import java.util.*;

public class TreasuryJCT {

	static String prefix_source="/host/heteroDatasets/iswc_experiments/government/2013/goldStandard_jct_treasury_function";
	static String prefix_target="/host/heteroDatasets/iswc_experiments/government/2013/goldStandard_jct_treasury_function1";
	static String prefix=null;
	public static void main(String[] args) throws IOException {
		/*
		for(int i=2001; i<=2013; i++)
			makeGold_title(Integer.toString(i));
		*/
		
		incrementGold(prefix_source,prefix_target,172+241+250+240,161+165+173+173);

	}
	
	public static void incrementGold(String gold1, String gold2, int incr1, int incr2)throws IOException{
		Scanner in=new Scanner(new File(gold1));
		PrintWriter out=new PrintWriter(new File(gold2));
		while(in.hasNextLine()){
			String line=in.nextLine();
			//System.out.println(line);
			String[] cols=line.split(" ");
			//System.out.println(cols[0]);
			//System.out.println(cols[1]);
			
			int i=Integer.parseInt(cols[0])+incr1;
			int j=Integer.parseInt(cols[1])+incr2;
			out.println(i+" "+j);
		}
		out.close();
		in.close();
	}
	
	public static void makeGold(String year)throws IOException{
		Scanner in_t=new Scanner(new FileReader(prefix+year+"/Treasury_"+year+".csv"));
		
		
		ArrayList<String> treasury=new ArrayList<String>();
		//ArrayList<String> jct=new ArrayList<String>();
		HashMap<String,ArrayList<Integer>> tmap=new HashMap<String, ArrayList<Integer>>();
		while(in_t.hasNextLine()){
			String line=in_t.nextLine();
			line=line.toLowerCase();
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
			  treasury.add(line);
			  String key=line.split(",")[0]+","+line.split(",")[1];
			  if(!tmap.containsKey(key))
				  tmap.put(key,new ArrayList<Integer>());
			  tmap.get(key).add(treasury.size()-1);
		}
		
		in_t.close();
		
		Scanner in_j=new Scanner(new FileReader(prefix+year+"/JCT_"+year+".csv"));
		PrintWriter out=new PrintWriter(new File(prefix+year+"/goldStandard_jct_treasury"));
		int index=0;
		while(in_j.hasNextLine()){
			String line=in_j.nextLine();
			line=line.toLowerCase();
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
			  String key=line.split(",")[0]+","+line.split(",")[1];
			  if(tmap.containsKey(key)){
				  for(int k:tmap.get(key))
					  out.println(index+" "+k);
			  }
			  index++;
		}
		out.close();
		in_j.close();
	}
	
	public static void makeGold_function(String year)throws IOException{
		Scanner in_t=new Scanner(new FileReader(prefix+year+"/Treasury_"+year+".csv"));
		
		
		ArrayList<String> treasury=new ArrayList<String>();
		//ArrayList<String> jct=new ArrayList<String>();
		HashMap<String,ArrayList<Integer>> tmap=new HashMap<String, ArrayList<Integer>>();
		while(in_t.hasNextLine()){
			String line=in_t.nextLine();
			line=line.toLowerCase();
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
			  treasury.add(line);
			  String key=line.split(",")[0];
			  if(!tmap.containsKey(key))
				  tmap.put(key,new ArrayList<Integer>());
			  tmap.get(key).add(treasury.size()-1);
		}
		
		in_t.close();
		
		Scanner in_j=new Scanner(new FileReader(prefix+year+"/JCT_"+year+".csv"));
		PrintWriter out=new PrintWriter(new File(prefix+year+"/goldStandard_jct_treasury_function"));
		int index=0;
		while(in_j.hasNextLine()){
			String line=in_j.nextLine();
			line=line.toLowerCase();
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
			  String key=line.split(",")[0];
			  if(tmap.containsKey(key)){
				  for(int k:tmap.get(key))
					  out.println(index+" "+k);
			  }
			  index++;
		}
		out.close();
		in_j.close();
	}
	
	public static void makeGold_title(String year)throws IOException{
		Scanner in_t=new Scanner(new FileReader(prefix+year+"/Treasury_"+year+".csv"));
		
		
		ArrayList<String> treasury=new ArrayList<String>();
		//ArrayList<String> jct=new ArrayList<String>();
		HashMap<String,ArrayList<Integer>> tmap=new HashMap<String, ArrayList<Integer>>();
		while(in_t.hasNextLine()){
			String line=in_t.nextLine();
			line=line.toLowerCase();
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
			  treasury.add(line);
			  String key=line.split(",")[1];
			  if(!tmap.containsKey(key))
				  tmap.put(key,new ArrayList<Integer>());
			  tmap.get(key).add(treasury.size()-1);
		}
		
		in_t.close();
		
		Scanner in_j=new Scanner(new FileReader(prefix+year+"/JCT_"+year+".csv"));
		PrintWriter out=new PrintWriter(new File(prefix+year+"/goldStandard_jct_treasury_title"));
		int index=0;
		while(in_j.hasNextLine()){
			String line=in_j.nextLine();
			line=line.toLowerCase();
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
			  String key=line.split(",")[1];
			  if(tmap.containsKey(key)){
				  for(int k:tmap.get(key))
					  out.println(index+" "+k);
			  }
			  index++;
		}
		out.close();
		in_j.close();
	}

}
