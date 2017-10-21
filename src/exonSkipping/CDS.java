package exonSkipping;

public class CDS {
	
	
	private String id; 
	private String name; 
	private int start; 
	private int stop; 
	private String geneId; 
	private String source; 
	private String strand; 
	private String transcriptId;
	private String proteinId; 
	private int exonNumber;
	
	public CDS(String id, String name, int start, int stop, String geneId, String source, String strand,
			String transcriptId, String proteinId, int exonNumber) {
		super();
		this.id = id;
		this.name = name;
		this.start = start;
		this.stop = stop;
		this.geneId = geneId;
		this.source = source;
		this.strand = strand;
		this.transcriptId = transcriptId;
		this.proteinId = proteinId;
		this.exonNumber = exonNumber;
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

	public String getTranscriptId() {
		return transcriptId;
	}

	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}

	public String getProteinId() {
		return proteinId;
	}

	public void setProteinId(String proteinId) {
		this.proteinId = proteinId;
	}

	public int getExonNumber() {
		return exonNumber;
	}

	public void setExonNumber(int exonNumber) {
		this.exonNumber = exonNumber;
	} 
	

	
	
	
	

}
