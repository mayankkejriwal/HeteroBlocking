package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import featureSelection.HeteroBK;
import general.CSVParser;
import christen.Parameters;

public class GenerateBlocks {
	
	
	
	
	
	public ArrayList<ArrayList<Integer>> codes_DNF;
	public ArrayList<ArrayList<Integer>> attributes1_DNF;
	public ArrayList<ArrayList<Integer>> attributes2_DNF;
	public int num_clauses=-1;
	String tuple;
	boolean first;
	
	

	public GenerateBlocks(ArrayList<String> BK, String line)throws IOException{
		if(line!=null)
			tuple=new String(line.toLowerCase());
		codes_DNF=new ArrayList<ArrayList<Integer>>();
		attributes1_DNF=new ArrayList<ArrayList<Integer>>();
		attributes2_DNF=new ArrayList<ArrayList<Integer>>();
		
		
		
		for(String clause:BK){
			
			String[] t=clause.split(" ");
			if(t.length%3!=0)
				System.out.println("ERROR IN CODE FILE!");
			else
			{
				ArrayList<Integer> cc=new ArrayList<Integer>();
				ArrayList<Integer> aa1=new ArrayList<Integer>();
				ArrayList<Integer> aa2=new ArrayList<Integer>();
				for(int i=0; i<t.length; i+=3){
				cc.add(Integer.parseInt(t[i]));
				aa1.add(Integer.parseInt(t[i+1]));
				aa2.add(Integer.parseInt(t[i+2]));
				}
				codes_DNF.add(cc);
				attributes1_DNF.add(aa1);
				attributes2_DNF.add(aa2);
			}
		  
		}
		num_clauses=codes_DNF.size();
		
	}
	
	public void setLine(String line, boolean first){
		this.first=first;
		tuple=line;
	}
	
	private HashSet<String> blockHelp(int code, int attribute){
	//	if(tuple.equals("education training employment and social services,welfare-to-work tax credit,20.00,10.00,,,,,,60.00,40.00,20.00,10.00,10.00,,,80.00,50.00,20.00,10.00,10.00,,,"))
		//		PrintUtilities.printArrayString(tuple.split(","));
		//System.out.println(tuple.split(",").length);
		HashSet<String> res=new HashSet<String>();
		String[] q=null;
		try {
			q = (new CSVParser()).parseLine(tuple);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(q.length<=attribute)
			return res;
		String attr=q[attribute];
		
		
		if(attr.toLowerCase().trim().equals("null"))
			return res;
			
		if(code==1)
			res.add(attr);
		else if(code==2)
			{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				res.add(tokens[i]);
			}
		else if(code==3)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				if(isInteger(tokens[i]))
					res.add(tokens[i]);
			}
		else if(code==4)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				if(isInteger(tokens[i])){
					Integer q1=new Integer(Integer.parseInt(tokens[i]));
					Integer p=new Integer(Integer.parseInt(tokens[i])-1);
					Integer r=new Integer(Integer.parseInt(tokens[i])+1);
					res.add(q1.toString());
					res.add(p.toString());
					res.add(r.toString());
				}
			}
		else if(code==5)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				if(tokens[i].length()<3)
					res.add(tokens[i]);
				else res.add(tokens[i].substring(0,3));
			}
		else if(code==6)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				if(tokens[i].length()<5)
					res.add(tokens[i]);
				else res.add(tokens[i].substring(0,5));
			}
		else if(code==7)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				if(tokens[i].length()<7)
					res.add(tokens[i]);
				else res.add(tokens[i].substring(0,7));
			}
		else if(code==8){
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length-1; i++)
				res.add(tokens[i]+"_"+tokens[i+1]);
		}
			
		else if(code==9)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length-3; i++)
				res.add(tokens[i]+"_"+tokens[i+1]+"_"+tokens[i+2]+"_"+tokens[i+3]);
		}
		else if(code==10)
		{
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length-5; i++)
				res.add(tokens[i]+"_"+tokens[i+1]+"_"+tokens[i+2]+"_"+tokens[i+3]+"_"+tokens[i+4]+"_"+tokens[i+5]);
		}
		else if(code>10&&code<=18){
			int p=code-11;
			String[] vals={"000","001","010","011","100","101","110","111"};
			String val=vals[p];
			boolean reverse= val.charAt(0)=='0' ? false : true;
			boolean mod= val.charAt(1)=='0' ? false : true;
			boolean four= val.charAt(2)=='0' ? false : true;
			String[] tokens=attr.split(Parameters.splitstring);
			for(int i=0; i<tokens.length; i++)
				res.add(HeteroBK.soundex(tokens[i],reverse, mod, four));
			
		}
		else if(code==19){
			String[] tokens=attr.split(Parameters.splitstring);
			for(String t:tokens)
				if(isAlphaNumeric(t))
					res.add(t);
		}
		else if(code>19&&code<=28){
			int p=code-20;
			String[] tokens=attr.split(Parameters.splitstring);
			String[] vals={"soundex","caverphone1","caverphone2","colognephonetic","doublemetaphone"
					,"matchrating","metaphone","nysiis","refinedsoundex"};
			for(String t:tokens)
				if(HeteroBK.phoneticEncode(t,vals[p])!=null)
					res.add(HeteroBK.phoneticEncode(t,vals[p]));
		}
		
		return res;
		
	}
	
	public HashSet<String> block(int index){
		HashSet<String> res=null;
		for(int i=0; i<codes_DNF.get(index).size(); i++){
			if(first){
				HashSet<String> tmp=blockHelp(codes_DNF.get(index).get(i),attributes1_DNF.get(index).get(i));
				
				if(tmp.size()==0){
					res=null;
					break;
				}
				if(i==0)
					res=tmp;
				else
					res=crossProduct(res, tmp);
			
			}
			else{
				
				HashSet<String> tmp=blockHelp(codes_DNF.get(index).get(i),attributes2_DNF.get(index).get(i));
				
				if(tmp.size()==0){
					res=null;
					break;
				}
				if(i==0)
					res=tmp;
				else
					res=crossProduct(res, tmp);
			
			}
		}
		return res;
			
	}
	
	private HashSet<String> crossProduct(HashSet<String> a, HashSet<String> b){
		HashSet<String> result=new HashSet<String>();
		for(String b1: b)
			if(b1!=null&&!b1.trim().equals(""))
				for(String a1:a)
					if(a1!=null&&!a1.trim().equals(""))
						if(a1.compareTo(b1)<=0)
							result.add(a1+" "+b1);
						else
							result.add(b1+" "+a1);
		return result;
	}
	
	private boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	private  boolean isAlphaNumeric(String token){
		if(!(token.contains("0")||token.contains("1")||token.contains("2")||token.contains("3")||
				token.contains("7")||token.contains("6")||token.contains("5")||token.contains("4")||
						token.contains("8")||token.contains("9")))
			return false;
		if(!isInteger(token))
			return true;
		else
			return false;
	}
	
	
	
}
