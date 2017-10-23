package exonSkipping;

public class RegionVector {
	
	private String id; 
	private String name; 
	private int start; 
	private int stop;
	private String strand; 
	private String geneId;
	
	private String biotype; 
	private String source; 
	
	public RegionVector(String id, String name, int start, int stop, String strand, String geneId, String biotype,
			String source) {
		super();
		this.id = id;
		this.name = name;
		this.start = start;
		this.stop = stop;
		this.strand = strand;
		this.geneId = geneId;
		this.biotype = biotype;
		this.source = source;
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

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public String getGeneId() {
		return geneId;
	}

	public void setGeneId(String geneId) {
		this.geneId = geneId;
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
	
	
	

}
