package exonSkipping;

public class Exon {
	
	private int number; 
	private String id; 
	private String name; 
	private int start; 
	private int stop; 
	private String geneId; 
	private String biotype; 
	private String source; 
	private String strand; 
	private String transcriptId;

	
	public Exon(int number,String id,  String name, int start, int stop, String geneId, String TranscriptId, String biotype, String source, String strand) {
		this.number= number;
		this.id= id; 
		this.name=name; 
		this.stop= stop; 
		this.start=start; 
		this.geneId= geneId;
		this.transcriptId= transcriptId;
		this.source=source; 
		this.biotype=biotype;
		this.strand=strand; 
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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

	public String getTranscriptId() {
		return transcriptId;
	}

	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}
	
	
	

}
