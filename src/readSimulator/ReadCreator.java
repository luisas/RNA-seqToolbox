package readSimulator;

import java.util.HashMap;

import org.apache.commons.math3.distribution.NormalDistribution;

import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import exonSkipping.Utilities;


/**
 * Saves the reads inside the Fragments hashMap.
 * This can be then used to retrieve the informations needed for the final output.
 *
 * @author santus
 *
 */
public class ReadCreator {

	private HashMap<Integer, Fragment> fragments;

	private Annotation GTFannotation;
	private GenomeSequenceExtractor ge;
	private int length;
	private double frlength;
	private double SD;
	private  String readcounts;
	private double mutationrate;


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

		this.fragments = new HashMap<Integer,Fragment>();

		calcReads();

		}


		public void calcReads(){

			//PARSE THE READCOUNT FILE
			HashMap<String,HashMap<String, Integer>> readCounts = Utils.parseReadCount(readcounts);


			int idFragment = 0 ;
			for(String geneID : readCounts.keySet()){

					for(String t: readCounts.get(geneID).keySet()){
						//System.out.println("TRANSCRIPT "+t);

						Gene gene = GTFannotation.getGenes().get(geneID);
						Transcript transcript = gene.getTranscripts().get(t);
						String chr = gene.getChr();

						NormalDistribution nd = new NormalDistribution(frlength, SD);
						int FL;
						int startPosition;
						int numberReads = readCounts.get(geneID).get(t);
						String fragmentSequence ="";
						String readSequenceFW ="";
						String readSequenceRW = "";



						//System.out.println("Number Reads"+numberReads);
						for(int id= 0 ; id < numberReads; id++){
							//CALC FRAGMENT LENGTH
							 int ndSample = (int) nd.sample();

							 FL = Integer.max(length, ndSample);

							 //System.out.println("ND SAMPLE"+ndSample);
							 //GET RANDOM START POSITION
							 startPosition = Utils.getRandomPos(transcript.getRegionVectorExons().getRegionsLength(), FL);
							 int endPosition = startPosition+FL;

							 //System.out.println("fragment LENGTH : "+FL);
							 //GET FRAGMENT SEQUENCE
							// fragmentSequence = ge.getFragmentSequence(startPosition,endPosition,transcript,gene);

							 //System.out.println(fragmentSequence);

							 //GET FW READ SEQUENCE
							 int startFW =  0;
							 int stopFW = length;
							 readSequenceFW = fragmentSequence.substring(0, length);
							 //readSequenceFW=ge.getReadSequence(startFW, stopFW,transcript,gene);
							 //System.out.println("CHR:"+chr+" startFW"+startFW +"stopFW"+stopFW);
							// System.out.println(readSequenceFW);

							 //GET RW READ SEQUENCE
							 int stopRW=FL;
							 int startRW = stopRW-length;
							 readSequenceRW= Utils.getRevComplement(fragmentSequence.substring(startRW, stopRW));
							// System.out.println(readSequenceRW);

							 //GET MUTATED SEQUENCES
							 MutatedSeq mFW = new MutatedSeq(readSequenceFW,mutationrate);
							 MutatedSeq mRW = new MutatedSeq(readSequenceRW,mutationrate);

							 //STORE READS
							 RegionVector genPosFW = Utils.getGenomicRV( startFW+startPosition, stopFW+startPosition,  geneID,  t,  GTFannotation);
							 RegionVector genPosRW = Utils.getGenomicRV( startRW+startPosition, stopRW+startPosition,  geneID,  t,  GTFannotation);

							 Read RW = new Read("+", startRW, stopRW, mRW,genPosRW);
							 Read FW = new Read("-", startFW, stopFW, mFW,genPosFW);


							 //STORE FRAGEMENTS
							 Fragment fragment = new Fragment(Integer.toString(idFragment), chr, geneID, t,startPosition,endPosition, FW, RW);
							 fragments.put(idFragment, fragment);
							 idFragment++;

						}



					}

			}



		}






	public HashMap<Integer, Fragment> getFragments() {
		return fragments;
	}
	public void setFragments(HashMap<Integer, Fragment> fragments) {
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


}
