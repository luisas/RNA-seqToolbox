package exonSkipping;

import java.util.ArrayList;
import java.util.HashMap;
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
	}


	public HashMap<Region,Vector<Region>> calculateExonSkipping(){

		HashMap<Region,Vector<Region>> exonskipping = new HashMap();



		//filter for CDS region only
		for(String key : this.transcripts.keySet()){

			this.transcripts.get(key).getRegionVectorExons().getOnCds(this.transcripts.get(key).getRegionVectorCds()).merge().getVector().size();

		}

		//RegionVector exonsCDS = this.


		// find intron

		// see if there is WT





		return exonskipping;



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





}