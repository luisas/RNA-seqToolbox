package diffSplicing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bam.bamAnnotation;
import exonSkipping.Annotation;
import exonSkipping.ExonSkipping;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import exonSkipping.Utilities;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMRecord;
import plots.Pair;
import readSimulator.Utils;

import static bamFeatures.bamAnnotation.*; 

import static plots.Pair.*;

public class psiAnnotation {

	private String geneId ; 
	private Region exon; 
	private int incl_count ; 
	private int excl_count; 
	private int total_count; 
	private double psi ;
	
	
	public psiAnnotation(Gene gene, ExonSkipping es , Region exon, List<ReadPair> reads, Annotation annotationGTF) {
		super();
		this.geneId = gene.getId();
		this.exon = exon;
		Pair<Integer, Integer> counts = getCounts(gene,es,reads, annotationGTF); 
		
		if(counts == null) {
			this.incl_count = 0; 
			this.excl_count = 0;

		}else {
			this.incl_count=counts.first;
			this.excl_count=counts.second;
			
		
		}
		
		this.total_count = incl_count + excl_count; 
		this.psi = calcPSI(incl_count, excl_count); 
		
		
	}

	
	public Pair<Integer,Integer> getCounts(Gene gene, ExonSkipping es,List<ReadPair> reads, Annotation annotationGTF){
		int incl =0 ; 
		int excl=0;
		final String result = "65497,65485,65576,65466,65553,65454,65556,65456,65478,65558,65547,65482,65460";
		
		
		List<String> found = new ArrayList<String>();
		List<String> foundEx = new ArrayList<String>();
		
		for (ReadPair rp : reads) {
		
		//IF overlap 
//		if(overlap(rp)) {
//			
//			RegionVector merged = rp.getExonsBoth().merge();
//			if(merged.contains(exon)) {
//				incl++;
//			}else {
//				excl++;
//			}
//			
//		
//			continue;
//		}
		
			
		//else	(not overlap)
		
				int iter =0 ; 
				while(iter<2) {
				
					SAMRecord read;
					if(iter == 0) {
						 read=rp.getFw();
					}else {
						 read=rp.getRw();
					}
					iter++;
					
					if(found.contains(read.getReadName())) {
						continue; 
					}
		
					if(!isTranscriptomic(rp.getFw(),rp.getRw(),gene)) {
						continue;
					}
				
					
					
					boolean leftFound=false;
					boolean exonLeftFound = false; 
					
					//RegionVector rv = toRv(rp.getFw(),rp.getRw());
					if(read.getReadName().equals("65576") || read.getReadName().equals("65482") ){
						System.out.println(read.getReadName());
						Utilities.printRead(read);
					}
					
					for(AlignmentBlock ab : read.getAlignmentBlocks()) { 
						
						Region r = new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength());
						//CENTRAL CASE
						if(exon.contains(r)) {
							incl++;
							found.add(read.getReadName());
							break;
						}
						//LEFT CASE: BLACK 
						//luisa ==
						if(ab.getReferenceStart()+ab.getLength() <= es.getSv().getStart()) {
							leftFound=true;
							continue;
						}
						if(leftFound) {
							if(ab.getReferenceStart()==exon.getStart()) {
								incl++;
								found.add(read.getReadName());
								break;
							}
							//CASE EXCLUSIVE
							//bigger
							if(ab.getReferenceStart()>=es.getSv().getEnd()) {
								excl++; 
								found.add(read.getReadName());
								foundEx.add(read.getReadName());
								break;
							}
						}
						leftFound=false; 
						
		
						//white found
						if(exon.getEnd()==ab.getReferenceStart()+ab.getLength()+1) {
							exonLeftFound=true;
							break; 
						}
						if(exonLeftFound) {
							if(ab.getReferenceStart()==es.getSv().getEnd()) {
								incl++;
								found.add(read.getReadName());
								break;
							}
						}
						
						
						
					}
				}
		}
	

//		
		System.out.println("FOUND BUT SHOULD NOT BE");
		for(String a : foundEx) {
			if(!result.contains(a)) {
				System.out.print(a+",");
				
			}
		}			System.out.println();
//
//		
		System.out.println("NOT FOUND BUT SHOULD BE");
		
