package exonSkipping;

public class CDS {

	private int id;
	private int start;
	private int stop;
	private String geneId;
	private String proteinId;


	public CDS(int id, int start, int stop, String geneId, String proteinId) {
		super();
		this.id = id;
		this.start = start;
		this.stop = stop;
		this.geneId = geneId;
		this.proteinId = proteinId;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
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


	public String getProteinId() {
		return proteinId;
	}


	public void setProteinId(String proteinId) {
		this.proteinId = proteinId;
	}




}
