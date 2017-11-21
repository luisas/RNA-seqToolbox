package readSimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.TreeSet;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math3.distribution.NormalDistribution;


import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;


/**
 *
 * @author santus
 *
 *Print out directly
 */

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
	static GenomeSequenceExtractor ge ;


	public static void main(String[] args) throws IOException {



		//PARSE COMMAND LINE
		readCommandLine(args);

		//PARSE THE GTF FILE
		exonSkipping.parserGTF.parse(gtf);

		//Genome Sequence Extractor
		 ge = new GenomeSequenceExtractor(new File(fasta), new File(fidx));

		 printOutput();

		// SAVE ALL FRAGMENTS AND READS
		//ReadCreator rc = new ReadCreator(GTFannotation, ge,length, frlength, SD, readcounts,mutationrate,  nMut);



		//PRINT RESULT FILE
	//	printInfos("read.mappinginfo", rc.getFragments());


		//RETRIEVE READ SEQUENCE INFO FROM FRAGMENTS HASHMAP
//		HashMap<Integer,String> FW = new HashMap<Integer,String>();
//		for(Integer key: rc.getFragments().keySet()){
//			FW.put(key, rc.getFragments().get(key).getFW().getSequence().getSequence());
//		}
//		HashMap<Integer,String> RW = new HashMap<Integer,String>();
//		for(Integer key: rc.getFragments().keySet()){
//			RW.put(key, rc.getFragments().get(key).getRW().getSequence().getSequence());
//		}

		//PRINT FASTAQ FILE
		//printFASTAQ("fw.fastq", FW);
		//printFASTAQ("rw.fastq", RW);


	}







