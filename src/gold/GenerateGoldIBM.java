package gold;

import java.io.*;

import java.util.*;

public class GenerateGoldIBM {

	public static void main(String[] args) throws IOException {
		game2();
		
	}
	
	private static void game2() throws IOException{
		String prefix="/host/heteroDatasets/game2/";
		String ibm_vgchartz="/host/heteroDatasets/game3/goldStandard_ibm_vgchartz";
		
		HashMap<Integer, HashSet<Integer>> ref=new HashMap<Integer,HashSet<Integer>>();
		String vgchartz_dbpedia="/host/heteroDatasets/game/goldStandard_vgchartz_dbpedia";
		Scanner in=new Scanner(new FileReader(vgchartz_dbpedia));
		while(in.hasNextLine()){
			String[] pair=in.nextLine().split(" ");
			int p1=Integer.parseInt(pair[0]);
			int p2=Integer.parseInt(pair[1]);
			if(!ref.containsKey(p1))
				ref.put(p1,new HashSet<Integer>());
			ref.get(p1).add(p2);
			
		}
		in.close();
		
		 in=new Scanner(new FileReader(ibm_vgchartz));
		 PrintWriter out=new PrintWriter(new File(prefix+"goldStandard_ibm_dbpedia"));
		 while(in.hasNextLine()){
			String[] pair=in.nextLine().split(" ");
			int p2=Integer.parseInt(pair[1]);
			if(!ref.containsKey(p2))
				continue;
			ArrayList<Integer> q=new ArrayList<Integer>(ref.get(p2));
			Collections.sort(q);
			for(int q1:q)
				out.println(pair[0]+" "+q1);
		 }
		 in.close();
		 out.close();
	}
	
	private static void game3()throws IOException{
		String prefix="/host/heteroDatasets/game3/";
		HashMap<String, HashSet<Integer>> vIndex=new HashMap<String,HashSet<Integer>>();
		ArrayList<String> vgchartz=new ArrayList<String>();
		
		Scanner in=new Scanner(new FileReader(prefix+"vgchartz.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			vgchartz.add(line);
			String[] comp=line.split(",");
			if(!vIndex.containsKey(comp[1]+","+comp[3]))
				vIndex.put(comp[1]+","+comp[3],new HashSet<Integer>());
			
			vIndex.get(comp[1]+","+comp[3]).add(vgchartz.size()-1);
		}
		in.close();
		
		in=new Scanner(new FileReader(prefix+"ibm.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard_ibm_vgchartz.csv"));
		int i=0;
		while(in.hasNextLine()){
			String line=in.nextLine();
			String[] comp=line.split(",");
			if(!vIndex.containsKey(comp[1]+","+comp[3]))
				System.out.println(line);
			else{
				for(int num:vIndex.get(comp[1]+","+comp[3]))
					out.println(i+" "+num);
			}
			i++;
		}

		out.close();
		in.close();
	}

}
