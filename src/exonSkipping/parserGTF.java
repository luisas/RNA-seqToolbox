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





		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;


			Gene gene= new Gene();
			Transcript transcript=new Transcript();
			Exon exon = new Exon();
			Protein protein = new Protein();
			Region e;
			Region c;



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

			        //System.out.println(Arrays.asList(tokenizedRow));
			        //System.out.println(Collections.singletonList(attributes));


			        //Create and save the corresponding object
			        if(tokenizedRow[2].equals("gene")) {

			        	HashMap<String, Transcript> transcriptsHM= new HashMap<String, Transcript>();
    					RegionVector regionVectorTranscripts=  new RegionVector();
    					HashMap<String, Protein> proteins = new HashMap<String, Protein>();

			        	gene= new Gene(attributes.get("gene_id"), attributes.get("gene_name"), tokenizedRow[0],
			        				 		Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
			        				 		attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6],
			        				 		transcriptsHM, regionVectorTranscripts, proteins);

			        	//gene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
				        genes.put(gene.getId(), gene);


			        }
		//----------------TRANSCRIPT
			        else if(tokenizedRow[2].equals("transcript")) {

			        		//transcript= new Transcript(attributes.get("transcript_id"), attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6]);
			        		//transcripts.put(transcript.getId(), transcript);

			        	 HashMap<String, Exon> exonsHM = new HashMap<String,Exon>();
	        			 RegionVector regionVectorExons = new RegionVector();
	        			 RegionVector regionVectorCds = new RegionVector();




			        	transcript = new Transcript(attributes.get("transcript_id"),attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
			        				attributes.get("gene_id"), exonsHM, regionVectorExons,regionVectorCds);

			        	Region r = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));


			        	//Save the id in the gene list
			        	if(!genes.containsKey(attributes.get("gene_id"))){

			        		HashMap<String, Transcript> transcriptsHM= new HashMap<String, Transcript>();
		    				RegionVector regionVectorTranscripts=  new RegionVector();
		    				HashMap<String, Protein> proteins = new HashMap<String, Protein>();

					        	gene= new Gene(attributes.get("gene_id"), attributes.get("gene_name"), tokenizedRow[0],
					        				 		Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
					        				 		attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6],
					        				 		transcriptsHM, regionVectorTranscripts, proteins);

					        	//gene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
						        genes.put(gene.getId(), gene);

			        	}

			        	genes.get(attributes.get("gene_id")).getTranscripts().put(attributes.get("transcript_id"), transcript);
		        		genes.get(attributes.get("gene_id")).getRegionVectorTranscripts().getVector().add(r);








			        }
			  //----------------EXON
			        else if(tokenizedRow[2].equals("exon")) {
			        		 //exon = new RegionVector(attributes.get("exon_id"), attributes.get("exon_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]), tokenizedRow[6], attributes.get("gene_id"), attributes.get("gene_biotype"), tokenizedRow[1]);
			        		 //exons.put(exon.getId(), exon);


			        	exon= new Exon(Integer.parseInt(attributes.get("exon_number")), Integer.parseInt(tokenizedRow[3]),Integer.parseInt(tokenizedRow[4]), attributes.get("transcript_id"));
			        	e = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

			        	 if(!genes.containsKey(attributes.get("gene_id"))){

			        		 HashMap<String, Transcript> transcriptsHM= new HashMap<String, Transcript>();
			    				RegionVector regionVectorTranscripts=  new RegionVector();
			    				HashMap<String, Protein> proteins = new HashMap<String, Protein>();

						        	gene= new Gene(attributes.get("gene_id"), attributes.get("gene_name"), tokenizedRow[0],
						        				 		Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
						        				 		attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6],
						        				 		transcriptsHM, regionVectorTranscripts, proteins);

						        	//gene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
							        genes.put(gene.getId(), gene);


			        	 }


			        	if(!genes.get(attributes.get("gene_id")).getTranscripts().containsKey(attributes.get("transcript_id"))){

				        	 HashMap<String, Exon> exonsHM = new HashMap<String,Exon>();
		        			 RegionVector regionVectorExons = new RegionVector();
		        			 RegionVector regionVectorCds = new RegionVector();



				        	transcript = new Transcript(attributes.get("transcript_id"),attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
				        				attributes.get("gene_id"), exonsHM, regionVectorExons, regionVectorCds);

				        	Region r = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
				        	genes.get(attributes.get("gene_id")).getTranscripts().put(attributes.get("transcript_id"), transcript);
			        		genes.get(attributes.get("gene_id")).getRegionVectorTranscripts().getVector().add(r);

			        	}

			        	else{

			        	gene.updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
			        	transcript.updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

			        	}


			        	genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getExons().put(attributes.get("exon_number"), exon);
		        		genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getRegionVectorExons().getVector().add(e);

		  //--------------------------CDS

			        }
			        else if(tokenizedRow[2].equals("CDS")) {


			        	protein = new Protein(attributes.get("protein_id"), attributes.get("gene_id"), new RegionVector());
			        	c= new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));


			        	if(!genes.containsKey(attributes.get("gene_id"))){

			        		 HashMap<String, Transcript> transcriptsHM= new HashMap<String, Transcript>();
			    				RegionVector regionVectorTranscripts=  new RegionVector();
			    				HashMap<String, Protein> proteins = new HashMap<String, Protein>();

						        	gene= new Gene(attributes.get("gene_id"), attributes.get("gene_name"), tokenizedRow[0],
						        				 		Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
						        				 		attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6],
						        				 		transcriptsHM, regionVectorTranscripts, proteins);

						        	//gene = new Gene(attributes.get("gene_id"), attributes.get("gene_name"),tokenizedRow[0], Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),  attributes.get("gene_biotype"), tokenizedRow[1], tokenizedRow[6], transcriptIds, rv1, rv2);
							        genes.put(gene.getId(), gene);


			        	 }
			        	if(!genes.get(attributes.get("gene_id")).getTranscripts().containsKey(attributes.get("transcript_id"))){

				        	 HashMap<String, Exon> exonsHM = new HashMap<String,Exon>();
		        			 RegionVector regionVectorExons = new RegionVector();
		        			 RegionVector regionVectorCds = new RegionVector();



				        	transcript = new Transcript(attributes.get("transcript_id"),attributes.get("transcript_name"), Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]),
				        				attributes.get("gene_id"), exonsHM, regionVectorExons, regionVectorCds);

				        	Region r = new Region(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
				        	genes.get(attributes.get("gene_id")).getTranscripts().put(attributes.get("transcript_id"), transcript);
			        		genes.get(attributes.get("gene_id")).getRegionVectorTranscripts().getVector().add(r);

			        	}
			        	else{

				        	gene.updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));
				        	transcript.updateStartStop(Integer.parseInt(tokenizedRow[3]), Integer.parseInt(tokenizedRow[4]));

				        }


			        	genes.get(attributes.get("gene_id")).getProteins().put(attributes.get("protein_id"), protein);
		        		genes.get(attributes.get("gene_id")).getProteins().get(attributes.get("protein_id")).getRegionVectorCds().getVector().add(c);
		        		genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getRegionVectorCds().getVector().add(c);


		        		//add to exons
		        		genes.get(attributes.get("gene_id")).getTranscripts().get(attributes.get("transcript_id")).getExons().get(attributes.get("exon_number")).getCDS().getVector().add(c);

		        		if (genes.get(attributes.get("gene_id")).getCds().containsKey(attributes.get("protein_id"))){
		        			
		        			
		        			genes.get(attributes.get("gene_id")).getCds().get(attributes.get("protein_id")).getVector().add(c); 
		        			
		        		}else{
		        			RegionVector rv= new RegionVector(); 
		        			rv.getVector().add(c); 
		        			genes.get(attributes.get("gene_id")).getCds().put(attributes.get("protein_id"), rv);
		        		}
		        		





		        }




				}
			}



			for(String key: genes.keySet()){

				genes.get(key).setRegionVectorTranscripts(genes.get(key).getRegionVectorTranscripts().merge());

			}


			Runner.annotation.setGenes(genes);

			System.out.println("Done");

			Utilities.printGenes(genes);
			//genes.keySet();
			//RegionVector.merge(Runner.annotation.getGenes().values()..getCds());


		} catch (IOException e) {
			e.printStackTrace();
		}

	}




}

