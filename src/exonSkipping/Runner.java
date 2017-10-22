package exonSkipping;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
		
		String myFileName = "/Users/luisasantus/Desktop/GoBi/data/Drosophila_melanogaster.BDGP5.77.gtf";
		//String myFileName = "/Users/luisasantus/Desktop/GoBi/data/small.gtf";
		parserGTF.parse(myFileName);
		//parser.parse(commandLine.getOptionValue("gtf"));
		
		try {
			// Tab delimited file will be written to data with the name tab-file.csv
			//String outputfile = commandLine.getOptionValue("o");
			String outputfile= "/Users/luisasantus/Desktop/GoBi/data/file.tsv";
			
			FileWriter fos = new FileWriter(outputfile);
			PrintWriter dos = new PrintWriter(fos);
			//dos.println("Heading1\tHeading2\tHeading3\t");
			// loop through all your data and print it to the file
			
			
			Iterator it = annotation.getGenes().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
				dos.print(pair.getKey().toString() +"\t");

				System.out.println(pair.getValue());
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
