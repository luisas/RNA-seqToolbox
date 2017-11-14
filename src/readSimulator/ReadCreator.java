package readSimulator;

import java.util.HashMap;

import org.apache.commons.math3.distribution.NormalDistribution;

import exonSkipping.Annotation;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import exonSkipping.Utilities;

public class ReadCreator {

	private HashMap<String, Fragment> fragments;

	private Annotation GTFannotation;
	private GenomeSequenceExtractor ge;
	private int length;
	private double frlength;
	private double SD;
	private  String readcounts;
	private double mutationrate;
	private  int nMut;



	public ReadCreator(Annotation gTFannotation, GenomeSequenceExtractor ge,
			int length, double frlength, double sD, String readcounts,
			double mutationrate, int nMut) {
		super();
		GTFannotation = gTFannotation;
		this.ge = ge;
		this.length = length;
		this.frlength = frlength;
		SD = sD;
		this.readcounts = readcounts;
		this.mutationrate = mutationrate;
		this.nMut = nMut;

		this.fragments = new HashMap<String,Fragment>();

		calcReads();

		}


		public void calcReads(){

			//PARSE THE READCOUNT FILE
			HashMap<String,HashMap<String, Integer>> readCounts = Utils.parseReadCount(readcounts);


			int idFragment = 0 ;
			for(String gene : readCounts.keySet()){

					for(String t: readCounts.get(gene).keySet()){
						System.out.println("TRANSCRIPT "+t);


						Transcript transcript = GTFannotation.getGenes().get(gene).getTranscripts().get(t);
						String chr = GTFannotation.getGenes().get(gene).getChr();

						NormalDistribution nd = new NormalDistribution(frlength, SD);
						int FL;
						int startPosition;
						int numberReads = readCounts.get(gene).get(t);
						String fragmentSequence ="";
						String readSequenceFW ="";
						String readSequenceRW = "";
						Utilities.printRegionVector(GTFannotation.getGenes().get(gene).getRegionVectorTranscripts());


						RegionVector prova = Utils.getGenomicPositions(50010073, 50017566, gene, t, GTFannotation);

						Utilities.printRegionVector(prova);

						for(int id= 0 ; id < numberReads; id++){
							//CALC FRAGMENT LENGTH
							 int ndSample = (int) nd.sample();
							 FL = Integer.max(length, ndSample);

							 //GET RANDOM START POSITION
							 startPosition = Utils.getRandomPos(transcript.getLength(), FL);
							 int Fend = startPosition+FL-1;

							 //GET FRAGMENT SEQUENCE
							 fragmentSequence = ge.getSequence(chr, startPosition,Fend);

							 System.out.println("spos "+startPosition+ "     end "+Fend);
							 //System.out.println("FragementSequence "+fragmentSequence);
							 //GET FW READ SEQUENCE
							 int startFW =  startPosition;
							 int stopFW = startPosition+length;
							 readSequenceFW=ge.getSequence(chr, startFW, stopFW);

							 //GET RW READ SEQUENCE
							 int startRW = Fend-length+1;
							 int stopRW=Fend;
							 readSequenceRW=Utils.getRevComplement(ge.getSequence(chr, startRW,stopRW));

							 //GET NUMBER MUTATIONS
							 MutatedSeq mFW = new MutatedSeq(readSequenceFW,nMut);
							 MutatedSeq mRW = new MutatedSeq(readSequenceRW,nMut);

							 //STORE READS
							 RegionVector genPosFW = Utils.getGenomicPositions( startFW, stopFW,  gene,  t,  GTFannotation);
							 RegionVector genPosRW = Utils.getGenomicPositions( startRW, stopRW,  gene,  t,  GTFannotation);

							 Read RW = new Read("+", startRW, stopRW, mRW,genPosRW);
							 Read FW = new Read("-", startFW, stopFW, mFW,genPosFW);


							 Fragment fragment = new Fragment(Integer.toString(idFragment), chr, gene, t, FW, RW);
							 fragments.put(Integer.toString(idFragment), fragment);
						}



					}

				idFragment++;
			}



		}






	public HashMap<String, Fragment> getFragments() {
		return fragments;
	}
	public void setFragments(HashMap<String, Fragment> fragments) {
		this.fragments = fragments;
	}
	public Annotation getGTFannotation() {
		return GTFannotation;
	}
	public void setGTFannotation(Annotation gTFannotation) {
		GTFannotation = gTFannotation;
	}
	public GenomeSequenceExtractor getGse() {
		return ge;
	}
	public void setGse(GenomeSequenceExtractor gse) {
		this.ge = gse;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public double getFrlength() {
		return frlength;
	}
	public void setFrlength(double frlength) {
		this.frlength = frlength;
	}
	public double getSD() {
		return SD;
	}
	public void setSD(double sD) {
		SD = sD;
	}









	public String getReadcounts() {
		return readcounts;
	}









	public void setReadcounts(String readcounts) {
		this.readcounts = readcounts;
	}









	public double getMutationrate() {
		return mutationrate;
	}









	public void setMutationrate(double mutationrate) {
		this.mutationrate = mutationrate;
	}









	public int getnMut() {
		return nMut;
	}









	public void setnMut(int nMut) {
		this.nMut = nMut;
	}








}
