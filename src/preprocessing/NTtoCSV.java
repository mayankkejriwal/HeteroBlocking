package preprocessing;
import java.util.*;
import java.io.*;

public class NTtoCSV {

	public static void convert(String nt, String csv)throws IOException{
		Scanner in=new Scanner(new File(nt));
		PrintWriter out=new PrintWriter(new File(csv));
		while(in.hasNextLine()){
			String line=in.nextLine();
			line=line.replace("\\\"","");
			line=line.substring(0,line.length()-2);
			String j="> ";
			String[] l=line.split(j);
			String p=new String("");
			for(int i=0; i<=2; i++){
				l[i]=l[i].replace("<","");
				l[i]=l[i].replace(">","");
				p+=l[i]+",";
				
			}
			out.println(p.substring(0,p.length()-1));
		}
		in.close();
		out.close();
	}
	
	public static void main(String[] args)throws IOException{
		String prefix="/host/heteroDatasets/icde_experiments/Persons/";
				convert(prefix+"person12.nt",prefix+"person12.csv");
		
		
		
	}

}
