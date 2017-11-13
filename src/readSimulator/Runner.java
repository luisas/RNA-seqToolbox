package readSimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exonSkipping.Annotation;
import exonSkipping.Region;
import exonSkipping.RegionVector;


public class Runner {

	public static Annotation GTFannotation= new Annotation();


	static int length;
	static int frlength;
	static double SD;
	static String readcounts;
	static double mutationrate;
	static String fasta;
	static String fidx;
	static String gtf;
	static String od;


	public static void main(String[] args) {

		readCommandLine(args);

		//PARSE THE GTF FILE
		exonSkipping.parserGTF.parse(gtf);

		//PARSE THE READCOUNT FILE
		HashMap<String,HashMap<String, Integer>> readCounts = Utils.parseReadCount(readcounts);

		//Genome Sequence Extractor
		GenomeSequenceExtractor ge = new GenomeSequenceExtractor(new File(fasta), new File(fidx));

		//FOR EACH ENTRY IN THE READCOUNT FILE
		for(String gene : readCounts.keySet()){
			System.out.println(gene);
				for(String t: readCounts.get(gene).keySet()){

					//System.out.print(" \t \t");
					int c = readCounts.get(gene).get(t);
					//System.out.println(t +" : "+ c );

					System.out.println("---------------------------------------------");
					System.out.println(t);
					System.out.println(ge.getTranscriptSequence(gene, t, GTFannotation));



					for(int i= 0 ; i < c; i++){
						// each i should be a read
						//System.out.print(" \t \t");
						//System.out.print(" \t \t");
						//System.out.println(i);
					}



				}
		}



		//printFASTAQ("fw.fastq", "MYID", "ABCDEF");
		//printFASTAQ(" rw.fastq", "ID", "ABNDBASNBH");
		//printInfos("read.mappinginfo");



		Utils.getRandomPos(10,10);

	}


 public static void printInfos(String fname) {

	 File outputfile= new File(od+fname);

	  try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("readid"+"\t"+"chr_id"+"\t"+"gene_id"+"\t"+"transcript_id"+"\t"+"fw_regvec"+"\t"+"rw_regvec"+"\t"+"t_fw_regvec"+"\t"+"t_rw_regvec"+"\t"+"fw_mut"+"\t"+"rw_mut");



			fos.close();
			dos.close();


	  } catch (IOException e) {
			System.out.println("Error Printing FASTAQ Delimited File");
		}




 }

  public static void printFASTAQ(String fname, String id, String sequence) {

	  //POSSIBLE ERROR : check if odends with / or not
	  File outputfile= new File(od+fname);

	  try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("@"+ id);
			dos.println(sequence);
			dos.println("+"+id);

			StringBuilder qualityScore = new StringBuilder();
			for(int i=0 ; i< sequence.length(); i++) {
				qualityScore.append("~");
			}
			dos.println(qualityScore.toString());

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
