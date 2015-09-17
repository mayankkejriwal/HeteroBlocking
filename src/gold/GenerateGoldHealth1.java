package gold;

import general.CSVParser;
import java.util.*;
import java.io.*;

public class GenerateGoldHealth1 {

	
	public static void main(String[] args) throws IOException{
		String prefix="/host/heteroDatasets/juan_health/";
		HashMap<String,HashSet<Integer>> healthcare=new HashMap<String,HashSet<Integer>>();
		
		
		Scanner in2=new Scanner(new FileReader(prefix+"healthcare_mod.csv"));
		int count=0;
		while(in2.hasNextLine()){
			String line=in2.nextLine();
			CSVParser a=new CSVParser();
			String[] keys=a.parseLine(line);
			//System.out.println(keys.length);
			if(keys.length<2){
				count++;
				continue;
			}
			String key=keys[1];
			if(!healthcare.containsKey(key))
				healthcare.put(key,new HashSet<Integer>());
			healthcare.get(key).add(count);
			count++;
		}
		in2.close();
		
		Scanner in1=new Scanner(new FileReader(prefix+"npi_mod.csv"));
		count=0;
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard_healthcare_npi"));
		while(in1.hasNextLine()){
			String line=in1.nextLine();
			if(checkForMatch(healthcare.keySet(),line)==null){
				count++;
				continue;
			}
			
				for(int i:healthcare.get(checkForMatch(healthcare.keySet(),line))){
					out.println(i+" "+count);
				}
			
			count++;
		}
		out.close();
		in1.close();

	}
	
	private static String checkForMatch(Set<String> keys, String line){
		for(String key:keys)
			if(line.contains(key))
				return key;
		return null;
	}

}
