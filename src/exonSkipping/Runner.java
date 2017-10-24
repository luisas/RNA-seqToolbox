package exonSkipping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Runner {

	static Annotation annotation= new Annotation();


	public static void main(String[] args) {

		try {
			Options options = new Options();
			options.addOption("gtf", true, "genomic annotation in gtf file format");
			options.addOption("o", true, "output file path");
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//String myFileName = "/Users/luisasantus/Desktop/GoBi/data/Drosophila_melanogaster.BDGP5.77.gtf";
		//String myFileName = "/Users/luisasantus/Desktop/GoBi/data/small.gtf";
		//String myFileName = "/Users/luisasantus/Desktop/GoBi/data/test.gtf";
		//String myFileName = "/home/s/santus/Desktop/GoBi/gencode.v25.annotation.gtf";
		String myFileName = "/home/s/santus/Desktop/GoBi/small.gtf";

		parserGTF.parse(myFileName);
		//parserGTF.parse(commandLine.getOptionValue("gtf"));

		try {
			// Tab delimited file will be written to data with the name tab-file.csv
			//String outputfile = commandLine.getOptionValue("o");
			String outputfile= "/home/s/santus/Desktop/GoBi/file.tsv";

			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			dos.println("id"+"\t"+"symbol"+"\t"+"chr"+"\t"+"strand"+"\t"+"nprots"+"\t"+"ntrans"+"\t"+"SV"+"\t"+"WT"+"\t"+"WT_prots"+"\t"+"SV_prots"+"\t"+"min_skipped_exon"+"\t"+"max_skipped_exon"+"\t"+"min_skipped_bases"+"\t"+"max_skipped_bases");


			Iterator it = annotation.getGenes().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
				Gene myGene= annotation.getGeneById(pair.getKey().toString());
				RegionVector rv = new RegionVector(myGene.getId(),myGene.getExon() );
				Vector<Region> introns= rv.inverse();

				for(Region r: introns ){

		        //ID
				dos.print(pair.getKey().toString() +"\t");
				//NAME
				dos.print(myGene.getName()+"\t");
				//CHR
				dos.print(myGene.getChr()+"\t");
				//STRAND
				dos.print(myGene.getStrand()+"\t");
				//nprots (number of annotated CDS in the gene)
				dos.print(annotation.getNumberOfCds(myGene)+ "\t");
				//ntrans (number of annotated transcripts in the gene)
				dos.print(annotation.getNumberTranscripts(myGene)+ "\t");

				//SV (the SV intron as start:end)
				dos.print(r.getStart()+":"+r.getEnd()+ "\t");
				//System.out.println(Collections.singletonList(introns));

				//Utilities.printVector(myGene.getExon());
				dos.println();

				}


				//WT (the WT introns within the SV intron separated by | as start:end)

				//SV prots (ids of the SV CDS-s, separated by |)

				//WT prots (ids of the WT CDS-s, separated by |)

				//min skipped exon the minimal number of skipped exons in any WT/SV pair

				//max skipped exon the maximum number of skipped exons in any WT/SV pair

				//min skipped bases the minimal number of skipped bases (joint length of skipped exons) in any WT/SV pair

				//max skipped bases the maximum number of skipped bases (joint length of skipped exons) in any WT/SV pair






		        it.remove(); // avoids a ConcurrentModificationException

		    }








			dos.close();
			fos.close();
			} catch (IOException e) {
			System.out.println("Error Printing Tab Delimited File");
			}















	}

}
