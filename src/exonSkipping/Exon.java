package exonSkipping;

public class Exon {

	private int number;
	private int start;
	private int stop;
	private String transcriptId;
	private RegionVector CDS;


	public Exon() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Exon(int number, int start, int stop, String transcriptId) {
		super();
		this.number = number;
		this.start = start;
		this.stop = stop;
		this.transcriptId = transcriptId;
		this.CDS= new RegionVector();
	}

	public Exon(int number, int start, int stop, String transcriptId, RegionVector CDS) {
		super();
		this.number = number;
		this.start = start;
		this.stop = stop;
		this.transcriptId = transcriptId;
		this.CDS= CDS;
	}


	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
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


	public String getTranscriptId() {
		return transcriptId;
	}


	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}


	public RegionVector getCDS() {
		return CDS;
	}


	public void setCDS(RegionVector cDS) {
		CDS = cDS;
	}










}
