package exonSkipping;

import java.util.HashMap;
import java.util.List;

public class Transcript {

	private String id;
	private String name;
	private int start;
	private int stop;
	private String geneId;

	private HashMap<String,Exon> exons;
	private RegionVector regionVectorExons;

	private RegionVector regionVectorCds;



	public Transcript() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Transcript(String id, String name, int start, int stop,
			String geneId, HashMap<String, Exon> exons,
			RegionVector regionVectorExons, RegionVector regionVectorCds) {
		super();
		this.id = id;
		this.name = name;
		this.start = start;
		this.stop = stop;
		this.geneId = geneId;
		this.exons = exons;
		this.regionVectorExons = regionVectorExons;
		this.regionVectorCds = regionVectorCds;
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


	public String getGeneId() {
		return geneId;
	}


	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}


	public HashMap<String, Exon> getExons() {
		return exons;
	}


	public void setExons(HashMap<String, Exon> exons) {
		this.exons = exons;
	}


	public RegionVector getRegionVectorExons() {
		return regionVectorExons;
	}


	public void setRegionVectorExons(RegionVector regionVectorExons) {
		this.regionVectorExons = regionVectorExons;
	}


	public RegionVector getRegionVectorCds() {
		return regionVectorCds;
	}


	public void setRegionVectorCds(RegionVector regionVectorCds) {
		this.regionVectorCds = regionVectorCds;
	}





}
