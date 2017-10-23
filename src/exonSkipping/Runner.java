package exonSkipping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

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
		String myFileName = "/Users/luisasantus/Desktop/GoBi/data/small.gtf";
		//String myFileName = "/Users/luisasantus/Desktop/GoBi/data/test.gtf";

		parserGTF.parse(myFileName);
		//parserGTF.parse(commandLine.getOptionValue("gtf"));
		
		try {
			// Tab delimited file will be written to data with the name tab-file.csv
			//String outputfile = commandLine.getOptionValue("o");
			String outputfile= "/Users/luisasantus/Desktop/GoBi/data/file.tsv";
			
			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			//dos.println("Heading1\tHeading2\tHeading3\t");
			
			
			Iterator it = annotation.getGenes().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
				Gene myGene= annotation.getGeneById(pair.getKey().toString());
				
		        //ID
				dos.print(pair.getKey().toString() +"\t");
				//NAME
				dos.print(myGene.getName()+"\t");
				//CHR
				dos.print(myGene.getChr()+"\t");
				//STRAND
				dos.print(myGene.getStrand()+"\t");
				//nprots (number of annotated CDS in the gene)
				dos.print(annotation.getNumberOfCds(myGene, annotation.getCds()) + "\t");
				//ntrans (number of annotated transcripts in the gene)
				dos.print(annotation.getNumberTranscripts(myGene));
				
				//SV (the SV intron as start:end)
				
				//WT (the WT introns within the SV intron separated by | as start:end)
				
				//SV prots (ids of the SV CDS-s, separated by |)
				
				//WT prots (ids of the WT CDS-s, separated by |)
				
				//min skipped exon the minimal number of skipped exons in any WT/SV pair
				
				//max skipped exon the maximum number of skipped exons in any WT/SV pair
				
				//min skipped bases the minimal number of skipped bases (joint length of skipped exons) in any WT/SV pair
				
				//max skipped bases the maximum number of skipped bases (joint length of skipped exons) in any WT/SV pair
				
				
				
						
				
				
		        it.remove(); // avoids a ConcurrentModificationException
				dos.println();
		    }
			


		
				   
				   
				  
			
			dos.close();
			fos.close();
			} catch (IOException e) {
			System.out.println("Error Printing Tab Delimited File");
			}
		
		
		
		

		
		
		
		
		
		
		
		
		
		
	}

}
