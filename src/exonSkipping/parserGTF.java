package exonSkipping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class parserGTF {



	public static void parse(String filename) {


		//Initialize array ans hashmap
		String tokenizedRow[] = new String[8];
		HashMap<String, String> attributes= new HashMap();
		HashMap<String, Gene> genes = new HashMap();
		HashMap<String, Transcript> transcripts = new HashMap();
		HashMap<String, RegionVector> exons= new HashMap();
		HashMap<String, RegionVector> cdss= new HashMap();




		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;
			Gene gene= new Gene();
			Transcript transcript;
			Region e;
			Region c;
			RegionVector exon = new RegionVector();
			RegionVector cds = new RegionVector();

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

			        			attributes.put(defaultTokenizer.nextToken(), defaultTokenizer.nextToken().replaceAll("[\";]",""));
			        		}

			        }

			        //printing statements for testing
			        //System.out.println(Arrays.asList(tokenizedRow));
			        //System.out.println(Collections.singletonList(attributes));


			        //Create and save the corresponding object
			        if(tokenizedRow[2].equals("gene")) {

			        		 ArrayList<String> transcriptIds= new ArrayList();
			        		 Vector<Region> rv1= new Vector();
			        		 Vector<Region> rv2= new Vector();
			        		 gene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
				        	 genes.put(gene.getId(), gene);


			        }
			        else if(tokenizedRow[2].equals("transcript")) {

			        		transcript= new Transcript(attributes.get("transcript_id"), attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6]);
			        		transcripts.put(transcript.getId(), transcript);


			        }
			        else if(tokenizedRow[2].equals("exon")) {
			        		 //exon = new RegionVector(attributes.get("exon_id"), attributes.get("exon_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]), tokenizedRow[6], attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1]);
			        		 //exons.put(exon.getId(), exon);
			        	 e = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

			        	 if(exons.containsKey(attributes.get("gene_id"))){

			        		 exons.get(attributes.get("gene_id")).getVector().add(e);
			        	 }else{
			        		 Vector<Region> v = new Vector();
			        		 v.add(e);
			        		 exon= new RegionVector(attributes.get("gene_id"), v);
			        		 exons.put(attributes.get("gene_id"), exon);

			        	 }





			        }
			        else if(tokenizedRow[2].equals("CDS")) {
			        	//ERROR
		        		//cds = new RegionVector(attributes.get("protein_id"), attributes.get("gene_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]), tokenizedRow[6], attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1]);
		        		//cdss.put(cds.getId(), cds);
			        	//c = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
			        	//cds.getVector().add(c);

			        	 c = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

			        	 if(cdss.containsKey(attributes.get("gene_id"))){

			        		 cdss.get(attributes.get("gene_id")).getVector().add(c);
			        	 }else{
			        		 Vector<Region> v = new Vector();
			        		 v.add(c);
			        		 cds= new RegionVector(attributes.get("gene_id"), v);
			        		 cdss.put(attributes.get("gene_id"), cds);

			        	 }







		        }




				}
			}


			//Fill the list of transcript for the genes.

			for (String key : transcripts.keySet()) {

			    String geneId= transcripts.get(key).getGeneId();
			    Gene.addTranscriptId(key,genes.get(geneId));

			}

			for (String key : exons.keySet()) {

				String geneId = key;
				RegionVector rv = exons.get(key);
				//genes.get(key).getExon().put(geneId, rv);
				genes.get(key).setExon(rv.getVector());

			}

			for (String key : cdss.keySet()) {

				String geneId = key;
				RegionVector rv = cdss.get(key);
				//genes.get(key).getCds().put(geneId, rv);
				genes.get(key).setCds(rv.getVector());


			}


			Runner.annotation.setGenes(genes);
			Runner.annotation.setTranscripts(transcripts);
			Runner.annotation.setExons(exons);
			Runner.annotation.setCds(cdss);

			System.out.println("Done");

			//Utilities.printGenes(genes);


		} catch (IOException e) {
			e.printStackTrace();
		}

	}




}

