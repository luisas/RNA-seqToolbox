package readSimulator;

public class Fragment {


	private String id;
	private String chr;
	private String geneID;
	private String transcriptID;
	private int tstart;
	private int tstop;
	private Read FW;
	private Read RW;




	public Fragment(String id, String chr, String geneID, String transcriptID,
			int tstart, int tstop, Read fW, Read rW) {
		super();
		this.id = id;
		this.chr = chr;
		this.geneID = geneID;
		this.transcriptID = transcriptID;
		this.tstart = tstart;
		this.tstop = tstop;
		FW = fW;
		RW = rW;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public String getGeneID() {
		return geneID;
	}
	public void setGeneID(String geneID) {
		this.geneID = geneID;
	}
	public String getTranscriptID() {
		return transcriptID;
	}
	public void setTranscriptID(String transcriptID) {
		this.transcriptID = transcriptID;
	}
	public int getTstart() {
		return tstart;
	}
	public void setTstart(int tstart) {
		this.tstart = tstart;
	}
	public int getTstop() {
		return tstop;
	}
	public void setTstop(int tstop) {
		this.tstop = tstop;
	}
	public Read getFW() {
		return FW;
	}
	public void setFW(Read fW) {
		FW = fW;
	}
	public Read getRW() {
		return RW;
	}
	public void setRW(Read rW) {
		RW = rW;
	}












}
