package exonSkipping;


public class Protein {

	private String id;
	private String geneId;

	//private HashMap<String,CDS> cds;
	private RegionVector regionVectorCds;


	public Protein() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Protein(String id, String geneId,
			RegionVector regionVectorCds) {
		super();
		this.id = id;
		this.geneId = geneId;
		//this.cds = cds;
		this.regionVectorCds = regionVectorCds;
	}

	public int getStart(){

		return this.getRegionVectorCds().getStart();
	}



	public int getStop(){

		return this.getRegionVectorCds().getStop();
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getGeneId() {
		return geneId;
	}


	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}



	public RegionVector getRegionVectorCds() {
		return regionVectorCds;
	}


	public void setRegionVectorCds(RegionVector regionVectorCds) {
		this.regionVectorCds = regionVectorCds;
	}





}
