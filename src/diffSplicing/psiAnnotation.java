package diffSplicing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

public class psiAnnotation {

	private String geneId ; 
	private Region exon; 
	private int incl_count ; 
	private int excl_count; 
	private int total_count; 
	private double psi ;
	final String result = "7022,7043,6991,6971,6970,6896,6929,6909,7019,6941,6988,6987,6924,6926,6947,7010,6980,7034,6984,7075,7030,6916,6974,6996,6930,6977,6976,6978,6937,6914";

	String prova = "7075";
	
	public psiAnnotation(Gene gene, ExonSkipping es , Region exon, List<ReadPair> reads, Annotation annotationGTF) {
		super();
		this.geneId = gene.getId();
		this.exon = exon;
		//Pair<Integer, Integer> counts = getCounts(gene,es,reads, annotationGTF); 
		Pair<Integer, Integer> counts = get_inclusive_and_exclusive_count(gene,exon,reads, annotationGTF); 
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
	
	/*
	 * calculates inclusive and exclusive count.
	 * 
	 * How : 
	 * 
	 * iterate through gene's transcript. See if reads are : 
	 * -consistent to transcript
	 * -overlapping transcript
	 * 
	 */
	public Pair<Integer,Integer> get_inclusive_and_exclusive_count(Gene gene,Region exon,List<ReadPair> reads, Annotation annotationGTF ) {
		//System.out.println("Calculating inclusive and exclusive count....");
		List<String> foundIn = new ArrayList<String>();
		List<String> foundEx = new ArrayList<String>();

		 
		List<String> toDelete = new ArrayList<String>();

		int n_inc = 0; 
		int n_exc=0;
		//----- Differentiate transcripts
		List<Transcript> inclusive = new ArrayList<Transcript>();
		List<Transcript> exclusive = new ArrayList<Transcript>();
		for(Transcript transcript : gene.getTranscripts().values()) {			
			//differentiate between "INCLUSIVE" transcripts and "EXCLUSIVE" transcripts
			if(transcript.getRegionVectorExons().contains(exon)) {
				inclusive.add(transcript);
				//System.out.println("INCLUSIVE");
				//System.out.println(Utilities.prettyRegionVector(transcript.getRegionVectorExons()));
			}else {
				exclusive.add(transcript);
				//System.out.println("EXCLUSIVE");
				//System.out.println(Utilities.prettyRegionVector(transcript.getRegionVectorExons()));

			}
		}

		
		//ONLY PICK RELEVANT READS
		List<ReadPair> relevantReads = new ArrayList<ReadPair>();
		for (ReadPair rp : reads) {
			if(!may_be_relevant(rp.getFw(),rp.getRw(),gene)) {
				continue;
			}else {
				relevantReads.add(rp);
			}	
		}
		
	
		
//--------------------------------
		Set<String> incReads = new HashSet<String>();
		int isInclusive = 0;
		for(Transcript t_i : inclusive) {
			for (ReadPair rp : relevantReads) {
				int iter =0;
				int i= 0 ;
				SAMRecord tmpRead = null;
				iterRead: while(iter<2) {
					//select right read of pair
					SAMRecord read;
					if(iter == 0) {read=rp.getFw();} else {read=rp.getRw();} iter++;
					if(can_come_from_transcript(read,t_i)) {
						i++;
					
				    
					}
//					if(read.getReadName().equals(prova)) {
//						System.out.println("READ");
//						Utilities.printRead(read);
//					}
					if(!t_i.getRegionVectorExons().isTranscriptomic(getExons(read))){
						// READ IS NOT TRANSCRIPTOMIC TO TRANSCRIPT 
						//toDelete.add(read.getReadName());
						break iterRead;
					}
					//IF BOTH Reads can come from transcript and one actually overlaps the exon
				
					if(i==2) {
						RegionVector read_rv = getExons(read);
						RegionVector read_rv_tmp = getExons(tmpRead);
						RegionVector exon_rv = new RegionVector();
						exon_rv.getVector().add(exon);
						if(read_rv.overlap(exon_rv) || read_rv_tmp.overlap(exon_rv)) {
							isInclusive ++ ; 
							incReads.add(read.getReadName());
							continue; 
						}
						for(Transcript t: exclusive) {
							Region hola = new Region(Integer.min(read.getAlignmentStart(), tmpRead.getAlignmentStart()), Integer.max(read.getAlignmentEnd(), tmpRead.getAlignmentEnd()));
							
							if(hola.contains(exon)) {
							if(!can_come_from_transcript(read,t) ||!can_come_from_transcript(tmpRead,t) ) {
								
									incReads.add(read.getReadName());
								//System.out.println("HOlA "+ read.getReadName());
								//Utilities.printRead(read);
								//toDelete.add(read.getReadName());
							 }
								
							}
						}
					}
					tmpRead = read; 
				}
				
			
			}
			
			//System.out.println(t_i.getId() +" has inclusive reads number "+incReads.size());
		}
		
		
	//----------------------------------
		
		Set<String> excReads = new HashSet<String>();
		int isExclusive = isInclusive;
		for(Transcript t_i : exclusive) {
			for (ReadPair rp : relevantReads) {
				int iter =0;
				int i= 0 ; 
				SAMRecord tmpRead = null; 
				iterRead: while(iter<2) {
					//select right read of pair
					SAMRecord read;
					if(iter == 0) {read=rp.getFw();} else {read=rp.getRw();} iter++;
					if(can_come_from_transcript(read,t_i)) {
						i++;
						
//						if(read.getReadName().equals(prova)) {
//							System.out.println("can come from exclusive");
//						}
				    }
					//maybe cancel
//					RegionVector exPlusExon = new RegionVector();
//					exPlusExon.getVector().addAll(t_i.getRegionVectorExons().getVector());
//					exPlusExon.getVector().add(exon);
//					exPlusExon.getVector().sort(null);
//					if(read.getReadName().equals(prova)) {
//						System.out.println(Utilities.prettyRegionVector(exPlusExon));
//					}
//					
//					if(!exPlusExon.isTranscriptomic(getExons(read))){
//						// READ IS NOT TRANSCRIPTOMIC TO TRANSCRIPT 
//						//toDelete.add(read.getReadName());
//						break iterRead;
//					}
					//IF BOTH Reads can come from transcript and one actually overlaps the exon
					if(i==2) {

						Region hola = new Region(Integer.min(read.getAlignmentStart(), tmpRead.getAlignmentStart()), Integer.max(read.getAlignmentEnd(), tmpRead.getAlignmentEnd()));
						
						if(hola.contains(exon)) {
							if(tmpRead.getAlignmentEnd()<read.getAlignmentStart()) {
								Region reg = new Region(tmpRead.getAlignmentEnd(),read.getAlignmentStart());
								if(reg.contains(exon)) {
							
									//PROVA
									for(Transcript t: inclusive) {
										if(can_come_from_transcript(read,t)) {
											if(can_come_from_transcript(tmpRead,t)) {
												
											//System.out.println("HOlA "+ read.getReadName());
											//Utilities.printRead(read);
											//toDelete.add(read.getReadName());
											continue iterRead;	
											}
										}
									}
									//----
									
															
								}
							}
							
						
								excReads.add(read.getReadName());
							

							
						}
													
					}
					tmpRead=read; 
				}
				
			
			}
			
			//System.out.println(t_i.getId() +" has exclusive reads number "+excReads.size());
		}
		
		
		// ----------------------------------------------------------------------
		Set<String> intersection = new HashSet<String>(incReads);
		intersection.retainAll(excReads);
		
		incReads.removeAll(intersection);
		excReads.removeAll(intersection);
		excReads.removeAll(toDelete);
		
		n_inc = incReads.size();
		n_exc = excReads.size();
		

//		System.out.println("FOUND BUT SHOULD NOT BE");
//		for(String a : excReads) {
//			if(!result.contains(a)) {
//				System.out.print(a+",");
//				
//			}
//		}			System.out.println();
//
//		
//		System.out.println("NOT FOUND BUT SHOULD BE");
//		
//		String[] b = result.split(",");
//		for(String s : b) {
//			if(!excReads.contains(s)) {
//				System.out.println(s);
//			}
//		}	
		return new Pair<Integer,Integer>(n_inc,n_exc); 
	}
	

	
	
	public boolean can_come_from_transcript(SAMRecord read ,Transcript transcript) {
		
		RegionVector transcript_cut = transcript.getRegionVectorExons().cut(read.getAlignmentStart(), read.getAlignmentEnd());
		RegionVector read_rv = getExons(read).merge();
	
		RegionVector exon_rv = new RegionVector();
		exon_rv.getVector().add(exon);

			if(transcript_cut.getNumberRegion()>0 && transcript_cut.equals(read_rv) ) {
				//if(read_rv.overlap(exon_rv)) {
						return true;
			    //}	
			}
		
		return false; 
		
	}
	
	
	public static RegionVector getExons(SAMRecord read) {
		RegionVector rv = new RegionVector();
		
		for( AlignmentBlock ab : read.getAlignmentBlocks()) {
			rv.getVector().add(new Region(ab.getReferenceStart(), ab.getReferenceStart()+ab.getLength()-1));	
		}
		return rv; 
	}

	//////////////////////////////////////
	// HELP METHODS
	/////////////////////////////////////
	
	/*
	 * Controls if a Read Pair is consistent to ALL transcripts of the gene.
	 * 
	 * 
	 */
	

	public static boolean may_be_relevant(SAMRecord fw, SAMRecord rw, Gene gene) {
		Region g;
		Region r = new Region(fw.getAlignmentStart(),rw.getAlignmentEnd());
		//In Gene 
		g = new Region(gene.getStart(), gene.getStop());
		if(!(g.getStart() <= r.getStart())  ||  !(g.getEnd() >= r.getEnd()) || !(fw.getReferenceName().equals(gene.getChr()))) {
			return false; 
		}
		
		
		return true; 
	}
	
	
	public static boolean isTranscriptomic (SAMRecord fw, SAMRecord rw, Gene gene) {
		   Region g;
		   Region r = new Region(fw.getAlignmentStart(),rw.getAlignmentEnd());
		   boolean currentValue = false; 
			   
				//within one gene (part of a gene)
		        g = new Region(gene.getStart(), gene.getStop());
				if(g.getStart() <= r.getStart()  && g.getEnd() >= r.getEnd() && fw.getReferenceName().equals(gene.getChr())) {
			
					//checks all transcripts of gene in question 
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
								//return false; 
							}
							else {
								currentValue=true;
								return true; 
							}
								
						}
					}
				}
				return currentValue;						
	}
	
	
	
	//////////////////////////////////////
	// GETTER AND SETTERS
	/////////////////////////////////////
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
	
}
