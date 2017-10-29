package exonSkipping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class parserGTF {


	public static void updateStartStop(String geneId) {
		
		
		
	}

	public static void parse(String filename) {


		//Initialize array ans hashmap
		String tokenizedRow[] = new String[8];
		HashMap<String, String> attributes= new HashMap();
		HashMap<String, Gene> genes = new HashMap();
		HashMap<String, RegionVector> transcripts = new HashMap();
		HashMap<String, RegionVector> exons= new HashMap();
		HashMap<String, RegionVector> cdss= new HashMap();




		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;
			Gene gene= new Gene();
			RegionVector transcript;
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

			        		//transcript= new Transcript(attributes.get("transcript_id"), attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6]);
			        		//transcripts.put(transcript.getId(), transcript);
			        		
			        		//Save the id in the gene list
			        		if(genes.containsKey(attributes.get("gene_id"))){
			        			genes.get(attributes.get("geneId")).getTranscriptIds().add(attributes.get("transcript_id"));
			        		}else {
			        			 ArrayList<String> transcriptIds= new ArrayList();
				        		 Vector<Region> rv1= new Vector();
				        		 Vector<Region> rv2= new Vector();
			        			 Gene newGene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
				        		 genes.put(attributes.get("gene_id"), newGene);
			        		}
			        		
			        		
			        		Region r = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
			        		//Save the real element
			        		if(transcripts.containsKey(attributes.get("transcript_id"))) {
			        			transcripts.get(attributes.get("transcript_id")).getVector().add(r);
			        		}else {
			        			Vector<Region> rv1= new Vector();
			        			rv1.add(r);
				        		transcript= new RegionVector(attributes.get("transcript_id"),attributes.get("gene_id"),rv1);
			        			transcripts.put(attributes.get("transcript_id"), transcript);
			        		}
			        		
			        		
			        		
			        		
			        	

			        }
			        else if(tokenizedRow[2].equals("exon")) {
			        		 //exon = new RegionVector(attributes.get("exon_id"), attributes.get("exon_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]), tokenizedRow[6], attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1]);
			        		 //exons.put(exon.getId(), exon);
			        	 e = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

			        	 if(genes.containsKey(attributes.get("gene_id"))){

			        		 //exons.get(attributes.get("gene_id")).getVector().add(e);
			        		 genes.get(attributes.get("gene_id")).getExon().add(e);
				        	 //Update start and stop of gene
				        	 
				        	 if(genes.get(attributes.get("gene_id")).getStart() > Integer.parseInt(tokenizedRow[3]) ) {
				        		 
				        		 genes.get(attributes.get("gene_id")).setStart(Integer.parseInt(tokenizedRow[3]));
				        	 }
				        	 
				        	 if(genes.get(attributes.get("gene_id")).getStop() < Integer.parseInt(tokenizedRow[4]) ) {
				        		 
				        		 genes.get(attributes.get("gene_id")).setStop(Integer.parseInt(tokenizedRow[4]));
				        	 }
			        		 
			        	 }else{
			        		 //Vector<Region> v = new Vector();
			        		 ArrayList<String> transcriptIds= new ArrayList();
			        		 Vector<Region> rv1= new Vector();
			        		 Vector<Region> rv2= new Vector();
			        		 //RegionVector rv = new RegionVector();
			        		 Gene newGene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
			        		 genes.put(attributes.get("gene_id"), newGene);
			        		 genes.get(attributes.get("gene_id")).getExon().add(e);

			        	 }
			        	 






			        }
			        else if(tokenizedRow[2].equals("CDS")) {
			        	//ERROR
		        		//cds = new RegionVector(attributes.get("protein_id"), attributes.get("gene_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]), tokenizedRow[6], attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1]);
		        		//cdss.put(cds.getId(), cds);
			        	//c = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
			        	//cds.getVector().add(c);

			        	 e = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));


			        	 if(genes.containsKey(attributes.get("gene_id"))){

			        		 //exons.get(attributes.get("gene_id")).getVector().add(e);
			        		 if(!genes.get(attributes.get("gene_id")).getCds().contains(e)) {
			        		 genes.get(attributes.get("gene_id")).getCds().add(e);
			        		 }
				        	 //Update start and stop of gene
				        	 
				        	 if(genes.get(attributes.get("gene_id")).getStart() > Integer.parseInt(tokenizedRow[3]) ) {
				        		 
				        		 genes.get(attributes.get("gene_id")).setStart(Integer.parseInt(tokenizedRow[3]));
				        	 }
				        	 
				        	 if(genes.get(attributes.get("gene_id")).getStop() < Integer.parseInt(tokenizedRow[4]) ) {
				        		 
				        		 genes.get(attributes.get("gene_id")).setStop(Integer.parseInt(tokenizedRow[4]));
				        	 }
			        		 
			        	 }else{
			        		 //Vector<Region> v = new Vector();
			        		 ArrayList<String> transcriptIds= new ArrayList();
			        		 Vector<Region> rv1= new Vector();
			        		 Vector<Region> rv2= new Vector();
			        		 //RegionVector rv = new RegionVector();
			        		 Gene newGene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
			        		 genes.put(attributes.get("gene_id"), newGene);
			        		 genes.get(attributes.get("gene_id")).getCds().add(e);

			        	 }







		        }




				}
			}


			//Fill the list of transcript for the genes.

			for (String key : transcripts.keySet()) {

			    String geneId= transcripts.get(key).getGeneId();
			    Gene.addTranscriptId(key,genes.get(geneId));

			}

//			for (String key : exons.keySet()) {
//
//				String geneId = key;
//				RegionVector rv = exons.get(key);
//				//genes.get(key).getExon().put(geneId, rv);
//				if(genes.get(key) != null) {
//				genes.get(key).setExon(rv.getVector());
//				}
//
//			}

			for (String key : cdss.keySet()) {

				String geneId = key;
				RegionVector rv = cdss.get(key);
				//genes.get(key).getCds().put(geneId, rv);
				if(genes.get(key) != null) {
				genes.get(key).setCds(rv.getVector());
				}else {
					//System.out.println(key);
				}


			}



			Runner.annotation.setGenes(genes);
			Runner.annotation.setTranscripts(transcripts);
			Runner.annotation.setExons(exons);
			Runner.annotation.setCds(cdss);

			System.out.println("Done");

			//Utilities.printGenes(genes);
			//genes.keySet()
			
			//RegionVector.merge(Runner.annotation.getGenes().values()..getCds());
			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}




}

