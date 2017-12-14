package bamFeatures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.samtools.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import augmentedTree.IntervalTree;
import augmentedTree.KeyedIntervalForest;
import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.parserGTF;

public class Runner  {

	static String o;
	static String gtf;
	static String bam;
	static String frstrand;
	static HashMap<String, bamAnnotation> bamAnnotation  = new HashMap<String, bamAnnotation>();
	static Annotation GTFannotation;
	//static KeyedIntervalForest<Gene ,String> lookup;


	public static void main(String[] args) {

		//Read command Line
		readCommandLine(args);


		//GTF file annotation
		GTFannotation = parserGTF.parse(gtf);
		//GTFannotation.getGenes().values().forEach((g)->g.getRegionVectorTranscripts().getVector().forEach((r)->System.out.println(r.getStart()+"-"+r.getEnd()+";   ")));
		//System.exit(0);
		System.out.println("-----------------------------");

		//IntervalTree<Gene> intervalTree = new IntervalTree<Gene>();
//        lookup = new KeyedIntervalForest<Gene,String>( (g)-> g.getChr(), (g)->g.getStart(), (g)->g.getStop());
//        List<Gene> list = new ArrayList<Gene>(GTFannotation.getGenes().values());
//        Collections.sort(list);
//        for(Gene gene : list ) {
//        	  System.out.println("CHR"+ gene.getChr()+"START "+gene.getStart()+ "-STOP: "+gene.getStop());
//        	  lookup.factory.tree.add(gene);
//        	  System.out.println("lookup size: "+lookup.factory.tree.size());
//
//        }


        //System.out.println("-----------------------------");
        //System.out.println(getGenes("XIV",694361,695188));
        //System.out.println("-----------------------------");

		//readBam
		//System.out.println("/....");
		readBAM();
		//print results
		printoutResult();
	}


	public static void readBAM() {

		HashMap<String, SAMRecord> lookup = new HashMap<String , SAMRecord>();

		SAMFileReader sam_reader = new SAMFileReader(new File(bam),false);
		sam_reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
		Iterator<SAMRecord> it = sam_reader.iterator();
		int c = 0;
		int skipped=0;
		int tot=0;
		PrintWriter dos;
		bamAnnotation ba= null;
		String tmpChr= "-1";
		while(it.hasNext()) {


			SAMRecord sr = it.next();
			SAMRecord other_seen = lookup.get(sr.getReadName());
			if(tmpChr.equals("-1")) {
				tmpChr=sr.getReferenceName();
			}
			//CASE: We have a read pair;
			if(other_seen!=null) {
				c++;


				if(sr.getReadName().equals("4778753") ||sr.getReadName().equals("1093326")  ) {

					ba=new bamAnnotation(sr,lookup.get(sr.getReadName()),GTFannotation);
					System.out.print(sr.getReadName()+"\t");
					System.out.print(ba.getMm()+"\t");
					System.out.print(ba.getClippingCount()+"\t");
					System.out.print(ba.getSplitCount()+"\t");
					System.out.print(ba.getGeneCount()+"\t");
					System.out.println();
//					Iterator i = sr.getAlignmentBlocks().iterator();
//					while(i.hasNext()){
//						//AlignmentBlock ab= (AlignmentBlock) i.next();
//						//System.out.println(ab.getReferenceStart()+" "+ab.getReadStart()+" "+ab.getLength());
//						//AlignmentBlock ab1= lookup.get(sr.getReadName()).getAlignmentBlocks().get(0);
//						//System.out.println(ab1.getReferenceStart()+" "+ab1.getReadStart()+" "+ab1.getLength());
//						System.out.println();
//						//GTFannotation.
//					}


				}


				//dos.print(ba.getReadId()+"\t");
				//dos.print("mm: "+ba.getMm()+"\t");
				//dos.print("clipping: "+ba.getClippingCount()+"\t");
//				if(ba.getSplitCount() ==-1 ) {
//					dos.print("split-inconsistent:true");
//				}else {
//					dos.print("nsplit:"+ba.getSplitCount());
//				}
//				dos.print(ba.getGeneCount());
//
//
//				dos.print("gdist:"+ba.getGeneCount());
//				dos.print("antisense"+ba.isAntisense());
//				dos.print("pcrindex:"+ba.getPcrindex());
				//dos.print("\n");


			}
			if(check_if_we_can_ignore(sr)) {
				skipped++;
				continue;
			}else {
				lookup.put(sr.getReadName(), sr);
			}

			//Clear lookup if chromosome changes
			if(!tmpChr.equals(sr.getReferenceName())) {
				lookup.clear();
			}
			tmpChr=sr.getReferenceName();



		}

		System.out.println("COUNT "+c);
		System.out.println("SKIPEPD "+skipped);



	}


