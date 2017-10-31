package exonSkipping;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


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


	private int nprots;
	private int ntrans;


	public Gene() {
		super();
	}


	public Gene(String id, String name, String chr, int start, int stop,
			String biotype, String source, String strand) {
		super();
		this.id = id;
		this.name = name;
		this.chr = chr;
		this.start = start;
		this.stop = stop;
		this.biotype = biotype;
		this.source = source;
		this.strand = strand;
		this.transcripts = new HashMap<String, Transcript>();
		this.regionVectorTranscripts = new RegionVector();
	}


	public Gene(String id, String name, String chr, int start, int stop,
			String biotype, String source, String strand,
			HashMap<String, Transcript> transcripts,
			RegionVector regionVectorTranscripts) {
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
	}


	public Set<ExonSkipping> calculateExonSkipping(){


		Set<ExonSkipping> result= new HashSet<ExonSkipping>();
		Set<String> proteinId = new HashSet<String>();
		ExonSkipping event;


		HashMap<String, RegionVector> cds= new HashMap<String, RegionVector>();
		HashMap<Region, HashSet<String>> intron2cds = new HashMap<>();

		//compute the intron2cds hashmap content
		//intron2cds contains all the possible SV introns as key!

		for(String key: this.getTranscripts().keySet())
		{
			for(String keyp : this.getTranscripts().get(key).getProteins().keySet()) {
				cds.put(keyp, this.getTranscripts().get(key).getProteins().get(keyp));

			}


		}
			for(java.util.Map.Entry<String, RegionVector> e : cds.entrySet()){

				for( Region r : e.getValue().inverse().getVector()){


					Utilities.update(intron2cds, r, e.getKey());

				}

			}




		Set<String> SV  = new HashSet<String>();
		Set<String> WT_start = new HashSet<String>();
		Set<String> WT_stop= new HashSet<String>();
		Set<String> WT = new HashSet<String>();


		// For each Splicing Variant candidate (introns) is computed if they actually are splicing variants

		for(Region candidate : intron2cds.keySet()){


			WT_start.clear();
			WT_stop.clear();
			WT.clear();

			event = new ExonSkipping();


			//Calculate SV -- all CDS containing the candidate
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

			//calculate WT
			Set<String> intersection = new HashSet<String>(WT_start);
			intersection.retainAll(WT_stop);

			WT = new HashSet<String>(intersection);
			WT.removeAll(SV);


			// If WT contains elements then we do have an exon skippig variant.
			// Information of this are saved!

			if(WT.size() > 0 ){

				proteinId.addAll(SV);

				// The candidate is an SV--> saved in event
				event.setSv(candidate);
				// saved its corresponding proteins IDs.
				event.setSvCDSids(intron2cds.get(candidate));

				//System.out.println("--------");
				//Utilities.printRegion(candidate);
				//For each different proteinId of WildTypes
				for(String cdsId : WT){

					//The id is saved into the exonSkipping event
					event.getWtCDSids().add(cdsId);

					//System.out.println(cdsId);

					RegionVector rv = new RegionVector();

					//cds.get(id) contains exon informations! Here interested in introns.
					// for each intron of the WT
					for(Region intron : cds.get(cdsId).inverse().getVector()){
						//NOT EFFICIENT !!! TO BE CHANGED


						// if the intron is inbetween the start and end of the SV (not at the same time)
						if(intron.getStart() >= candidate.getStart() && intron.getEnd() <= candidate.getEnd()){

							if(!(intron.getStart() == candidate.getStart() && intron.getEnd() == candidate.getEnd())){
								rv.getVector().add(intron);
							}

						}

					}
					if(rv.getVector().size()>0){
						event.getWt().add(rv);


						//Utilities.printRegionVector(rv);



					}


				}

				proteinId.addAll(WT);
				event.calcMinMax();
				result.add(event);
			}

		}

		nprots = proteinId.size();
		ntrans=calcNTranscripts(proteinId);

		return result;
	}


	public int calcNTranscripts(Set<String> proteinId){
		int number = 0 ;

		for(String key : this.getTranscripts().keySet()){
			for(String id: proteinId){

				if(this.getTranscripts().get(key).getProteins().containsKey(id)){
					 number ++;
				}

			}


		}
		return number;

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





	public int getNprots() {
		return nprots;
	}


	public void setNprots(int nprots) {
		this.nprots = nprots;
	}


	public int getNtrans() {
		return ntrans;
	}


	public void setNtrans(int ntrans) {
		this.ntrans = ntrans;
	}





}