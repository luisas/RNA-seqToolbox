package exonSkipping;

import java.util.ArrayList;

public class Gene {
	
	private String id; 
	private String name;
	private String chr; 
	private int start; 
	private int stop; 
	private String biotype; 
	private String source; 
	private String strand; 
	private ArrayList<String> transcriptIds;
	
	
	public Gene() {
		super();
	}


	public Gene(String id, String name,String chr,  int start, int stop, String biotype, String source, String strand, ArrayList<String> transcriptIds) {
		this.id= id;
		this.name=name;
		this.chr=chr;
		this.stop= stop; 
		this.start=start; 
		this.source=source; 
		this.biotype=biotype;
		this.strand=strand; 
		this.transcriptIds=transcriptIds;
		
	}

	
	public static void addTranscriptId(String transcriptId, Gene gene) {
		
		gene.transcriptIds.add(transcriptId);
		
	}
	public String getId() {
		return id;
	}

	public ArrayList<String> getTranscriptIds() {
		return transcriptIds;
	}

	public void setTranscriptIds(ArrayList<String> transcriptIds) {
		this.transcriptIds = transcriptIds;
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

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}
	
	
	
	

}
