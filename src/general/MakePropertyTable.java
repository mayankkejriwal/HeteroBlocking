package general;
import java.io.*;
import java.util.*;


public class MakePropertyTable {

	//expects header in rdfFile which it will skip
	//will check for 'quotes' before applying delimiter
	//if opt is 2 multiset will be collected in single line
	//if opt is 1, then multi-sets will be collapsed to 'last' value of property
	//if opt is 0 then all combinatorial possibilities will be explored
	public MakePropertyTable(String rdfInputFile, String delimiter, String csvOutputFile, int opt)throws IOException{
		Scanner in=new Scanner(new FileReader(rdfInputFile));
		ArrayList<String[]> triples=new ArrayList<String[]>();
		int count=0;
		HashSet<String> properties=new HashSet<String>();
		if(in.hasNextLine())
			in.nextLine();
		while(in.hasNextLine()){
			String total=in.nextLine();
			String[] res=total.split("\"");
			if(res.length!=1){
				total="";
				for(int j=1; j<res.length; j+=2)
					res[j]=res[j].replace(",","");
				for(int p=0; p<res.length; p++)
					total+=res[p];
				}
			String[] split=total.split(delimiter);
			String[] triple=new String[3];
			if(split.length==3)
				triple=split;
			else
				for(int i=0; i<3; i++)
					if(i<split.length)
						triple[i]=split[i];
					else triple[i]="";
			if(triple.length!=3)
				System.out.println(total);
			triples.add(triple);
			if(!properties.contains(triple[1])){
				count++;
				properties.add(triple[1]);
			}
			
		}
		
		in.close();
		
		ArrayList<String> props=new ArrayList<String>(properties);
		Collections.sort(props);
		ArrayList<String[]> csvRecords=new ArrayList<String[]>();
		HashMap<String, Integer> subjectIndex=new HashMap<String,Integer>();
		for(String[] triple:triples){
			if(!subjectIndex.containsKey(triple[0])){
				String[] row=new String[count+1];
				row[0]=triple[0];
				csvRecords.add(row);
				subjectIndex.put(triple[0],csvRecords.size()-1);
			}
			int propPosition=props.indexOf(triple[1])+1;
			int subjectPosition=subjectIndex.get(triple[0]);
			
			if(csvRecords.get(subjectPosition)[propPosition]==null)
				csvRecords.get(subjectPosition)[propPosition]=triple[2];
			else
				csvRecords.get(subjectPosition)[propPosition]=csvRecords.get(subjectPosition)[propPosition]+","+triple[2];
		}
		
		csvRecords=preprocess(csvRecords, opt);
		
		PrintWriter out=new PrintWriter(new File(csvOutputFile));
		String header="subject,";
		for(String prop:props)
			header+=(prop+",");
		header=header.substring(0,header.length()-1);
		out.println(header);
		for(String[] record:csvRecords)
			out.println(getCSVLine(record));
		out.close();
	}
	
	private ArrayList<String[]> preprocess(ArrayList<String[]> records, int opt){
		ArrayList<String[]> res=null;
		if(opt==2){
			for(String[] record:records)
				for(int i=1; i<record.length; i++)
					if(record[i]==null||record[i].split(",").length<=1)
						continue;
					else
						record[i]="\""+record[i]+"\"";
					
		res=records;
		}
		else if(opt==1){
			for(String[] record:records)
				for(int i=1; i<record.length; i++)
					if(record[i]==null||record[i].split(",").length<=1)
						continue;
					else
					{
						String[] prop=record[i].split(",");
						record[i]=prop[prop.length-1];
					}
		res=records;
		}
		else if(opt==0){
			ArrayList<String[]> toAdd=new ArrayList<String[]>();
			HashSet<Integer> toRemove=new HashSet<Integer>();
			for(int i=0; i<records.size(); i++){
				String[] record=records.get(i);
				int[] multiSets=findMultiSets(record);
				if(multiSets==null)
					continue;
				else{
					ArrayList<String[]> orig=new ArrayList<String[]>();
					orig.add(record);
					orig=expandCombinatorics(orig,multiSets);
					toRemove.add(i);
					for(int p=0; p<orig.size(); p++)
						toAdd.add(orig.get(p));
				}
			
			}
			res=new ArrayList<String[]>();
			for(int i=0; i<toAdd.size(); i++)
				res.add(toAdd.get(i));
			for(int i=0; i<records.size(); i++)
				if(toRemove.contains(i))
					continue;
				else
					res.add(records.get(i));
			
		}
		return res;
				
			
	}
	
	private ArrayList<String[]> expandCombinatorics(ArrayList<String[]> orig, int...multiSets){
		if(multiSets.length==1){
			int index=multiSets[0];
			ArrayList<String[]> res=new ArrayList<String[]>();
			for(int i=0; i<orig.size(); i++){
				String[] record=orig.get(i);
				String[] concat=record[index].split(",");
				for(int j=0; j<concat.length; j++){
					String[] tmp=new String[record.length];
					tmp[0]=record[0];
					for(int k=1; k<record.length; k++)
						if(k!=index)
							tmp[k]=record[k];
						else
							tmp[k]=concat[j];
					res.add(tmp);
					
				}
			}
			return res;
		}
		else{
			int[] newMultiSets=new int[multiSets.length-1];
			int index=multiSets[0];
			for(int i=1; i<multiSets.length; i++)
				newMultiSets[i-1]=multiSets[i];
			ArrayList<String[]> res=new ArrayList<String[]>();
			for(int i=0; i<orig.size(); i++){
				String[] record=orig.get(i);
				String[] concat=record[index].split(",");
				for(int j=0; j<concat.length; j++){
					String[] tmp=new String[record.length];
					tmp[0]=record[0];
					for(int k=1; k<record.length; k++)
						if(k!=index)
							tmp[k]=record[k];
						else
							tmp[k]=concat[j];
					res.add(tmp);
					
				}
			}
			return expandCombinatorics(res,newMultiSets);
		}
	}
	
