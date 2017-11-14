package readSimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import exonSkipping.Annotation;



public class Runner {

	public static Annotation GTFannotation= new Annotation();


	static int length;
	static double frlength;
	static double SD;
	static String readcounts;
	static double mutationrate;
	static String fasta;
	static String fidx;
	static String gtf;
	static String od;
	static int nMut;


	public static void main(String[] args) {

		//PARSE COMMAND LINE
		readCommandLine(args);

		//PARSE THE GTF FILE
		exonSkipping.parserGTF.parse(gtf);

		//Genome Sequence Extractor
		GenomeSequenceExtractor ge = new GenomeSequenceExtractor(new File(fasta), new File(fidx));

		// SAVE ALL FRAGMENTS AND READS
		ReadCreator rc = new ReadCreator(GTFannotation, ge,length, frlength, SD, readcounts,mutationrate,  nMut);

		//PRINT RESULT FILE
		printInfos("read.mappinginfo", rc.getFragments());


		//PRINT FASTAQ FILE

//		HashMap<String,String> RW = new HashMap();
//		RW.put("id1", "ABC");
//		RW.put("id2", "ALJHLKH");
//		printFASTAQ("fw.fastq", RW);
//		printFASTAQ(" rw.fastq", BW);


	}






 public static void printInfos(String fname, HashMap<String,Fragment> fragments) {

	 File outputfile= new File(od+fname);

	  try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("readid"+"\t"+"chr_id"+"\t"+"gene_id"+"\t"+"transcript_id"+"\t"+"t_fw_regvec"+"\t"+"t_rw_regvec"+"fw_regvec"+"\t"+"rw_regvec"+"\t"+"\t"+"fw_mut"+"\t"+"rw_mut");


			for(String key: fragments.keySet()) {

				Fragment f = fragments.get(key);
				//ID
				dos.print(key+"\t");
				//CHROMSOME
				dos.print(f.getChr()+"\t");
				//GENE ID
				dos.print(f.getGeneID()+"\t");
				//TRANSCRIPT ID
				dos.print(f.getTranscriptID()+"\t");

				// here retrieve the reads
				Read RW = f.getRW();
				Read FW = f.getFW();

				// T RW
				dos.print(RW.getTstart()+"-"+RW.getTstop()+"\t");
				//T FW
				dos.print(FW.getTstart()+"-"+FW.getTstop()+"\t");


				//RW
				dos.print(Utils.prettyRegionVector(RW.getGenPos())+"\t");

				//FW
				dos.print(Utils.prettyRegionVector(FW.getGenPos())+"\t");

				//FW MUT
				dos.print(Utils.prettyMutations(FW.getSequence().getPositions())+"\t");

				//RW MUT
				dos.print(Utils.prettyMutations(RW.getSequence().getPositions())+"\t");

			}


			fos.close();
			dos.close();


	  } catch (IOException e) {
			System.out.println("Error Printing FASTAQ Delimited File");
		}




 }

  public static void printFASTAQ(String fname, HashMap<String,String> map) {

	  //POSSIBLE ERROR : check if odends with / or not
	  File outputfile= new File(od+fname);

	  try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			for(String id: map.keySet()){
				dos.println("@"+ id);
				String sequence = map.get(id);
				dos.println(sequence);
				dos.println("+"+id);
				StringBuilder qualityScore = new StringBuilder();
				for(int i=0 ; i< sequence.length(); i++) {
					qualityScore.append("~");
				}
				dos.println(qualityScore.toString());

			}


			fos.close();
			dos.close();

	  } catch (IOException e) {
			System.out.println("Error Printing FASTAQ Delimited File");
		}
  }




   public static void readCommandLine(String[] args) {

		try {
			Options options = new Options();
			options.addOption("length", true, " read length");
			options.addOption("frlength", true, "fragment length distribution");
			options.addOption("SD", true, "standard deviation");
			options.addOption("readcounts", true, "table of gene_id, transcript_id, count tuples");
			options.addOption("mutationrate", true, "mutation rate in percent (between 0.0 and 100.0):");
			options.addOption("fasta", true, "genome FASTA file");
			options.addOption("fidx", true, "genome FASTA file index");
			options.addOption("gtf", true, "annotation file for the transcript locations");
			options.addOption("od", true, "output directory path");



			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);

			// TODO check if all param!
			if (cmd.hasOption("od") ){
				length = Integer.parseInt(cmd.getOptionValue("length"));
				frlength = Integer.parseInt(cmd.getOptionValue("frlength"));
				SD=Double.parseDouble(cmd.getOptionValue("SD"));
				readcounts= cmd.getOptionValue("readcounts");
				mutationrate= Double.parseDouble(cmd.getOptionValue("mutationrate"));
				fasta= cmd.getOptionValue("fasta");
				fidx= cmd.getOptionValue("fidx");
				gtf= cmd.getOptionValue("gtf");
				od= cmd.getOptionValue("od");
				nMut = (int) ((mutationrate*length)/100);

			}
			else{
				System.out.println();
				System.out.println("The programm was invoked with wrong parameters");
				System.out.println();
				System.out.println("Correct Usage: ");
				System.out.println("\t \t -length readlength");
				System.out.println("\t \t -frlength fragmentlength");
				System.out.println("\t \t -SD standard deviation ");
				System.out.println("\t \t -readcounts table of gene_id, transcript_id, count tuples (PathTO)");
				System.out.println("\t \t -mutationrate mutationrate");
				System.out.println("\t \t -fasta <path/to/your/fastafile>");
				System.out.println("\t \t -fidx <path/to/your/genomefastafileindex>");
				System.out.println("\t \t -gtf <path/to/your/gtffile>");
				System.out.println("\t \t -od <path/to/your/outputdirectory>");

				System.exit(0);

			}

	} catch (ParseException e) {
		System.err.println("Error Reading the command line parameters!");
		e.printStackTrace();
	}
	}



	}