		String[] b = result.split(",");
		for(String s : b) {
			if(!foundEx.contains(s)) {
				System.out.println(s);
			}
		}		
//		 System.out.println("---------------FOUND");
//		 for(String a : found) {
//				
//					System.out.println(a);
//					
//				
//			}
		 
		 
		return new Pair<Integer,Integer>(incl,excl); 
	}
	
	public static boolean overlap(ReadPair rp) {
		
		if(rp.getFw().getAlignmentEnd() >= rp.getRw().getAlignmentStart()) {
			return true;
		}
		
		return false; 
	}
	
	public static RegionVector getExons(SAMRecord read) {
		RegionVector rv = new RegionVector();
		for(AlignmentBlock ab: read.getAlignmentBlocks()) {
			rv.getVector().add(new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength()));
			
		}
		return rv;
	}
	
	

	public static RegionVector toRv(SAMRecord r1, SAMRecord r2) {
		
		RegionVector rv = new RegionVector();
		for(AlignmentBlock ab : r1.getAlignmentBlocks()) {
			rv.getVector().add(new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength()));
		}
		for(AlignmentBlock ab : r2.getAlignmentBlocks()) {
			rv.getVector().add(new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength()));
		}
		return rv.merge();
	}
	
	public static boolean isTranscriptomic (SAMRecord fw, SAMRecord rw, Gene gene) {
		 Region g;
		   Region r = new Region(fw.getAlignmentStart(),rw.getAlignmentEnd());
		   boolean currentValue = false; 
			   
				g = new Region(gene.getStart(), gene.getStop());
				//within one gene (part of a gene)
				if(g.getStart() <= r.getStart()  && g.getEnd() >= r.getEnd() && fw.getReferenceName().equals(gene.getChr())) {
					
							
					//System.out.println("GENE "+g.getStart()+"  "+g.getEnd());
					//System.out.println("REGION "+r.getStart()+"  "+r.getEnd());
					boolean tValue = false; 
					boolean result= false; 
					for(Transcript transcript : gene.getTranscripts().values()){
						
						Region t = new Region(transcript.getStart(),transcript.getStop());
						//Inside of a transcript
						if(t.getStart() <= r.getStart()  && t.getEnd() >= r.getEnd() ){
							RegionVector exons_t = transcript.getRegionVectorExons().merge(); 
							RegionVector exons_reads1 = bamAnnotation.getExonsReads(fw).merge();
							RegionVector exons_reads2 = bamAnnotation.getExonsReads(rw).merge();
							//RegionVector introns_reads = exons_reads.inverse();
					


							if(!exons_reads1.isConsistent(exons_t) || !exons_reads2.isConsistent(exons_t) ) {
								currentValue= false;
							}
							
							else {
								
								return true; 
								//result=true;
								//currentValue= true; 
							}
								
						}
					}
				}
				
				return currentValue;
							
						
	}
	
	

	
	private double calcPSI(int incl_count, int excl_count) {
		
		if(incl_count==0) {
			return 0.0;
		}
		return (double)incl_count/(double)(incl_count+excl_count);
	}
	
	public String getGeneId() {
		return geneId;
	}
	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}
	public Region getExon() {
		return exon;
	}
	public void setExon(Region exon) {
		this.exon = exon;
	}
	public int getIncl_count() {
		return incl_count;
	}
	public void setIncl_count(int incl_count) {
		this.incl_count = incl_count;
	}
	public int getExcl_count() {
		return excl_count;
	}
	public void setExcl_count(int excl_count) {
		this.excl_count = excl_count;
	}
	public int getTotal_count() {
		return total_count;
	}
	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
	public double getPsi() {
		return psi;
	}
	public void setPsi(double psi) {
		this.psi = psi;
	}
	
	public RegionVector getExonsRead(SAMRecord fw) {
		
		   RegionVector exons = new RegionVector();
		   for(AlignmentBlock ab: fw.getAlignmentBlocks()){
			   int ref_s  = ab.getReferenceStart();
			   int ref_e = ref_s + ab.getLength();

			   Region g_ref = new Region(ref_s, ref_e);
			   exons.getVector().add(g_ref);
			   //System.out.println(ref_s+" "+ ref_e);
		   }
		   return exons; 
	}
	

	
	
}
