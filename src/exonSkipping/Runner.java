package exonSkipping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import plots.PlotUtils;

public class Runner {

	static Annotation annotation= new Annotation();
	static Vector<Double> max_exons;
	static Vector<Double> max_bases;

	static String outputFolder;
	public static String outputfile ;

	public static void main(String[] args) {


		//Read command line parameters


		try {
			Options options = new Options();
			options.addOption("gtf", true, "genomic annotation in gtf file format");
			options.addOption("o", true, "output file path");
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);

			if (cmd.hasOption("o") && cmd.hasOption("gtf")){
			String myFileName = cmd.getOptionValue("gtf");
			outputfile= cmd.getOptionValue("o");
			parserGTF.parse(myFileName);
			}
			else{
				System.out.println();
				System.out.println("The programm was invoked with wrong parameters");
				System.out.println();
				System.out.println("Correct Usage: ");
				System.out.println("\t \t -gtf <path/to/your/gtf/file>");
				System.out.println("\t \t -o <path/to/your/output/file>");

				System.exit(0);

			}


		} catch (ParseException e) {
			System.err.println("Error Reading the command line parameters!");
			e.printStackTrace();
		}


		// Print results in tsv file

		try {

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("id"+"\t"+"symbol"+"\t"+"chr"+"\t"+"strand"+"\t"+"nprots"+"\t"+"ntrans"+"\t"+"SV"+"\t"+"WT"+"\t"+"WT_prots"+"\t"+"SV_prots"+"\t"+"min_skipped_exon"+"\t"+"max_skipped_exon"+"\t"+"min_skipped_bases"+"\t"+"max_skipped_bases");

			max_exons = new Vector<Double>();
			max_bases = new Vector<Double>();

			Iterator<Entry<String, Gene>> it = annotation.getGenes().entrySet().iterator();
		    while (it.hasNext()) {

		        Map.Entry<String, Gene>pair = (Map.Entry<String, Gene>)it.next();
				Gene myGene= annotation.getGeneById(pair.getKey().toString());


				for(ExonSkipping es : myGene.calculateExonSkipping()){

				        //ID
						dos.print(pair.getKey().toString() +"\t");

						//NAME
						dos.print(myGene.getName()+"\t");

						//CHR
						dos.print(myGene.getChr()+"\t");

						//STRAND
						dos.print(myGene.getStrand()+"\t");

						//nprots (number of annotated CDS in the gene)
						dos.print(myGene.getNprots()+ "\t");

						//ntrans (number of annotated transcripts in the gene)
						dos.print(myGene.getNtrans()+ "\t");

						//SV (the SV intron as start:end)
						dos.print(es.getSv().getStart()+":"+es.getSv().getEnd()+ "\t");

						//WT (the WT introns within the SV intron separated by | as start:end)
						dos.print( Utilities.prettySetRegionVector(es.getWt())+"\t");

						//WT prots (ids of the WT CDS-s, separated by |)
						dos.print(Utilities.printID(es.getWtCDSids()) + "\t");

						//SV prots (ids of the SV CDS-s, separated by |)
						dos.print(Utilities.printID(es.getSvCDSids()) + "\t");

						//min skipped exon the minimal number of skipped exons in any WT/SV pair
						dos.print(es.getMin_skipped_exon()+ "\t");

						//max skipped exon the maximum number of skipped exons in any WT/SV pair
						dos.print(es.getMax_skipped_exon()+ "\t");
						max_exons.add((double) es.getMax_skipped_exon());
						System.out.println(es.getMax_skipped_exon());

						//min skipped bases the minimal number of skipped bases (joint length of skipped exons) in any WT/SV pair
						dos.print(es.getMin_skipped_bases()+ "\t");

						//max skipped bases the maximum number of skipped bases (joint length of skipped exons) in any WT/SV pair
						dos.print(es.getMax_skipped_bases()+ "\t");
						max_bases.add((double) es.getMax_skipped_bases());


						//System.out.println(es.getMax_skipped_bases());
						//Utilities.printRegion(es.getSv());
						//System.out.println(es.getWt().size());

						dos.println();



				}



		        it.remove(); // avoids a ConcurrentModificationException

		    }
			dos.close();
			fos.close();
		} catch (IOException e) {
			System.out.println("Error Printing Tab Delimited File");
		}

		//retrieve outputfolder
		String[] sl = outputfile.split("\\/");
		outputFolder= outputfile.replace(sl[sl.length-1], "");

		String nameSkippedExon="skipped_exons.jpg";
		String nameSkippedBases="skipped_bases.jpg";


	//	PlotUtils.getCumHist(max_exons, "Cumulative distribution of skipped Exons", "Skipped Exons", "Number Of Events", outputFolder+nameSkippedExon);
	//	PlotUtils.getCumHist(max_bases, "Cumulative distribution of skipped Bases", "Skipped Bases", "Number Of Events", outputFolder+nameSkippedBases);



	}

}
