package diffSplicing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import bam.BamRunner;
import bam.bamAnnotation;
import exonSkipping.Annotation;
import exonSkipping.ExonSkipping;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;
import exonSkipping.Utilities;
import exonSkipping.parserGTF;
import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;


public class splicingRunner {

	static boolean test = false; 
	static String o;
	static String gtf;
	static String bam;
	
	
	
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();;

		//------------------------------------Read command Line
		readCommandLine(args);
		
		//--------------------------------------parse GTF 
		Annotation annotationGTF = parserGTF.parse(gtf);
		
		//---------------------------------------read Bam File

		List<ReadPair>reads = readBAM(bam, annotationGTF);

		long stopTime = System.currentTimeMillis();;
		long diff = (stopTime - startTime)/1000;
		System.out.println("Init: "+ diff);
		
		
		//////////////////////////////////SKIPPED EXONS
		if(test) {
			
			Gene gene = annotationGTF.getGeneById("ENSG00000143839.12");
			//System.out.println("GENE start : "+ gene.getStart()+" stop: "+gene.getStop());
			
			/*
			 * Two transcript -> two exon skipping 
			 */
			
//			//watch all transcripts
//			for(Transcript t : gene.getTranscripts().values()) {
//				System.out.println(t.getId());
//				System.out.println(Utils.prettyRegionVector(t.getRegionVectorExons()));
//			}
			
			for(ExonSkipping es : gene.calculateExonSkipping()){	
				for(RegionVector rv : es.getWt()) {
					psiAnnotation psi;
					//System.out.println("----------------------------------------------");
					//print out skipped exon
					//System.out.println("Skipped exon: " + Utils.prettyRegionVector(rv.inverse()));
					
					for(Region r: rv.inverse().getVector()) {
						 Region e = new Region(r.getStart()-1, r.getEnd()-1);

						 psi = new psiAnnotation(gene,es,e,reads, annotationGTF);
						 System.out.println(e.getStart()+"-"+e.getEnd());
						 System.out.println("incl count : "+psi.getIncl_count());
						 System.out.println("excl count : "+psi.getExcl_count());
					}
				}
				

			}
			System.exit(0);	
			
			
		}
		 startTime = System.currentTimeMillis();;
		
		//NORMAL WAY 
		List<psiAnnotation> psiAnnotation = new ArrayList<>();
		Iterator<Entry<String, Gene>> it = annotationGTF.getGenes().entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Gene>pair = (Map.Entry<String, Gene>)it.next();
			Gene myGene= annotationGTF.getGeneById(pair.getKey().toString());
			for(ExonSkipping es : myGene.calculateExonSkipping()){	
				for(RegionVector rv : es.getWt()) {
					psiAnnotation psi;
					for(Region r: rv.inverse().getVector()) {
						 Region e = new Region(r.getStart()-1, r.getEnd()-1);
						
							 psi = new psiAnnotation(myGene,es,e,reads, annotationGTF);
							 psiAnnotation.add(psi);
					}
				}
				
			}
	    }
	 
	    printoutResult(psiAnnotation);
	    System.out.println("ready");
		 stopTime = System.currentTimeMillis();;
		 diff = (stopTime - startTime);
		System.out.println("Init: "+ diff);
	
	} 
	
///////////////////////////////////////////////////////////////
//// HELP FUNCTIONS : READ BAM 
///////////////////////////////////////////////////////////////	
	
	/*
	 * Reads BAM file and saves the reads in ReadPairs.
	 * 
	 * Only selects the ones that have a "paired" read.
	 * 
	 * Selects the ones that have the features needed by check_if_we_can_ignore.
	 */
	public static List<ReadPair> readBAM(String bam, Annotation annotationGTF) {

		HashMap<String, SAMRecord> lookup = new HashMap<String , SAMRecord>();
		HashMap<String, ReadPair> reads = new HashMap<String , ReadPair>();
		SAMFileReader sam_reader = new SAMFileReader(new File(bam),false);
		sam_reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
		String tmpChr= "-1";
	
		//iterate through all reads in bam file
		Iterator<SAMRecord> it = sam_reader.iterator();
		while(it.hasNext()) {

			SAMRecord sr = it.next();
			SAMRecord other_seen = lookup.get(sr.getReadName());
		
			if(tmpChr.equals("-1")) {
				tmpChr=sr.getReferenceName();
			}
			
			if(!tmpChr.equals(sr.getReferenceName())) {
				lookup.clear();
			}
			tmpChr=sr.getReferenceName();

			if(BamRunner.check_if_we_can_ignore(sr, annotationGTF)) {			
	
				continue;
			}else if(other_seen!=null ) {
		
				reads.put(sr.getReadName(), new ReadPair(sr,other_seen));
				continue;

			}else {
				
				lookup.put(sr.getReadName(), sr);
			}
		}

		// now i have read pairs
		List<ReadPair> list = new ArrayList<ReadPair>();
		for(ReadPair rp : reads.values()) {
			//if(isTranscriptomic(rp.getFw() ,rp.getRw() ,  annotationGTF)){
				list.add(new ReadPair(rp.getFw(),rp.getRw()));
				//list.add(rp.getRw());
			//}
		}
		sam_reader.close();
		return list; 
	}

