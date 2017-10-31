package exonSkipping;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Utilities {


	public  static StringBuilder prettySetRegionVector(Set<RegionVector> set){

		RegionVector rv1 = new RegionVector();




		for(RegionVector rv : set){
			for(Region r: rv.getVector() ){
				if(!rv1.getVector().contains(r)){
					rv1.getVector().add(r);
				}


			}

		}

		Collections.sort(rv1.getVector(), RegionVector.comparator);


		return prettyRegionVector(rv1);

	}

	public static StringBuilder prettyRegionVector(RegionVector rv){

		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(Region r: rv.getVector() ){
			sb.append(prefix);
			sb.append(r.getStart()+":"+r.getEnd());
			prefix = "|";
		}

		return sb;
	}


	public static StringBuilder printID(Set<String> set){
		StringBuilder svProts = new StringBuilder();
		String prefix = "";
		for(String cdsId : set ){
			svProts.append(prefix);
			svProts.append(cdsId);
			prefix = "|";
		}

		return svProts;
	}


	public static void printGene(Gene gene) {

		System.out.print("GENE:    ");
		System.out.println(gene.getId() +"\t"+ gene.getName() +"\t"+gene.getStart()+ "\t"+gene.getStop());
		System.out.println( "\t" +"\t List of Transcripts");
		//System.out.println(Collections.singletonList(gene.getTranscriptIds()));
		printTranscripts(gene);
		//printRegionVector(gene.getRegionVectorTranscripts());

		//System.out.print( "\t" +"\t List of CDS");
		//printVector(gene.getCds());


	}


	public static void printTranscripts(Gene gene){
		for(String key: gene.getTranscripts().keySet()){
			printTranscript(gene.getTranscripts().get(key));

		}


	}
	public static void printTranscript(Transcript transcript){
		System.out.println("\t" +"\t"+transcript.getId()+"\t"+transcript.getStart()+ "\t"+transcript.getStop());
		//Utilities.printRegionVector(transcript.getRegionVectorCds());


	}
	public static void printGenes(HashMap<String, Gene> genes) {

		for (String key : genes.keySet()) {
			printGene(genes.get(key));
		}
	}


	public static void printVector(Vector<Region> vector){

		for(Region r: vector ){

			System.out.println(r.getStart()+":"+r.getEnd()+"----"+r.getLength());

		}

	}

	public static void printRegionVector(RegionVector rv) {


		for(Region r : rv.getVector()) {


			System.out.println("\t" +"\t"+r.getStart()+":"+r.getEnd()+"----"+r.getLength());

		}

	}


	public static void printCds(HashMap<String, RegionVector> map) {

		for(String key : map.keySet()) {
			printVector(map.get(key).getVector());

			System.out.println("hola");

		}


	}

	public static void update(HashMap<Region, HashSet<String>> hm ,Region r, String otherId ){


		if(hm.containsKey(r)){

			hm.get(r).add(otherId);
		}
		else{

			HashSet<String> hs = new HashSet<>();
			hs.add(otherId);
			hm.put(r, hs);
		}

	}

	public static void printRegion(Region r ){

		System.out.println(r.getStart() + "-" + r.getEnd());
	}



  public static boolean mySetContains(Set<RegionVector> set, Region r){

	  for(RegionVector rv: set){

		  if(rv.getVector().contains(r)){
			  return true;
		  }

	  }
	  return false;

  }



}