	private int[] findMultiSets(String[] record){
		ArrayList<Integer> res=new ArrayList<Integer>();
	
		for(int i=1; i<record.length; i++)
			if(record[i]==null||record[i].split(",").length<=1)
				continue;
			else
				res.add(i);
		if(res.size()==0)
			return null;
		else{
			int[] result=new int[res.size()];
			int count=0;
			for(int t:res){
				result[count]=t;
				count++;
			}
			return result;
		}
			
	}
	
	private String getCSVLine(String[] line){
		String output="\"";
		for(int i=0; i<line.length; i++){
			output+=(line[i]+"\",\"");
		}
		output= output.substring(0,output.length()-2);
		return output.replace("\"\"","\"");
	}
	
	//rdfInputFile is actually a 3 column csv file. The returned output will
	//also be a csv file but in property table rep. 
	//header not expected in input file
	//if headeropt is true, header will also be printed
	//if opt is 2 multiset will be collected in single line
	//if opt is 1, then multi-sets will be collapsed to 'last' value of property
	//if opt is 0 then all combinatorial possibilities will be explored
	public MakePropertyTable(String rdfInputFile, String csvOutputFile, int opt, boolean headeropt){
		
		ArrayList<String[]> triples=new ArrayList<String[]>();
		int count=0;
		HashSet<String> properties=new HashSet<String>();
		String total=null;
		try{
			Scanner in=new Scanner(new FileReader(rdfInputFile));
		while(in.hasNextLine()){
			total=in.nextLine();
			CSVParser parse=new CSVParser();
			String[] triple=parse.parseLine(total);
			for(int i=0; i<triple.length; i++)
				triple[i]=triple[i].replace(",",";");
			triples.add(triple);
			if(!properties.contains(triple[1])){
				count++;
				properties.add(triple[1]);
			}
			
		}
		
		in.close();
		} catch(IOException E){
			System.out.println(total);
		}
		ArrayList<String> props=new ArrayList<String>(properties);
		Collections.sort(props);
		ArrayList<String[]> csvRecords=new ArrayList<String[]>();
		HashMap<String, Integer> subjectIndex=new HashMap<String,Integer>();
		for(String[] triple:triples){
			if(!subjectIndex.containsKey(triple[0])){
				String[] row=new String[count+1];
				row[0]=triple[0];
				csvRecords.add(row);
				subjectIndex.put(triple[0],csvRecords.size()-1);
			}
			int propPosition=props.indexOf(triple[1])+1;
			int subjectPosition=subjectIndex.get(triple[0]);
			
			if(csvRecords.get(subjectPosition)[propPosition]==null)
				csvRecords.get(subjectPosition)[propPosition]=triple[2];
			else
				csvRecords.get(subjectPosition)[propPosition]=csvRecords.get(subjectPosition)[propPosition]+","+triple[2];
		}
		
		csvRecords=preprocess(csvRecords, opt);
		try{
		PrintWriter out=new PrintWriter(new File(csvOutputFile));
		String header="subject,";
		for(String prop:props)
			header+=(prop+",");
		header=header.substring(0,header.length()-1);
		header=correct(header);
		if(headeropt)
			out.println(header);
		for(String[] record:csvRecords){
			String l=correct(getCSVLine(record));
			
			out.println(l);
		}
		out.close();}catch(Exception E){
			
		}
	}
	
	//correct recursively for ,",
	private String correct(String line){
		while(line.contains(",\",")){
			line=line.replaceAll(",\",",",\"\",");
		}
		return line;
	}
	

	public static void checkPropertyTable(String propCSV)throws IOException{
		Scanner in=new Scanner(new File(propCSV));
		int size=-1;
		while(in.hasNextLine()){
			String line=in.nextLine();
			//System.out.println(line);
			try{
			String[] tokens=(new CSVParser()).parseLine(line);
			if(size==-1)
				size=tokens.length;
			}
			catch(Exception E){
				System.out.println(line);
				continue;
			}
			
			
		}
		in.close();
	}
	
	public static void main(String[] args)throws IOException{
		/*
		String rdfinput="/host/heteroDatasets/books/dbpediaMod3.csv";
		String outputfile="/host/heteroDatasets/books/dbpediaProp.csv";
		String delimiter=",";
		new MakePropertyTable(rdfinput,delimiter,outputfile,2);*/
		String prefix="/host/heteroDatasets/eswc1/Restaurants/archive/RDF_orig/";
		new MakePropertyTable(prefix+"goldstandard.csv",prefix+"goldstandardprop.csv",2,true);
		checkPropertyTable(prefix+"goldstandardprop.csv");
	}
}
