package gold;

import java.io.*;
import java.util.*;

public class GenerateGoldBooks {

	public static void main(String[] args)throws IOException {
		String prefix="/host/heteroDatasets/books/";
		HashMap<String, HashSet<Integer>> vIndex=new HashMap<String,HashSet<Integer>>();
		
		
		Scanner in=new Scanner(new FileReader(prefix+"ratings.csv"));
		int i=0;
		while(in.hasNextLine()){
			String isbn=in.nextLine().split("\"")[3];
			if(!vIndex.containsKey(isbn))
				vIndex.put(isbn,new HashSet<Integer>());
			vIndex.get(isbn).add(i);
			i++;
		}
		in.close();
		
		in=new Scanner(new FileReader(prefix+"books.csv"));
		i=0;
		PrintWriter out=new PrintWriter(new File(prefix+"goldStandard_books_ratings"));
		while(in.hasNextLine()){
			String isbn=in.nextLine().split("\"")[1];
			if(!vIndex.containsKey(isbn)){
				i++;
				continue;
			}
			
			ArrayList<Integer> q=new ArrayList<Integer>(vIndex.get(isbn));
			Collections.sort(q);
			for(int q1:q)
				out.println(i+" "+q1);
			i++;
		}
		in.close();
		out.close();
	}

}
