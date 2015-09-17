package general;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PrintUtilities {

	
	public static void printArrayString(String[] tokens) {
		for(int i=0; i<tokens.length; i++)
			System.out.print(tokens[i]+" ; ");
		System.out.println();

	}
	
	public static void printArrayInt(int[] array){
		for(int i=0; i<array.length; i++)
			System.out.print(array[i]+" ");
		System.out.println();
	}
	
	public static String convertToString(ArrayList<Integer> feature){

	    String res="";
	    for(int i=0; i<feature.size(); i++)
	    	res+=(feature.get(i)+" ");
	    return res.substring(0,res.length()-1);

	   }
	
	public static void printDoubleMatrix(double[][] matrix){
		for(int i=0; i<matrix.length; i++)
			printDouble(matrix[i]);
	}
	
	public static void printDouble(double[] d){
		System.out.println();
		//DecimalFormat p=new DecimalFormat("0.00");
		DecimalFormat q=new DecimalFormat("0.000000");
		for(int i=0; i<d.length; i++)
			System.out.print(q.format(d[i])+" ");
	}

}
