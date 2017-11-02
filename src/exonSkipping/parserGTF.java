package exonSkipping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;


public class parserGTF {

	static HashMap<String, Gene> genes = new HashMap<String, Gene>();


	public static void parse(String filename) {


		//Initialize array ans hashmap
		String tokenizedRow[] = new String[8];
		HashMap<String, String> attributes= new HashMap<String, String>();





		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;



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

			        			String key = defaultTokenizer.nextToken();
			        			if(defaultTokenizer.hasMoreTokens()){
			        				attributes.put(key, defaultTokenizer.nextToken().replaceAll("[\";]",""));
			        			}
			        		}

			        }

			        //System.out.println(Arrays.asList(tokenizedRow));
			        //System.out.println(Collections.singletonList(attributes));

	 //-----------------------GENE
			        if(tokenizedRow[2].equals("gene")) {

			        			addGene(tokenizedRow, attributes);


			        }
		//----------------TRANSCRIPT
			        else if(tokenizedRow[2].equals("transcript")) {


			        	//Save the id in the gene list
			        	if(!genes.containsKey(attributes.get("gene_id"))){

			        		addGene(tokenizedRow, attributes);

			        	}


			        	addTranscript(tokenizedRow, attributes);

			        }
			  //----------------EXON
			        else if(tokenizedRow[2].equals("exon")) {




			        	 if(!genes.containsKey(attributes.get("gene_id"))){
			        		 addGene(tokenizedRow, attributes);
			        	 }

			        	if(!genes.get(attributes.get("gene_id")).getTranscripts().containsKey(attributes.get("transcript_id"))){
			        		addTranscript(tokenizedRow, attributes);
			        	}

			        	addExon(tokenizedRow, attributes);


			        	}





		  //--------------------------CDS


			        else if(tokenizedRow[2].equals("CDS")) {



			        	 if(!genes.containsKey(attributes.get("gene_id"))){
			        		 addGene(tokenizedRow, attributes);
			        	 }

			        	if(!genes.get(attributes.get("gene_id")).getTranscripts().containsKey(attributes.get("transcript_id"))){
			        		addTranscript(tokenizedRow, attributes);
			        	}

			        		addCds(tokenizedRow, attributes);

		        }




				}
			}

			Runner.annotation.setGenes(genes);





		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void addGene(String tokenizedRow[],HashMap<String, String> attributes ) {

		Gene gene= new Gene(attributes.get("gene_id"), attributes.get("gene_name"), tokenizedRow[0],
	 		Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
	 		attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6]);

		genes.put(gene.getId(), gene);


}

public static void addTranscript(String tokenizedRow[],HashMap<String, String> attributes) {
		Transcript transcript = new Transcript(attributes.get("transcript_id"),attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
			attributes.get("gene_id"));

		Region r = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

     genes.get(attributes.get("gene_id")).getTranscripts().put(attributes.get("transcript_id"), transcript);
		genes.get(attributes.get("gene_id")).getRegionVectorTranscripts().getVector().add(r);
		genes.get(attributes.get("gene_id")).updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));


}

public static void addExon(String tokenizedRow[],HashMap<String, String> attributes) {

	Exon exon= new Exon(Integer.parseInt(attributes.get("exon_number")), Integer.parseInt(tokenizedRow[3]),Integer.parseInt(tokenizedRow[4]));
	Region	e = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
	genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getExons().put(attributes.get("exon_number"), exon);
genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getRegionVectorExons().getVector().add(e);
genes.get(attributes.get("gene_id")).updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));



}

public static void addCds(String tokenizedRow[],HashMap<String, String> attributes) {

	Region c= new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

	if(!genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getProteins().containsKey(attributes.get("protein_id"))) {

		RegionVector rv = new RegionVector();
		rv.getVector().add(c);
		genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getProteins().put(attributes.get("protein_id"),rv);

	}else {

		genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getProteins().get(attributes.get("protein_id")).getVector().add(c);
	}


	genes.get(attributes.get("gene_id")).updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
	genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
}




}