public static void printOutput() throws IOException{


	//PARSE THE READCOUNT FILE
	HashMap<String,HashMap<String, Integer>> readCounts = Utils.parseReadCount(readcounts);


	  String output;
	  if(od.endsWith("/")){
		  output=od;
	  }else{
		  output=od+"/";
	  }

	File outputfileINFO= new File(output+"read.mappinginfo");
	File outputfileFW= new File(output+"fw.fastq");
	File outputfileRW= new File(output+"rw.fastq");

	FileWriter fosFW = new FileWriter(outputfileFW);
	PrintWriter dosFW = new PrintWriter(fosFW);
	FileWriter fosRW = new FileWriter(outputfileRW);
	PrintWriter dosRW = new PrintWriter(fosRW);
	FileWriter fos = new FileWriter(outputfileINFO);
	PrintWriter dos = new PrintWriter(fos);
	dos.println("readid"+"\t"+"chr"+"\t"+"gene"+"\t"+"transcript"+"\t"+"t_fw_regvec"+"\t"+"t_rw_regvec"+"\t"+"fw_regvec"+"\t"+"rw_regvec"+"\t"+"fw_mut"+"\t"+"rw_mut");

	int idFragment = 0 ;
	String TranscriptSeq;
	//Go through all genes and transcripts
	for(String geneID : readCounts.keySet()){
			for(String t: readCounts.get(geneID).keySet()){

				Gene gene = GTFannotation.getGenes().get(geneID);
				Transcript transcript = gene.getTranscripts().get(t);
				TranscriptSeq = ge.getTranscriptSequence(gene,transcript);
				String chr = gene.getChr();
				NormalDistribution nd = new NormalDistribution(frlength, SD);
				int FL;
				int startPosition;
				int numberReads = readCounts.get(geneID).get(t);
				String fragmentSequence ="";
				String readSequenceFW ="";
				String readSequenceRW = "";

				//go through all reads
				for(int id= 0 ; id < numberReads; id++){
					System.out.println(idFragment);
					//CALC FRAGMENT LENGTH

					 int ndSample = (int) nd.sample();
					 FL = Integer.max(length, ndSample);
					 while(FL >= transcript.getRegionVectorExons().getRegionsLength()){
						 ndSample = (int) nd.sample();
						 FL = Integer.max(length, ndSample);
					 }

					 //GET RANDOM START POSITION
					 startPosition = Utils.getRandomPos(transcript.getRegionVectorExons().getRegionsLength(), FL);
					 int endPosition = startPosition+FL;

					 //GET FRAGMENT SEQUENCE
					 fragmentSequence = ge.getFragmentSequence(startPosition,endPosition,TranscriptSeq);

					 //GET FW READ SEQUENCE
					 int startFW =  0;
					 int stopFW = length;
					 readSequenceFW = fragmentSequence.substring(0, length);


					 //GET RW READ SEQUENCE
					 int stopRW=FL;
					 int startRW = stopRW-length;
					 readSequenceRW= Utils.getRevComplement(fragmentSequence.substring(startRW, stopRW));

					 //GET MUTATED SEQUENCES
					 MutatedSeq mFW = new MutatedSeq(readSequenceFW,mutationrate);
					 MutatedSeq mRW = new MutatedSeq(readSequenceRW,mutationrate);

					 //STORE READS
					 System.out.println("tstart"+ startFW+startPosition);
					 RegionVector genPosFW = Utils.getGenomicRV( startFW+startPosition, stopFW+startPosition,  geneID,  t,  GTFannotation);
					 
					 System.out.println("-----start: "+startRW+startPosition);
					 System.out.println("--------stop : "+stopRW+startPosition);
					 RegionVector genPosRW = Utils.getGenomicRV( startRW+startPosition, stopRW+startPosition,  geneID,  t,  GTFannotation);

					 Read RW = new Read("-", startRW, stopRW, mRW,genPosRW);
					 Read FW = new Read("+", startFW, stopFW, mFW,genPosFW);

					 	//PRINT FASTAQ FILES
						dosRW.println("@"+ idFragment);
						dosRW.println(RW.getSequence().getSequence());
						dosRW.println("+"+idFragment);
						StringBuilder qualityScore = new StringBuilder();
						for(int i=0 ; i< RW.getSequence().getSequence().length(); i++) {
							qualityScore.append("I");
						}
						dosRW.println(qualityScore.toString());

						dosFW.println("@"+ idFragment);
						dosFW.println(FW.getSequence().getSequence());
						dosFW.println("+"+idFragment);
						StringBuilder qualityScoreFW = new StringBuilder();
						for(int i=0 ; i< FW.getSequence().getSequence().length(); i++) {
							qualityScoreFW.append("I");
						}
						dosFW.println(qualityScoreFW.toString());



						//PRINT INFOS

					    dos.print(Integer.toString(idFragment)+"\t");
						//CHROMSOME
						dos.print(chr+"\t");
						//GENE ID
						dos.print(geneID+"\t");
						//TRANSCRIPT ID
						dos.print(t+"\t");
						//T FW
						dos.print(FW.getTstart()+startPosition+"-"+(FW.getTstop()+startPosition)+"\t");
						// T RW
						dos.print(RW.getTstart()+startPosition+"-"+(RW.getTstop()+startPosition)+"\t");
						//FW
						dos.print(Utils.prettyRegionVector(FW.getGenPos())+"\t");
						//RW
						dos.print(Utils.prettyRegionVector(RW.getGenPos())+"\t");
						//FW MUT
						dos.print(Utils.prettyMutations(FW.getSequence().getPositions())+"\t");
						//RW MUT
						dos.print(Utils.prettyMutations(RW.getSequence().getPositions())+"\n");



					 idFragment++;

				}



			}

	}
	fos.close();
	dos.close();
	fosFW.close();
	dosFW.close();
	fosRW.close();
	dosRW.close();



}


 public static void printInfos(String fname, HashMap<Integer,Fragment> fragments) {


	  String output;
	  if(od.endsWith("/")){
		  output=od;
	  }else{
		  output=od+"/";
	  }

	  File outputfile= new File(output+fname);

	  try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("readid"+"\t"+"chr_id"+"\t"+"gene_id"+"\t"+"transcript_id"+"\t"+"t_fw_regvec"+"\t"+"t_rw_regvec"+"\t"+"fw_regvec"+"\t"+"rw_regvec"+"\t"+"fw_mut"+"\t"+"rw_mut");


			for(Integer key: new TreeSet<Integer>(fragments.keySet())) {

				//System.out.println("KEY: "+key);

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

				//T FW
				dos.print(FW.getTstart()+f.getTstart()+"-"+(FW.getTstop()+f.getTstart())+"\t");
				// T RW
				dos.print(RW.getTstart()+f.getTstart()+"-"+(RW.getTstop()+f.getTstart())+"\t");


				//FW
				dos.print(Utils.prettyRegionVector(FW.getGenPos())+"\t");

				//RW
				dos.print(Utils.prettyRegionVector(RW.getGenPos())+"\t");


				//System.out.println(key+"\t"+f.getTstart()+"-"+f.getTstop());
				//FW MUT
				//dos.print(1+"\t");
				dos.print(Utils.prettyMutations(FW.getSequence().getPositions())+"\t");

				//RW MUT
				dos.print(Utils.prettyMutations(RW.getSequence().getPositions())+"\n");
				//dos.print(1+"\n");
			}


			fos.close();
			dos.close();


	  } catch (IOException e) {
			System.out.println("Error Printing FASTAQ Delimited File");
		}




 }

  public static void printFASTAQ(String fname, HashMap<Integer,String> map) {

	  String output;
	  if(od.endsWith("/")){
		  output=od;
	  }else{
		  output=od+"/";
	  }

	  File outputfile= new File(output+fname);

	  try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			for(Integer id: new TreeSet<Integer>(map.keySet())){
				dos.println("@"+ id);
				String sequence = map.get(id);
				dos.println(sequence);
				dos.println("+"+id);
				StringBuilder qualityScore = new StringBuilder();
				for(int i=0 ; i< sequence.length(); i++) {
					qualityScore.append("I");
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

			if (cmd.hasOption("od")
					&& cmd.hasOption("length")
					&& cmd.hasOption("frlength")
					&& cmd.hasOption("SD")
					&& cmd.hasOption("readcounts")
					&& cmd.hasOption("mutationrate")
					&& cmd.hasOption("fasta")
					&& cmd.hasOption("fidx")
					&& cmd.hasOption("gtf")

					){
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
