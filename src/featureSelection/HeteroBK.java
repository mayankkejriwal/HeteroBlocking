package featureSelection;

import java.util.HashSet;

import christen.Parameters;

import org.apache.commons.codec.language.*;

public class HeteroBK {
	
	
	
	public static int[] ExactMatch(String[] arr1, String[] arr2){
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			
			for(int j=0; j<arr2.length; j++){
				
				if(arr1[i].equals(arr2[j]))
					weight[i*arr2.length+j]=1;
				else
					weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	private static boolean containscommon(String[] tokens1, String[] tokens2){
		
		for(int i=0; i<tokens1.length; i++){
			for(int j=0; j<tokens2.length; j++)
				if(tokens1[i].equals(tokens2[j]))
					return true;
		}
		return false;
			
	}
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	private static boolean containscommonInteger(String[] tokens1, String[] tokens2){
		
		for(int i=0; i<tokens1.length; i++){
			if(!isInteger(tokens1[i]))
				continue;
			for(int j=0; j<tokens2.length; j++)
				if(isInteger(tokens2[j])&&(int)Integer.parseInt(tokens1[i])==(int)Integer.parseInt(tokens2[j]))
					{
					return true;
					}
		}
		return false;
			
	}
	
	private static boolean checkCondition(int a, int b){
		if(a==b||a-1==b||a+1==b)
			return true;
		
		return false;
	}
	private static boolean containsSameOffByOneInteger(String[] tokens1, String[] tokens2){
		
		for(int i=0; i<tokens1.length; i++){
			if(!isInteger(tokens1[i]))
				continue;
			for(int j=0; j<tokens2.length; j++)
				if(isInteger(tokens2[j])&&checkCondition(Integer.parseInt(tokens1[i]),Integer.parseInt(tokens2[j])))
					return true;
		}
		return false;
			
	}
	
	private static boolean containscommonAlphaNumeric(String[] tokens1, String[] tokens2){
		
		for(int i=0; i<tokens1.length; i++){
			if(!isAlphaNumeric(tokens1[i]))
				continue;
			for(int j=0; j<tokens2.length; j++)
				if(isAlphaNumeric(tokens2[j])&&tokens1[i].toLowerCase().equals(tokens2[j].toLowerCase()))
					{
					return true;
					}
		}
		return false;
			
	}
	
	private static boolean isAlphaNumeric(String token){
		if(!(token.contains("0")||token.contains("1")||token.contains("2")||token.contains("3")||
				token.contains("7")||token.contains("6")||token.contains("5")||token.contains("4")||
						token.contains("8")||token.contains("9")))
			return false;
		if(!isInteger(token))
			return true;
		else
			return false;
	}
	
	public static int[] CommonAlphaNumeric(String[] arr1, String[] arr2){
		
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
				if(containscommonAlphaNumeric(tokens1,tokens2))
					weight[i*arr2.length+j]=1;
				else
					weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	
	public static int[] CommonToken(String[] arr1, String[] arr2){
		
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
				if(containscommon(tokens1,tokens2))
					weight[i*arr2.length+j]=1;
				else
					weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	
	public static int[] CommonInteger(String[] arr1, String[] arr2){
		
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
				if(containscommonInteger(tokens1,tokens2))
					weight[i*arr2.length+j]=1;
				else
					weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	
	public static int[] CommonOrOffByOneInteger(String[] arr1, String[] arr2){
		
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
			if(containsSameOffByOneInteger(tokens1,tokens2))
				weight[i*arr2.length+j]=1;
			else
				weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	
	private static boolean containsCommonNFirst(String[] tokens1, String[] tokens2, int n){
		for(int i=0; i<tokens1.length; i++){
			String temp1=null;
			if(n>tokens1[i].length())
				temp1=tokens1[i];
			else
				temp1=tokens1[i].substring(0,n);
			for(int j=0; j<tokens2.length; j++){
				if(n>tokens2[j].length())
				{	if(temp1.equals(tokens2[j]))
						return true;
				}
				else
					if(temp1.equals(tokens2[j].substring(0,n)))
						return true;
			}
		}
		return false;
			
	}
	
	public static int[] CommonNFirst(String[] arr1, String[] arr2, int n){
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
			if(containsCommonNFirst(tokens1,tokens2, n))
				weight[i*arr2.length+j]=1;
			else
				weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	private static boolean ArraysEqual(String[] tokens1, String[] tokens2, int i, int j, int n){
		int count=0;
		for(int k1=i; k1<tokens1.length; k1++){
			if(j>=tokens2.length)
				break;
			if(tokens1[k1].equals(tokens2[j])){
					count++;
					j++;}
			else {
					if(count>=n)
						return true;
					else
						return false;
				}
		}
		if(count>=n)
			return true;
		else
			return false;
	}
	private static boolean containsCommonTokenNGram(String[] tokens1, String[] tokens2, int n){
		if(n<1)
			return false;
		for(int i=0; i<=tokens1.length-n; i++){
			for(int j=0; j<=tokens2.length-n; j++){
				if(tokens1[i].equals(tokens2[j]))
					if(ArraysEqual(tokens1,tokens2,i,j,n))
						return true;
			}
		}
		return false;
			
	}
	
	public static int[] CommonTokenNGram(String[] arr1, String[] arr2, int n){
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
			if(containsCommonTokenNGram(tokens1,tokens2, n))
				weight[i*arr2.length+j]=1;
			else
				weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	
	
	
	private static String rev(String orig){
		String res="";
		for(int i=orig.length()-1; i>=0; i--)
			res+=orig.charAt(i);
		return res;
	}
	
	//string length assumed to be less than 4
	private static String padWithZeros(String orig){
		String res=new String(orig);
		for(int i=orig.length(); i<=3; i++)
			res+="0";
		return res;
	}
	
	//return soundex encoding of orig. orig can be anything, so we're taking liberty with soundex def.
	public static String soundex(String orig, boolean reverse, boolean mod, boolean four){
		String alphabet="abcdefghijklmnopqrstuvwxyz";
		String translation_nonmod="01230120022455012623010202";
		String translation_mod="01360240043788015936020505";
		String translation= mod ? translation_mod : translation_nonmod;
		if(orig.length()==0)
			return "";
		
		String src= reverse ?  rev(orig): (new String(orig))  ;
		HashSet<Character> charset=new HashSet<Character>();
		String res=""+src.charAt(0);
		for(int i=1; i<src.length(); i++){
			int index=alphabet.indexOf(src.charAt(i));
			if(index==-1)
				continue;
			char conv=translation.charAt(index);
			if(conv=='0')
				continue;
			if(charset.contains(conv))
					continue;
			else{
				charset.add(conv);
				res+=conv;
				
			}
		}
		
		
		
		if(res.length()==4||(!four))
			return res;
		
		if(res.length()>4)
			return res.substring(0,4);
		else
			return padWithZeros(res);
		
	}
	
	private static boolean containsCommonSoundex(String[] tokens1, String[] tokens2, boolean reverse, boolean mod, boolean four){
		for(int i=0; i<tokens1.length; i++)
			for(int j=0; j<tokens2.length; j++){
				if(soundex(tokens1[i],reverse,mod,four).equals(soundex(tokens2[j], reverse, mod, four))&&!soundex(tokens2[j], reverse, mod, four).equals(""))
					return true;
			}
		return false;
	}
	
	public static int[] soundex(String[] arr1, String[] arr2, boolean reverse, boolean mod, boolean four){
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
			if(containsCommonSoundex(tokens1,tokens2, reverse, mod, four))
				weight[i*arr2.length+j]=1;
			else
				weight[i*arr2.length+j]=0;
			}
		}
		return weight;
	}
	
	private static boolean containsCommonPhonetic(String[] tokens1, String[] tokens2, String type){
		for(String token1:tokens1)
			for(String token2:tokens2){
				if(token2.equals(token1))
					return true;
				try{
					if(type.equals("soundex")){
						if(new Soundex().difference(token1,token2)>=3)
							return true;
					}
					else if(type.equals("caverphone1")){
						Caverphone1 d=new Caverphone1();
						String t1=d.encode(token1);
						String t2=d.encode(token2);
						if(t1.equals(t2))
							return true;
					}
					else if(type.equals("caverphone2")){
						Caverphone2 d=new Caverphone2();
						String t1=d.encode(token1);
						String t2=d.encode(token2);
						if(t1.equals(t2))
							return true;
					}
					else if(type.equals("colognephonetic")){
						ColognePhonetic d=new ColognePhonetic();
						if(d.isEncodeEqual(token1,token2))
							return true;
					}
					else if(type.equals("doublemetaphone")){
						DoubleMetaphone d=new DoubleMetaphone();
						String t1=d.encode(token1);
						String t2=d.encode(token2);
						if(t1.equals(t2))
							return true;
					}
					else if(type.equals("matchrating")){
						MatchRatingApproachEncoder d=new MatchRatingApproachEncoder();
						if(d.isEncodeEquals(token1,token2))
							return true;
							
					}
					else if(type.equals("metaphone")){
						Metaphone d=new Metaphone();
						if(d.isMetaphoneEqual(token1,token2))
							return true;
					}
					else if(type.equals("nysiis")){
						Nysiis d=new Nysiis();
						if(d.nysiis(token1).equals(d.nysiis(token2)))
							return true;
					}
					else if(type.equals("refinedsoundex")){
						RefinedSoundex d=new RefinedSoundex();
						if(d.difference(token1,token2)>=3)
							return true;
					}
				}
				catch(Exception e){
					continue;
				}
			}
		return false;
	}
	
	public static int[] phonetic(String[] arr1, String[] arr2, String type){
		int[] weight=new int[arr1.length*arr2.length];
		for(int i=0; i<arr1.length; i++){
			String[] tokens1=arr1[i].split(Parameters.splitstring);
			for(int j=0; j<arr2.length; j++){
				String[] tokens2=arr2[j].split(Parameters.splitstring);
				
					if(containsCommonPhonetic(tokens1,tokens2, type))
						weight[i*arr2.length+j]=1;
					else
						weight[i*arr2.length+j]=0;
				
			}
		}
		return weight;
	}
	
	public static String phoneticEncode(String token, String type){
		try{
			if(type.equals("soundex")){
				return new Soundex().encode(token);
			}
			else if(type.equals("caverphone1")){
				Caverphone1 d=new Caverphone1();
				return d.encode(token);
			}
			else if(type.equals("caverphone2")){
				Caverphone2 d=new Caverphone2();
				return d.encode(token);
			}
			else if(type.equals("colognephonetic")){
				ColognePhonetic d=new ColognePhonetic();
				return d.encode(token);
			}
			else if(type.equals("doublemetaphone")){
				DoubleMetaphone d=new DoubleMetaphone();
				return d.encode(token);
			}
			else if(type.equals("matchrating")){
				MatchRatingApproachEncoder d=new MatchRatingApproachEncoder();
				return d.encode(token);
					
			}
			else if(type.equals("metaphone")){
				Metaphone d=new Metaphone();
				return d.encode(token);
			}
			else if(type.equals("nysiis")){
				Nysiis d=new Nysiis();
				return d.encode(token);
			}
			else if(type.equals("refinedsoundex")){
				RefinedSoundex d=new RefinedSoundex();
				return d.encode(token);
			}
		}
		catch(Exception e){
			return null;
		}
		return null;
	}
	
	

}
