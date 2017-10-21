package exonSkipping;

import java.util.List;

public class Gene {
	
	private String id; 
	private String name; 
	private int start; 
	private int stop; 
	private List<Transcript> transcripts; 
	private String biotype; 
	private String source; 
	private String strand; 
	
	public Gene(String id, String name, int start, int stop, List<Transcript> transcripts, String biotype, String source, String strand) {
		this.id= id;
		this.name=name; 
		this.stop= stop; 
		this.start=start; 
		this.transcripts=transcripts;
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

	public List<Transcript> getTranscripts() {
		return transcripts;
	}

	public void setTranscripts(List<Transcript> transcripts) {
		this.transcripts = transcripts;
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
