package featureCounting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import featureCounting.bamAnnotation;
import diffSplicing.ReadPair;
import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Utilities;
import exonSkipping.parserGTF;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import readSimulator.Utils;

public class RunnerFeatureCounting {
	
	static boolean test = false; 
	static String o;
	static String gtf;
	static String bam;
	static Annotation GTFannotation;
	static String frstrand;

	static HashMap<String, bamAnnotation> annotation  = new HashMap<String, bamAnnotation>();
	
	static HashMap<RegionVector,List<String>> mapGRV = new  HashMap<RegionVector,List<String>>();

	
	///////////////////////////////////////////////////////
	// 		MAIN
    /////////////////////////////////////////////////////
	public static void main(String[] args) {

	
		//Read command Line
		readCommandLine(args);

		//GTF file annotation
		GTFannotation = parserGTF.parse(gtf);
		System.out.println("GTF parsed!");
		
		//Parse the BAM file and extract information
		readBAM(bam,GTFannotation);
		
		System.out.println("BAM READ");
		//print results
		printoutResult();
		
		System.out.println("Ready!");
	}
	
	///////////////////////////////////////////////////////
	// 		READ BAM FILE
    /////////////////////////////////////////////////////
	
	// this is in outut file but should not be : 964785 ( more )
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

			if(check_if_we_can_ignore(sr, annotationGTF)) {			
				continue;
			}else if(other_seen!=null ) {
				
				//check if they are consistent
		
		

			

				bamAnnotation ba=new bamAnnotation(sr,lookup.get(sr.getReadName()),GTFannotation,frstrand);
				annotation.put(sr.getReadName(),ba);
				
//				
				if(other_seen.getReadName().equals("7800682")) {
					System.out.println("Read information about "+ other_seen.getReadName());
					Utilities.printRead(sr);
					Utilities.printRead(other_seen);
					System.out.println(Utils.prettyRegionVector(ba.getGrv()));
					if(!bamAnnotation.check_if_split_inconsistent(bamAnnotation.getExonsReadsMod(sr),bamAnnotation.getExonsReadsMod(other_seen) )) {
						System.out.println("SPLIT INCONSISTENT!!");
					}
					
				}
//				
//				if(ba.getGrv().getStart() ==81043  && ba.getGrv().getStop() == 81176 ) {
//					System.out.println("Same for PCR");
//					System.out.println(sr.getReadName());
//					Utilities.printRead(sr);
//					Utilities.printRead(other_seen);
//					System.out.println(Utils.prettyRegionVector(ba.getGrv()));
//				}
				
				
				//PCR 
				if(!splitInc(sr,other_seen)) {

				
			//	if(bamAnnotation.getExonsReadsMod(sr).isConsistent(bamAnnotation.getExonsReadsMod(other_seen))) {
					if(mapGRV.containsKey(ba.getGrv())) {
						ba.setPcrindex( mapGRV.get(ba.getGrv()).size());
						mapGRV.get(ba.getGrv()).add(sr.getReadName());
					}else {
						ba.setPcrindex(0);
						List<String> tmpList = new ArrayList<String>();
						tmpList.add(sr.getReadName());
						mapGRV.put(ba.getGrv(),tmpList);	
					}
				
				}
				
				reads.put(sr.getReadName(), new ReadPair(sr,other_seen));
				continue;
			}else {
				lookup.put(sr.getReadName(), sr);
			}
		}

		// now i have read pairs
		List<ReadPair> list = new ArrayList<ReadPair>();
		for(ReadPair rp : reads.values()) {
				list.add(new ReadPair(rp.getFw(),rp.getRw()));
		}
		sam_reader.close();
		
		return list; 
	}

	
	
	   public static boolean splitInc(SAMRecord fw, SAMRecord rw) {

		   RegionVector exons2 = bamAnnotation.getExonsReads(rw);
		   RegionVector exons1 = bamAnnotation.getExonsReads(fw);

		   RegionVector introns1 = new RegionVector();
		   RegionVector introns2 = new RegionVector();
		   
		   introns1.getVector().addAll(exons1.merge().inverse().getVector());

		   for(Region intron : exons2.merge().inverse().getVector()) {
			   if(!introns1.getVector().contains(intron)){
				   introns2.getVector().add(intron);
			   }
		   }
		   boolean split_inc = bamAnnotation.check_if_split_inconsistent(exons1,exons2);
		  
		   if(fw.getReadName().equals("5104697")) {
			   System.out.println("EVEN HERE SPLIT INC");
		   }
		   
		   if(split_inc){
			   return true; 
			  
		   }
		   return false; 
		   
	   }
	public static boolean check_if_we_can_ignore(SAMRecord sr, Annotation GTFannotation) {
		Region r= new Region(Math.min(sr.getAlignmentStart(),sr.getMateAlignmentStart()),Math.max(sr.getAlignmentStart(),sr.getMateAlignmentStart()));
		//IF UNMAPPED
		if(sr.getReadUnmappedFlag()) { 	
		return true; }
		if(sr.getNotPrimaryAlignmentFlag()){
		return true; }
		//IF MATE UNMAPPED
		if(sr.getMateUnmappedFlag()) {			
		return true;}
		//IF READ ON SAME STRAND
		if(sr.getReadNegativeStrandFlag()==sr.getMateNegativeStrandFlag() &&  sr.getReferenceName().equals(sr.getMateReferenceName())) { 
		return true;}
		//Check if integenic
		if(strangeGenomicRegion(r, sr.getReferenceName(), GTFannotation)) {
		return true;}
		
		return false;
	}
	
	public static boolean strangeGenomicRegion(Region r, String chr, Annotation GTFannotation ) {

		//conains genes and not part of any
		boolean wog= false;
		boolean cog=false;
		Region g ;
		for(Gene gene :  GTFannotation.getGenes().values() ) {

			g = new Region(gene.getStart(), gene.getStop());

			//within one gene (part of a gene)
			if(g.getStart() <= r.getStart() && g.getEnd() >= r.getEnd() && chr.equals(gene.getChr())) {
				wog=true;
				//intergenic read
			}

			//contains one gene (includes at least one gene)
			else if(g.getStart() >= r.getStart() && g.getEnd() <= r.getEnd()  && chr.equals(gene.getChr())) {
				cog =true;
			}

		}

		if( !wog && cog ) {
			return true;
		}
		return false;
	}
	
	///////////////////////////////////////////////////////
	// 		PRINT THE RESULTS IN THE RIGHT FORMAT 
    /////////////////////////////////////////////////////
	
	public static void printoutResult() {
		File filename = new File(o);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);

			for(String key : annotation.keySet()){
				dos.print(key+"\t");
				if(annotation.get(key).isSplitInconsistent()) {
					dos.print("split-inconsistent:true");
					dos.print("\n");
					continue;
				}
				dos.print("mm:"+annotation.get(key).getMm()+"\t");
				dos.print("clipping:"+annotation.get(key).getClippingCount()+"\t");
				dos.print("nsplit:"+annotation.get(key).getSplitCount()+"\t");
				dos.print("gcount:"+annotation.get(key).getGeneCount()+"\t");
				if(annotation.get(key).getGeneCount() == 0) {
					dos.print("gdist:"+annotation.get(key).getGdist()+"\t");
					dos.print("antisense:"+annotation.get(key).isAntisense()+"\t");
				}else {
					dos.print(annotation.get(key).getStringFromMap(annotation.get(key).getMapT())+"\t");
				}
				dos.print("pcrindex:"+annotation.get(key).getPcrindex()+"\t");
				dos.print("\n");
			}
			fos.close();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	///////////////////////////////////////////////////////
	// 		READ COMMAND LINE PARAMS
    /////////////////////////////////////////////////////
	
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

				if(cmd.hasOption("frstrand")) {
					frstrand=cmd.getOptionValue("frstrand");
				}else {
					frstrand="undefined";
				}
			}
			else{
				System.out.println();
				System.out.println("The programm was invoked with wrong parameters");
				System.out.println();
				System.out.println("Correct Usage: ");
				System.out.println("\t \t -frstrand If not given the experiment\\n\" + \n" +
						"					\"is strand unspecific, if true the first read maps sense to the transcribed region, if false the\\n\" + \n" +
						"					\"first read maps antisense to the transcribed region");
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
