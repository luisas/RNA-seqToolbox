package featureCounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;
import plots.Pair;
import readSimulator.Utils;


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
	private RegionVector grv;

	private boolean splitInconsistent = false; 
	
	public String frstrand; 
	private HashMap<Gene,List<String>>  mapT; 





	///////////////////////////////////////////////////////
	///				CONSTRUCTOR
	//////////////////////////////////////////////////////

	public bamAnnotation(SAMRecord fw, SAMRecord rw, Annotation GTFannotation, String frstrand) {
		super();

		if(rw.getMateNegativeStrandFlag()) {
			SAMRecord temp = fw;
			fw = rw;
			rw= temp;
		}
		this.frstrand = frstrand; 
		this.readId = fw.getReadName();
		this.mm = getSumMM(fw,rw);
		this.clippingCount=getSumClippingCount(fw,rw);
		this.splitCount = calcSplitCount(fw,rw);
		this.geneCount = calcGeneCount(fw,rw,GTFannotation);
		this.grv =getGenomicRV(fw, rw);
				
	}
 
	


	   /*
	    * GENE COUNT
	    */
	    String readtest= "7800682";


	   public int calcGeneCount(SAMRecord fw, SAMRecord rw, Annotation GTFannotation){

		   Region g;
		   Region r = new Region(getGenomicRV(fw,rw).getStart(),getGenomicRV(fw,rw).getStop());

		   List<Gene> genes = new ArrayList<Gene>();
		   HashMap<Gene,List<String>> map  = new HashMap<Gene,List<String>>();
 
		   int priority =0 ;
		   //priority for intronic =1
		   //priority for merged =2
		   //pririty for match =3
		   
	
		   //ITERATE THE GENES
		   for(Gene gene :  GTFannotation.getGenes().values() ) {

			   
			    List<String> values = new ArrayList<>();
				g = new Region(gene.getStart(), gene.getStop());
				//within one gene (part of a gene)

				
				if(g.getStart() <= r.getStart()  && g.getEnd() >= r.getEnd() && fw.getReferenceName().equals(gene.getChr())) {

					genes.add(gene);
			

					for(Transcript transcript : gene.getTranscripts().values()){
						
						Region t = new Region(transcript.getStart(),transcript.getStop());
						
						if(t.getStart() <= r.getStart()  && t.getEnd() >= r.getEnd() ){
							RegionVector exons_t = transcript.getRegionVectorExons().merge(); 
							RegionVector exons_reads1 = getExonsReads(fw).merge();
							RegionVector exons_reads2 = getExonsReads(rw).merge();
							if(fw.getReadName().equals(readtest)) {
								System.out.println("----------------------------------------------------");
								System.out.println(readtest);
								System.out.println(Utils.prettyRegionVector(getGenomicRV(fw,rw)));
								System.out.println("GENE  "+ gene.getStart()+"-"+gene.getStop());
								System.out.println("TRANSCRIPT " + Utils.prettyRegionVector(transcript.getRegionVectorExons()));
								
								//exons read one is not consistent apparently but should be!
								System.out.println(Utils.prettyRegionVector(exons_reads1));
								System.out.println(Utils.prettyRegionVector(exons_t));
								System.out.println("----------------------------------------------------");
							}
							if(!exons_reads1.isConsistent(exons_t) || !exons_reads2.isConsistent(exons_t) ) {
								if(exons_reads1.isMergedContained(getMergedRV(gene)) && exons_reads2.isMergedContained(getMergedRV(gene))) {
									if(priority <=2) {
									priority=2; 
									values= cleanValues(values,"MERGED",null);
									Pair<HashMap<Gene,List<String>>, List<Gene>> p1 = clearMap( map , genes, false);
									map =p1.first;
									genes =p1.second;
									values.add("MERGED");
									}
								}else {
									if(priority <=1) {
									priority =1 ; 
									//new add 
									values= cleanValues(values,"INTRON",null);
									values.add("INTRON");
									}
								}
							}
							else {
								priority = 3; 
								values= cleanValues(values,"MERGED","INTRON");
								Pair<HashMap<Gene,List<String>>, List<Gene>> p = clearMap( map , genes, true);
								map =p.first;
								genes =p.second;
								values.add(transcript.getId());
								
							}
							
								
						}
							
						}

					if(!values.isEmpty()) {
					  map.put(gene,values);	
					}
					}
				
				}
		   this.setMapT(map); 
		   if(genes.size() == 0 ){
		   }
		   return map.keySet().size();
	   }
	   
	   
	   public static Pair<HashMap<Gene,List<String>>, List<Gene>> clearMap(HashMap<Gene,List<String>> map , List<Gene> genes, boolean merge) {
		   
		   List<Gene> genesCopy = new ArrayList<Gene>(genes);
		   HashMap<Gene,List<String>> mapCopy = new HashMap<Gene,List<String>>(map);
		   
		   for(Gene gene : genes) {
			   if(map.containsKey(gene) ) {
				   if(map.get(gene).contains("INTRON")) {
					   mapCopy.remove(gene);
					   genesCopy.remove(gene);
				   }
				   else if(merge) {
					   if(map.get(gene).contains("MERGED")) {
						   mapCopy.remove(gene);
						   genesCopy.remove(gene);
					   } 
				   }
			   }
			   
		   }
		return new Pair<HashMap<Gene,List<String>>, List<Gene>>(mapCopy,genesCopy);
	   }
	   
	public static List<String> cleanValues(List<String> list , String a , String b ){
		List<String> result = new ArrayList<String>();
		for( String s : list) {
			if(!s.equals(a) && !s.equals(b)) {
				result.add(s);
			}
		}
		return result;
	}

	   public String getStringFromMap(HashMap<Gene,List<String>> map) {
		   
		   StringBuilder sb = new StringBuilder();
		   String prefix="";
		   //System.out.println("HOLA");
		   //System.out.println(map.size());
		   for(Gene gene : map.keySet()) {
			   if(map.get(gene).size()>0) {
			   sb.append(prefix+gene.getId()+","+gene.getBiotype()+":");
			   prefix="|";
			   
			   String p = "";
			   for(String s : map.get(gene)) {
				   
					   sb.append(p+s);
					   p=",";
				}
			   }
		   }
		   return sb.toString();
	   }
	   
	 

	   
	///////////////////////////////////////////////////////
	///		INFORMATION EXTRACTORS FOR ANNOTATION
	//////////////////////////////////////////////////////
	

	/*
	 * MM : works
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
    * CLIPPINGS : works
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
 * SPLIT COUNT : works
 */

   public int calcSplitCount(SAMRecord fw, SAMRecord rw) {

	   RegionVector exons2 = getExonsReads(rw);
	   RegionVector exons1 = getExonsReads(fw);

	   RegionVector introns1 = new RegionVector();
	   RegionVector introns2 = new RegionVector();
	   
	   introns1.getVector().addAll(exons1.merge().inverse().getVector());

	   for(Region intron : exons2.merge().inverse().getVector()) {
		   if(!introns1.getVector().contains(intron)){
			   introns2.getVector().add(intron);
		   }
	   }
	   boolean split_inc = check_if_split_inconsistent(exons1,exons2);
	  
	   
	   if(split_inc){
		   setSplitInconsistent(true);
		
		   
		   
		   return -1 ;
		  
	   }
	   return introns1.getNumberRegion()+introns2.getNumberRegion();
   }
   
   
   
   
   
	///////////////////////////////////////////////////////
	///				HELP FUNCTIONS
	//////////////////////////////////////////////////////
   
   
   /*
    * Given fw and rw, returns a region Vector with all EXONS
    */
	public RegionVector getGenomicRV(SAMRecord fw, SAMRecord rw) {
		if(rw.getMateNegativeStrandFlag()) {
			SAMRecord temp = fw;
			fw = rw;
			rw= temp;
		}
		RegionVector grv = new RegionVector();
		for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength()-1;
			   Region g_ref = new Region(ref_s, ref_e);
			   grv.getVector().add(g_ref);
		   }
		for(AlignmentBlock ab: rw.getAlignmentBlocks()){
			   int ref_s1  = ab.getReferenceStart();
			   int ref_e1 = ref_s1 + ab.getLength()-1;
			   Region g_ref = new Region(ref_s1, ref_e1);
			   grv.getVector().add(g_ref);
		   }	
		return grv.merge(); 
	}
	
	public RegionVector getGenomicRV2(SAMRecord fw, SAMRecord rw) {
		if(rw.getMateNegativeStrandFlag()) {
			SAMRecord temp = fw;
			fw = rw;
			rw= temp;
		}
		RegionVector grv = new RegionVector();
		for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength();
			   Region g_ref = new Region(ref_s, ref_e);
			   grv.getVector().add(g_ref);
		   }
		for(AlignmentBlock ab: rw.getAlignmentBlocks()){
			   int ref_s1  = ab.getReferenceStart();
			   int ref_e1 = ref_s1 + ab.getLength();
			   Region g_ref = new Region(ref_s1, ref_e1);
			   grv.getVector().add(g_ref);
		   }	
		
		return grv.merge(); 
	}
   
	/*
	 * Given one read, return EXONS RV
	 */
	
	   public static RegionVector getExonsReads(SAMRecord fw) {
		   RegionVector exons1 = new RegionVector();
		   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength()-1;
			   Region g_ref = new Region(ref_s, ref_e);
			   exons1.getVector().add(g_ref);
		   }
		   return exons1;
	   }
	   
	   public static RegionVector getExonsReadsMod(SAMRecord fw) {
		   RegionVector exons1 = new RegionVector();
		   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength();
			   Region g_ref = new Region(ref_s, ref_e);
			   exons1.getVector().add(g_ref);
		   }
		   return exons1;
	   }
	   
	   
	   /*
	    * Collects all trancript of a genes and returns the Merged RV
	    */
	   
	   public static RegionVector getMergedRV(Gene gene) {
		   
		   RegionVector transcripts = new RegionVector(); 
		   for(Transcript t : gene.getTranscripts().values()) {
			   transcripts.getVector().addAll(t.getRegionVectorExons().getVector());
		   }
		   return transcripts.merge();
	   }
	   

   

	///////////////////////////////////////////////////////
	///				CHECK FOR SPLIT INCONSISTENCY
	//////////////////////////////////////////////////////
   
   public static boolean check_if_split_inconsistent(RegionVector exons1, RegionVector exons2) {

	   RegionVector introns1 = exons1.inverse();
	   RegionVector introns2 = exons2.inverse();
		   for(Region exon : exons2.getVector()) {
			   for(Region intron : introns1.getVector()) {
				   if(exon.overlap(intron)>0) {
					  return true; 
				   }
			   }
		   }
		   
		   for(Region exon : exons1.getVector()) {
			   for(Region intron : introns2.getVector()) {
				   if(exon.overlap(intron)>0) {
					  return true; 
				   }
			   }
	
		   }   
	   return false; 
   }



   
	///////////////////////////////////////////////////////
	///				GETTER & SETTERS 
	//////////////////////////////////////////////////////

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
	public boolean isSplitInconsistent() {
		return splitInconsistent;
	}
	public void setSplitInconsistent(boolean splitInconsistent) {
		this.splitInconsistent = splitInconsistent;
	}
	public RegionVector getGrv() {
		return grv;
	}
	public void setGrv(RegionVector grv) {
		this.grv = grv;
	}
	public HashMap<Gene,List<String>>  getMapT() {
		return mapT;
	}
	public void setMapT(HashMap<Gene,List<String>>  mapT) {
		this.mapT = mapT;
	}
}
