package exonSkipping;

import java.util.List;

public class Transcript {

	private String id; 
	private String name; 
	private int start; 
	private int stop; 
	private String geneId; 
	private String biotype; 
	private String source; 
	private String strand; 
	
	public Transcript(String id, String name, int start, int stop, String geneId, String biotype, String source, String strand) {
		this.id= id;
		this.name=name; 
		this.stop= stop; 
		this.start=start; 
		this.geneId= geneId;
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
	
	
	
	
}
