package bamFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import readSimulator.Utils;

import exonSkipping.Annotation;
import exonSkipping.Gene;
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

	private Annotation GTFannotation;

	private HashMap mapT; 
	private boolean splitInconsistent = false; 





	public bamAnnotation(SAMRecord fw, SAMRecord rw, Annotation GTFannotation) {
		super();

		if(rw.getMateNegativeStrandFlag()) {
			SAMRecord temp = fw;
			fw = rw;
			rw= temp;
		}

		this.GTFannotation = GTFannotation;
		this.readId = fw.getReadName();
		this.mm = getSumMM(fw,rw);
		this.clippingCount=getSumClippingCount(fw,rw);
		this.splitCount = calcSplitCount(fw,rw);
		this.geneCount = calcGeneCount(fw,rw,GTFannotation);
//		this.transcripts = calcTranscripts(fw,rw);
//		this.gdist = calcgdist(fw,rw);
//		this.antisense = calcAntisense(fw,rw);
//		this.pcrindex = calcPCRindex(fw,rw);
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
	    * GENE COUNT
	    */


	   public int calcGeneCount(SAMRecord fw, SAMRecord rw, Annotation GTFannotation){

		   Region g;
		  // System.out.println("::::::::::::::::::::::::::::::");
//		   System.out.println(fw.getAlignmentStart()+"             "+rw.getAlignmentEnd());
//		   System.out.println(fw.getMateNegativeStrandFlag());

	

		   Region r = new Region(fw.getAlignmentStart(),rw.getAlignmentEnd());



		   List<Gene> genes = new ArrayList<Gene>();
		   HashMap<Gene,List<String>> map  = new HashMap();
 
		   for(Gene gene :  GTFannotation.getGenes().values() ) {
			   List<String> values = new ArrayList<>();
				g = new Region(gene.getStart(), gene.getStop());
				//within one gene (part of a gene)
				if(g.getStart() <= r.getStart()  && g.getEnd() >= r.getEnd() && fw.getReferenceName().equals(gene.getChr())) {
					genes.add(gene);
					
					
					for(Transcript transcript : gene.getTranscripts().values()){
						
						Region t = new Region(transcript.getStart(),transcript.getStop());
						//Inside of a transcript
						if(t.getStart() <= r.getStart()  && t.getEnd() >= r.getEnd() ){
							RegionVector introns_t = transcript.getRegionVectorExons().inverse(); 
							RegionVector introns_reads = getIntronsReads(fw,rw);
					
							//System.out.println("INTRONS READS "+Utils.prettyRegionVector(introns_reads));
							//System.out.println("INTRONS T "+Utils.prettyRegionVector(transcript.getRegionVectorExons()));
							if(!introns_reads.isConsistent(introns_t)) {
								if(introns_reads.isConsistent(getMergedRV(gene))) {
									values.add("MERGED");
								}else {
									values.add("INTRON");			
								}
							}
							else {
								//System.out.println(gene.getId());
								values.add(transcript.getId());
							}
								
						}
//							if(first && !values.isEmpty()) {
//								values.clear();
//								first =false; 
//							}
//							System.out.println("luisa");
//							values.add(transcript.getId());
//						
//							transcriptFound = true; 
//								
						}
//						else if(!transcriptFound) {
//							System.out.println("luisa");
//								
//								
//								
//							
//						}
					map.put(gene,values);	
					}
				
				}
		
		   
		   this.setMapT(map); 
		   
		   

		  // System.out.println();
		   //System.out.println("GENES SIZE: "+genes.size());
		   //System.out.println("------------------------------------------");
		   if(genes.size() == 0 ){
		   }
		   return genes.size();
	   }

	   public String getStringFromMap(HashMap<Gene,List<String>> map) {
		   
		   StringBuilder sb = new StringBuilder();
		   String prefix="";
		   //System.out.println("HOLA");
		   //System.out.println(map.size());
		   for(Gene gene : map.keySet()) {
			   
			   sb.append(prefix+gene.getId()+","+gene.getBiotype()+":");
			   prefix="|";
			   
			   String p = "";
			   for(String s : map.get(gene)) {
					   sb.append(p+s);
					   p=",";
				} 	   
		   }
		   return sb.toString();
	   }
	   
	   public static RegionVector getMergedRV(Gene gene) {
		   
		   RegionVector transcripts = new RegionVector(); 
		   
		  // System.out.println("sdf "+gene.getTranscripts().values().size());
		   for(Transcript t : gene.getTranscripts().values()) {
			  // System.out.println(gene.getId());
			   transcripts.getVector().addAll(t.getRegionVectorExons().getVector());
		   }
		   

		   return transcripts.merge();
		   
	   }
	   
public static RegionVector getIntronsRead(SAMRecord fw) {
		   
		   RegionVector exons = new RegionVector();
		   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength();

			   Region g_ref = new Region(ref_s, ref_e);
			   exons.getVector().add(g_ref);
			   System.out.println(ref_s+" "+ ref_e);
		   }

		   
		   System.out.println("exons: "+Utils.prettyRegionVector(exons));
		   return exons.inverse();
		   
		   
	   }

	   public static RegionVector getIntronsReads(SAMRecord fw,SAMRecord rw) {
		   
		   RegionVector exons = new RegionVector();
		   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength();

			   Region g_ref = new Region(ref_s, ref_e);
			   exons.getVector().add(g_ref);
			   //System.out.println(ref_s+" "+ ref_e);
		   }
		   //System.out.println("change");
		   for(AlignmentBlock ab: rw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength();
			   Region g_ref = new Region(ref_s, ref_e);
			   //System.out.println(ref_s+" "+ ref_e);
			   //System.out.println(exons.getVector().contains(g_ref));
			   if(!exons.getVector().contains(g_ref)){
				   exons.getVector().add(g_ref);
			   }
		   }
		   
		   //System.out.println("exons: "+Utils.prettyRegionVector(exons));
		   return exons.inverse();
		   
		   
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

	   RegionVector exons2 = new RegionVector();
	   RegionVector exons1 = new RegionVector();

	   RegionVector introns1 = new RegionVector();
	   RegionVector introns2 = new RegionVector();
	   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
		   int ref_s  = ab.getReferenceStart();
		   int ref_e = ref_s + ab.getLength();
		   Region g_ref = new Region(ref_s, ref_e);
		   exons1.getVector().add(g_ref);
		   System.out.println(ref_s+" "+ ref_e);
	   }
	   introns1.getVector().addAll(exons1.merge().inverse().getVector());


	   System.out.println("change");
	   for(AlignmentBlock ab1: rw.getAlignmentBlocks()){
		   int ref_s  = ab1.getReferenceStart();
		   int ref_e = ref_s + ab1.getLength();
		   Region g_ref = new Region(ref_s, ref_e);
		   System.out.println(ref_s+" "+ ref_e);
		 //  System.out.println(exons.getVector().contains(g_ref));
		   exons2.getVector().add(g_ref);
	   }
	   for(Region intron : exons2.merge().inverse().getVector()) {
		   if(!introns1.getVector().contains(intron)){
			   introns2.getVector().add(intron);
		   }
	   }
	   

	   System.out.println("testing split inco");
