package exonSkipping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class parserGTF {
	
	
	public static void parse(String filename) {
		
		//Initialize array 
		String tokenizedRow[] = new String[30];
		
		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;

		//Each line is tokenized 
			while ((currentLine = br.readLine()) != null ) {
				
				//Exclude header
				if(!currentLine.startsWith("#")) {
					
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
			      
					//first 8 (0-7)
					int i = 0 ; 	
			        while (defaultTokenizer.hasMoreTokens())
			        {
			        		tokenizedRow[i]=defaultTokenizer.nextToken();
			        		System.out.println(tokenizedRow[i]);
			        		
			        		i++;

			        }
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	public static void main(String[] args) {
		
		//TEST
		//String myFileName = "/Users/luisasantus/Desktop/GoBi/data/Drosophila_melanogaster.BDGP5.77.gtf";
		String myFileName = "/Users/luisasantus/Desktop/GoBi/data/small.gtf";

		parse(myFileName);
		
	}
	
	

}