	public static boolean check_if_we_can_ignore(SAMRecord sr) {

		Region r= new Region(Math.min(sr.getAlignmentStart(),sr.getMateAlignmentStart()),Math.max(sr.getAlignmentStart(),sr.getMateAlignmentStart()));
		//IF UNMAPPED
		if(sr.getReadUnmappedFlag()) { return true; }
		//IF SECONDARY OR SUPPLEMENTARY
		if(sr.getNotPrimaryAlignmentFlag()){ return true; }
		//IF MATE UNMAPPED
		if(sr.getMateUnmappedFlag()) {return true;}
		//IF READ ON SAME STRAND
		if(sr.getReadNegativeStrandFlag()==sr.getMateNegativeStrandFlag() ||  sr.getReferenceName()!=sr.getMateReferenceName()) { return true;}
		//Check if integenic
		if(strangeGenomicRegion(r)) {return true;}
		//Different chromosomes
		return false;
	}
	public static void printoutResult() {
		File filename = new File(o);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);

			for(String key : bamAnnotation.keySet()){
				dos.print(key+"\t");
				dos.print("mm:"+bamAnnotation.get(key).getMm()+"\t");
				dos.print("clipping:"+bamAnnotation.get(key).getClippingCount()+"\t");
				dos.print("nsplit:"+bamAnnotation.get(key).getSplitCount()+"\t");
				//to do
				dos.print("antisense:"+bamAnnotation.get(key).getSplitCount()+"\t");
				//todo
				dos.print("pcrindex:"+0+"\t");


				dos.print("\n");
				System.out.print(key+"\t");
				System.out.println(bamAnnotation.get(key).getMm()+"\t");
				System.out.println(bamAnnotation.get(key).getClippingCount()+"\t");
				System.out.println(bamAnnotation.get(key).getSplitCount()+"\t");
				System.out.println("gcount:"+bamAnnotation.get(key).getGeneCount()+"\t");

			}

			fos.close();
			dos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}


//	if(last == null) {
//		last =g;
//		continue;
//	}

//	else if(g.getStart() >= r.getStart() && g.getEnd() >= r.getEnd()){
//	cog= true;
//}
//else if(g.getStart() >= r.getStart() && g.getStop() <= r.getEnd() ){
//	cog= true;
//}

//if(last.getEnd() <= r.getStart() && g.getStart() >= r.getStart()) {
//	b2g=true;
//}


//last =g;
//	if(g.overlap(r)>0) {
//	cog=true;
//}

//	Vector<Region> v = new Vector<Region>();
//	RegionVector a = new RegionVector(v);
//	for(Gene g : GTFannotation.getGenes().values()) {
//		v.add(new Region(g.getStart(), g.getStop()));
//	}
//
//	Collections.sort(v,a.getComparator());

	public static boolean strangeGenomicRegion(Region r) {

		//conains genes and not part of any
		boolean wog= false;
		boolean cog=false;
		Region g ;
		for(Gene gene :  GTFannotation.getGenes().values() ) {

			g = new Region(gene.getStart(), gene.getStop());

			//within one gene (part of a gene)
			if(g.getStart() <= r.getStart() && g.getEnd() >= r.getEnd()) {
				wog=true;
				//intergenic read
			}

			//contains one gene (includes at least one gene)
			else if(g.getStart() >= r.getStart() && g.getEnd() <= r.getEnd()) {
				cog =true;
			}

		}

		if( !wog && cog ) {
			return true;
		}
		return false;
	}

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

//	public static Iterator<Gene> getGenes(String chr, int start, int stop ){
//
//		//Collection<Gene> c = lookup.getIntersecting(chr,start,stop) ;
//		//System.out.println(c.size());
//		return  c.iterator();
//	}
}

