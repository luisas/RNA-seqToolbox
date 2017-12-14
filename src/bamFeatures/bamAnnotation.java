package bamFeatures;

import java.util.Vector;

import readSimulator.Utils;

import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;

import static readSimulator.Utils.*;

public class bamAnnotation {

	private String readId;
	private int mm;
	private int clippingCount;
	private int splitCount;
	private int geneCount;
	private Vector<String> transcripts;
	private int gdist;
	private boolean antisense;
	private int pcrindex;



	public bamAnnotation(SAMRecord fw, SAMRecord rw) {
		super();

		if(rw.getFirstOfPairFlag()) {
			SAMRecord temp = fw;
			fw = rw;
			rw= temp;
		}


		this.readId = fw.getReadName();
		this.mm = getSumMM(fw,rw);
		this.clippingCount=getSumClippingCount(fw,rw);
		this.splitCount = calcSplitCount(fw,rw);
	}





	public bamAnnotation(String readId, int mm, int clippingCount, int splitCount, int geneCount,
			Vector<String> transcripts, int gdist, boolean antisense, int pcrindex) {
		super();
		this.readId = readId;
		this.mm = mm;
		this.clippingCount = clippingCount;
		this.splitCount = splitCount;
		this.geneCount = geneCount;
		this.transcripts = transcripts;
		this.gdist = gdist;
		this.antisense = antisense;
		this.pcrindex = pcrindex;
	}




	/*
	 * MM
	 */

	public int getSumMM(SAMRecord fw,SAMRecord rw) {
		return calcMM(fw)+calcMM(rw);

	}

   public int calcMM(SAMRecord sr){

	   Integer nm = (Integer) sr.getAttribute("NM");
	   nm = (nm!=null) ? nm :(Integer) sr.getAttribute("nM");
	   nm = (nm!=null) ? nm :(Integer) sr.getAttribute("XM");
	   return nm;
   }

   /*
    * CLIPPINGS
    */
   public int getSumClippingCount(SAMRecord fw,SAMRecord rw) {

	   return getClippingCount(fw)+ getClippingCount(rw);
   }

   public int getClippingCount(SAMRecord sr) {
	   int diff1=Math.abs(sr.getUnclippedStart()-sr.getAlignmentStart());
	   int diff2 = Math.abs(sr.getAlignmentEnd()-sr.getUnclippedEnd());
		return diff1+diff2;
   }

/*
 * SPLIT COUNT
 */

   public int calcSplitCount(SAMRecord fw, SAMRecord rw) {

	   RegionVector exons = new RegionVector();
	   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
		   int ref_s  = ab.getReferenceStart();
		   int ref_e = ref_s + ab.getLength();

		   Region g_ref = new Region(ref_s, ref_e);
		   exons.getVector().add(g_ref);
		   System.out.println(ref_s+" "+ ref_e);
	   }
	   System.out.println("change");
	   for(AlignmentBlock ab: rw.getAlignmentBlocks()){
		   int ref_s  = ab.getReferenceStart();
		   int ref_e = ref_s + ab.getLength();
		   Region g_ref = new Region(ref_s, ref_e);
		   System.out.println(ref_s+" "+ ref_e);

		   System.out.println(exons.getVector().contains(g_ref));
		   if(!exons.getVector().contains(g_ref)){
			   exons.getVector().add(g_ref);
		   }
	   }

	   System.out.println(Utils.prettyRegionVector(exons.merge()));

	   //RegionVector introns = exons.merge().inverse();
	   boolean split_inc = check_if_split_inconsistent(exons.merge());
	   if(split_inc){
		   return -1 ; 
	   }
	   return exons.merge().getNumberRegion()-1;
   }

   public boolean check_if_split_inconsistent(RegionVector exons) {

	   RegionVector introns = exons.inverse();
	   boolean endsAtBeginning = false;
	   boolean beginsAtEnd = false;
	   boolean after = false;

	   boolean onefound = false ;

//	   if(exons.getVector().size()<=2){
//		   return false; 
//	   }
	   for(Region i : introns.getVector()){

		   for(Region r1 : exons.getVector()){

			  if(i.getEnd() == r1.getStart()){
				  endsAtBeginning = true;
				  onefound = true ;
				  continue;
			  }
			  if(onefound){
				  if(i.getStart() == r1.getEnd()){
					  beginsAtEnd = true;
					  after = true ;
				  }
				  break;
			  }
		   }


	   }

	   System.out.println("beginsAtEnd " + beginsAtEnd);
	   System.out.println("endsAt beginning "+ endsAtBeginning);
	   System.out.println("after " + after);
	   if(!endsAtBeginning || !beginsAtEnd || !after){
		   return true ;
	   }


	   return false;
   }


	public String getReadId() {
		return readId;
	}
	public void setReadId(String readId) {
		this.readId = readId;
	}
	public int getMm() {
		return mm;
	}
	public void setMm(int mm) {
		this.mm = mm;
	}
	public int getClippingCount() {
		return clippingCount;
	}
	public void setClippingCount(int clippingCount) {
		this.clippingCount = clippingCount;
	}
	public int getSplitCount() {
		return splitCount;
	}
	public void setSplitCount(int splitCount) {
		this.splitCount = splitCount;
	}
	public int getGeneCount() {
		return geneCount;
	}
	public void setGeneCount(int geneCount) {
		this.geneCount = geneCount;
	}
	public Vector<String> getTranscripts() {
		return transcripts;
	}
	public void setTranscripts(Vector<String> transcripts) {
		this.transcripts = transcripts;
	}
	public int getGdist() {
		return gdist;
	}
	public void setGdist(int gdist) {
		this.gdist = gdist;
	}
	public boolean isAntisense() {
		return antisense;
	}
	public void setAntisense(boolean antisense) {
		this.antisense = antisense;
	}
	public int getPcrindex() {
		return pcrindex;
	}
	public void setPcrindex(int pcrindex) {
		this.pcrindex = pcrindex;
	}





}
