package gold;

import java.io.*;
import java.util.*;

public class ConvertIBM {

	//we also use for non-IBM (e.g. books) conversions
	public static void main(String[] args)throws IOException {
		String prefix="/host/heteroDatasets/books/";
		Scanner in=new Scanner(new FileReader(prefix+"books_header.csv"));
		PrintWriter out=new PrintWriter(new File(prefix+"books.csv"));
		while(in.hasNextLine()){
			String line=in.nextLine();
			line=line.replace("\";\"","\",\"");
			out.println(line);
		}
		out.close();
		in.close();

	}

}
