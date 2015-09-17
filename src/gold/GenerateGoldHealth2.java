package gold;

import general.CSVParser;

import java.io.*;
import java.util.*;

import christen.Parameters;

public class GenerateGoldHealth2 {

	
	public static void main(String[] args) throws IOException {
		String prefix="/host/heteroDatasets/juan_health/";
		ArrayList<String> healthcare=new ArrayList<String>();
		
		
		Scanner in=new Scanner(new File(prefix+"healthcare_mod.csv"));
		while(in.hasNextLine()){
			CSVParser p=new CSVParser();
			String[] line=p.parseLine(in.nextLine());
			healthcare.add(line[0]);
		}
		
		in.close();
		
		
		in=new Scanner(new File(prefix+"ontology_mod1.csv"));
		PrintWriter out=new PrintWriter(prefix+"goldStandard_healthcare_ontology");
		int count=0;
		while(in.hasNextLine()){
			String line=in.nextLine().toLowerCase();
			for(int i=0; i<healthcare.size(); i++){
				String[] split=healthcare.get(i).split(Parameters.splitstring);
				int cont=0;
				for(String s:split){
					String j=s.toLowerCase();
					if(!line.contains(j)){
						cont=1;
						break;
					}
				}
				if(cont==0){
					out.println(i+" "+count);
				}
				else
					cont=0;
				
			}
			count++;
			
		}
		in.close();
		out.close();
		
	}

}
