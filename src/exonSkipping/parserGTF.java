package exonSkipping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class parserGTF {
	
	
	public static void parse(String filename) {
		
		//Initialize array ans hashmap
		String tokenizedRow[] = new String[8];
		HashMap<String, String> attributes= new HashMap();
		HashMap<String, Gene> genes = new HashMap();
		HashMap<String, Transcript> transcripts = new HashMap();
		HashMap<String, Exon> exons = new HashMap();
		
		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;
			Gene gene;
			Transcript transcript;
			Exon exon;

		//Each line is tokenized 
			while ((currentLine = br.readLine()) != null ) {
				
				//Exclude header
				if(!currentLine.startsWith("#")) {
					
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
			      
					//Save first 8 features in array "TokenizedRow" and the last attribute in hashmap "attributes"
					int i = 0 ; 	
			        while (defaultTokenizer.hasMoreTokens())
			        {
			        		if(i<8) {
				        		tokenizedRow[i]=defaultTokenizer.nextToken();
				        		i++;
			        		}
			        		else {
			        			
			        			attributes.put(defaultTokenizer.nextToken(), defaultTokenizer.nextToken());
			        		}			        		

			        }
			        
			        //printing statements for testing
			        //System.out.println(Arrays.asList(tokenizedRow));
			        //System.out.println(Collections.singletonList(attributes)); 
			        
			        
			        //Create and save the corresponding object
			        if(tokenizedRow[2].equals("gene")) {
				       
				        	 gene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6]);
				        	 genes.put(gene.getId(), gene);
				        	 
			        }
			        else if(tokenizedRow[2].equals("transcript")) {
			        	
			        		transcript= new Transcript(attributes.get("transcript_id"), attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6]);
			        		transcripts.put(transcript.getId(), transcript);
			        	
			        }
			        else if(tokenizedRow[2].equals("transcript")) {
			        		//exon=new Exon();
			        		//exons.put(exon.getId(), exon);
			        	
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