///////////////////////////////////////////////////////////////
////HELP FUNCTION : IS TRANSCRIPTOMIC
///////////////////////////////////////////////////////////////	
	
	
	public static boolean isTranscriptomic(SAMRecord fw, SAMRecord rw, Annotation GTFannotation) {
		   Region g;
		   Region r = new Region(fw.getAlignmentStart(),rw.getAlignmentEnd());
		   boolean currentValue = false; 
		   for(Gene gene :  GTFannotation.getGenes().values() ) {
			   
				g = new Region(gene.getStart(), gene.getStop());
				//within one gene (part of a gene)
				if(g.getStart() <= r.getStart()  && g.getEnd() >= r.getEnd() && fw.getReferenceName().equals(gene.getChr())) {
					
					
					
					for(Transcript transcript : gene.getTranscripts().values()){

						Region t = new Region(transcript.getStart(),transcript.getStop());
						//Inside of a transcript
						if(t.getStart() <= r.getStart()  && t.getEnd() >= r.getEnd() ){
							RegionVector exons_t = transcript.getRegionVectorExons().merge(); 
							RegionVector exons_reads1 = bamAnnotation.getExonsReads(fw).merge();
							RegionVector exons_reads2 = bamAnnotation.getExonsReads(rw).merge();

							if(!exons_reads1.isConsistent(exons_t) || !exons_reads2.isConsistent(exons_t) ) {
								currentValue=false;
							}
							else {

								currentValue=true;
								return true; 
							}
								
						}
							
					}			
					}
				
				}
						
		   		return currentValue; 
	}	
///////////////////////////////////////////////////////////////
//// FUNCTION FOR TESTING
///////////////////////////////////////////////////////////////	
	
	public static void printoutReads(List<SAMRecord> list) {
		File filename = new File("/Users/luisasantus/Desktop/reads.txt");
		FileWriter fos;
		
		
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);


			for(SAMRecord s : list) {
				//if(psi.getTotal_count()>0) {
				dos.print(s.getReadName()+"\t");
				
				String prefix= "";
				for(AlignmentBlock ab : s.getAlignmentBlocks()) {
					int end  = ab.getReferenceStart()+ab.getLength();
					dos.print(prefix+ab.getReferenceStart()+"-"+ end);
					prefix="|";
				}
				
				dos.print("\n");
				//}
			}

			dos.print("\n");
			fos.close();
			dos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////
////PRINT OUT FINAL RESULT
///////////////////////////////////////////////////////////////
	public static void printoutResult(List<psiAnnotation> list) {
		File filename = new File(o);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);

			dos.println("gene\texon\tnum_incl_reads\tnum_excl_reads\tnum_total_reads\tpsi");

			for(psiAnnotation psi : list) {
				if(psi.getTotal_count()>0) {
				dos.print(psi.getGeneId()+"\t");
				int end = psi.getExon().getEnd()+1;
				dos.print(psi.getExon().getStart()+"-"+end+"\t");
				dos.print(psi.getIncl_count()+"\t");
				dos.print(psi.getExcl_count()+"\t");
				dos.print(psi.getTotal_count()+"\t");
				dos.print(psi.getPsi());
				dos.print("\n");
				}
			}

			dos.print("\n");
			fos.close();
			dos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
///////////////////////////////////////////////////////////////
//// COMMAND LINE 
///////////////////////////////////////////////////////////////

	public static void readCommandLine(String[] args) {

		try {
			Options options = new Options();
			options.addOption("frstrand", false, "If not given the experiment\n" +
					"is strand unspecific, if true the first read maps sense to the transcribed region, if false the\n" +
					"first read maps antisense to the transcribed region.");
			options.addOption("bam", true, "bam file path");
			options.addOption("gtf", true, "gtf file");
			options.addOption("o", true, "output file path");



			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);

			// TODO check if all param!
			if (cmd.hasOption("o") && cmd.hasOption("bam") && cmd.hasOption("gtf") ){

				o = cmd.getOptionValue("o");
				bam = cmd.getOptionValue("bam");
				gtf=cmd.getOptionValue("gtf");

			}
			else{
				System.out.println();
				System.out.println("The programm was invoked with wrong parameters");
				System.out.println();
				System.out.println("Correct Usage: ");
				System.out.println("\t \t -bam <path/to/your/bamfile>");
				System.out.println("\t \t -gtf <path/to/your/gtffile>");
				System.out.println("\t \t -o <path/to/your/outputfile>");

				System.exit(0);

			}

	} catch (ParseException e) {
		System.err.println("Error Reading the command line parameters!");
		e.printStackTrace();
	}



}
}