//	   System.out.println("exons  : "+Utils.prettyRegionVector(exons));
//	   System.out.println("exons merged : "+Utils.prettyRegionVector(exons.merge()));
//	   System.out.println("introns  : "+Utils.prettyRegionVector(introns.merge()));
//	   System.out.println("split count:"+(introns.getNumberRegion()));
	   //RegionVector introns = exons.merge().inverse();
	   boolean split_inc = check_if_split_inconsistent(exons1,exons2);
	  
	   if(split_inc){
		   System.out.println("INCONSISTENT");
		   setSplitInconsistent(true);
		   return -1 ;
		  
	   }
	   return introns1.getNumberRegion()+introns2.getNumberRegion();
   }

   public boolean check_if_split_inconsistent(RegionVector exons1, RegionVector exons2) {

	   RegionVector introns1 = exons1.inverse();
	   RegionVector introns2 = exons2.inverse();
//	   System.out.println(Utils.prettyRegionVector(exons));
//	   System.out.println("introns");
   
	   //


	   System.out.println("exons1 : "+Utils.prettyRegionVector(exons2));
	   System.out.println("introns2 : "+Utils.prettyRegionVector(introns1));
		   for(Region exon : exons2.getVector()) {
			   for(Region intron : introns1.getVector()) {
				   if(exon.overlap(intron)>0) {
					  return true; 
				   }
			   }
		   }
		   
		   System.out.println("exons1 : "+Utils.prettyRegionVector(exons1));
		   System.out.println("introns2 : "+Utils.prettyRegionVector(introns2));
		   for(Region exon : exons1.getVector()) {
			   for(Region intron : introns2.getVector()) {
				   if(exon.overlap(intron)>0) {
					  return true; 
				   }
			   }
	
		   }
	   
	   
	   
	 
	   
	   return false; 
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
//	   boolean endsAtBeginning = false;
//	   boolean beginsAtEnd = false;
//	   boolean after = false;
//	   boolean onefound = false ;
//
////	   if(exons.getVector().size()<=2){
////		   return false;
////	   }
//	   for(Region i : introns.getVector()){
//
//		   for(Region r1 : exons.getVector()){
//
//			  if(i.getEnd() == r1.getStart()){
//				  endsAtBeginning = true;
//				  onefound = true ;
//			  }
//			  if(onefound){
//				  for(Region r2 : exons.getVector()){
//					  if(i.getStart() == r2.getEnd()){
//						  beginsAtEnd = true;
//						  after = true ;
//					  }
//					  break;
//				  }
//
//
//			  }
//		   }
//
//
//	   }
//
////	   System.out.println("beginsAtEnd " + beginsAtEnd);
////	   System.out.println("endsAt beginning "+ endsAtBeginning);
////	   System.out.println("after " + after);
//	   if(!endsAtBeginning && !beginsAtEnd && !after){
//		   return true ;
//	   }
//
//
//	   return false;
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





	public HashMap getMapT() {
		return mapT;
	}





	public void setMapT(HashMap mapT) {
		this.mapT = mapT;
	}





	public boolean isSplitInconsistent() {
		return splitInconsistent;
	}





	public void setSplitInconsistent(boolean splitInconsistent) {
		this.splitInconsistent = splitInconsistent;
	}





}
