package exonSkipping;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class Gene {

	private String id;
	private String name;
	private String chr;
	private int start;
	private int stop;
	private String biotype;
	private String source;
	private String strand;

	private HashMap<String, Transcript> transcripts;
	private RegionVector regionVectorTranscripts;

	private HashMap<String, Protein> proteins;
	private HashMap<String, RegionVector> cds;


	//private ArrayList<String> transcriptIds;
	//private Vector<Region> cds;
	//private Vector<Region> exon;


	public Gene() {
		super();
	}


	public Gene(String id, String name, String chr, int start, int stop,
			String biotype, String source, String strand,
			HashMap<String, Transcript> transcripts,
			RegionVector regionVectorTranscripts,
			HashMap<String, Protein> proteins) {
		super();
		this.id = id;
		this.name = name;
		this.chr = chr;
		this.start = start;
		this.stop = stop;
		this.biotype = biotype;
		this.source = source;
		this.strand = strand;
		this.transcripts = transcripts;
		this.regionVectorTranscripts = regionVectorTranscripts;
		this.proteins = proteins;
		this.cds = new HashMap<String, RegionVector>();
	}


	public Set<ExonSkipping> calculateExonSkipping(){


		Set<ExonSkipping> result= new HashSet<ExonSkipping>();
		ExonSkipping event;



		// SECOND TRY

		HashMap<String, RegionVector> cds = this.getCds();
		HashMap<Region, HashSet<String>> intron2cds = new HashMap<>();

		//intron2cds contains all the possible SV introns as key!

		for(java.util.Map.Entry<String, RegionVector> e : cds.entrySet()){


			for( Region r : e.getValue().inverse().getVector()){


				Utilities.update(intron2cds, r, e.getKey());

			}

		}


		Set<String> SV = new HashSet<String>();
		Set<String> WT_start = new HashSet<String>();
		Set<String> WT_stop= new HashSet<String>();
		Set<String> WT = new HashSet<String>();


		int es = 0 ;
		for(Region candidate : intron2cds.keySet()){

			event = new ExonSkipping();

			//System.out.println("---------------------");
			WT_start.clear();
			WT_stop.clear();
			WT.clear();
			//Calculate SV -- all CDS containing the candidate
			//System.out.println("-------------------!!-----------------");
			SV = intron2cds.get(candidate);


			for(Region intron : intron2cds.keySet()){
			//Calculate WT_start : CDS having introns beginning at start of candidate
				if(intron.getStart() == candidate.getStart() && intron.getEnd() != candidate.getEnd() ){
					WT_start.addAll(intron2cds.get(intron));


				}
			//Calculate WT_stop : CDS having introns ending at end candidate
				if(intron.getEnd() == candidate.getEnd() && intron.getStart() != candidate.getStart()){
					WT_stop.addAll(intron2cds.get(intron));

				}

			}

			//System.out.println(WT_start.size());
			//calculate WT
			Set<String> intersection = new HashSet<String>(WT_start);
			intersection.retainAll(WT_stop);

			WT = new HashSet<String>(intersection);
			WT.removeAll(SV);

			//System.out.println(WT_start.size());
			//System.out.println(Arrays.asList(WT_stop));

			//System.out.println(WT.size());

			if(WT.size() > 0 ){
				es++;
				//Utilities.printRegion(candidate);
				//intronSV.add(candidate);
				event.setSv(candidate);

				event.setSvCDSids(intron2cds.get(candidate));

				for(String cdsId : WT){
					event.getWtCDSids().add(cdsId);
					//System.out.println(WT_start.size());

				}



				result.add(event);
			}


		}



		//System.out.println(result);

		return result;



	}


	public int getNumberTranscripts(){

		return this.regionVectorTranscripts.getNumberRegion();
		//return this.getRegionVectorTranscripts().getOnCds(this.transcripts.get(key).getRegionVectorCds()).merge().getVector().size();
	}

	public int getNumberCds(){
		//System.out.println(this.getProteins().keySet());

//		RegionVector rv =new RegionVector();
//		for(String key: this.getProteins().keySet()){
//			System.out.println(key+"\t"+ this.getProteins().get(key).getStart()+"-"+ this.getProteins().get(key).getStop());
//			Region m = new Region(this.getProteins().get(key).getStart(), this.getProteins().get(key).getStop());
//
//			rv.getVector().add(m);
//		}
//
//		System.out.println(rv.merge().getVector().size());



		return this.getProteins().keySet().size();


	}
	public void updateStartStop(int start, int stop){

		if(this.getStart() > start ) {

   		 this.setStart(start);
   	 }

   	 if(this.getStop() < stop ) {

   		 this.setStop(stop);
   	 }



	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getChr() {
		return chr;
	}


	public void setChr(String chr) {
		this.chr = chr;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getStop() {
		return stop;
	}


	public void setStop(int stop) {
		this.stop = stop;
	}


	public String getBiotype() {
		return biotype;
	}


	public void setBiotype(String biotype) {
		this.biotype = biotype;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getStrand() {
		return strand;
	}


	public void setStrand(String strand) {
		this.strand = strand;
	}


	public HashMap<String, Transcript> getTranscripts() {
		return transcripts;
	}


	public void setTranscripts(HashMap<String, Transcript> transcripts) {
		this.transcripts = transcripts;
	}


	public RegionVector getRegionVectorTranscripts() {
		return regionVectorTranscripts;
	}


	public void setRegionVectorTranscripts(RegionVector regionVectorTranscripts) {
		this.regionVectorTranscripts = regionVectorTranscripts;
	}


	public HashMap<String, Protein> getProteins() {
		return proteins;
	}


	public void setProteins(HashMap<String, Protein> proteins) {
		this.proteins = proteins;
	}


	public HashMap<String, RegionVector> getCds() {
		return cds;
	}


	public void setCds(HashMap<String, RegionVector> cds) {
		this.cds = cds;
	}





}