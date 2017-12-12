package differentialExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import plots.Config;
import plots.RExecutor;

/*
 * 
 * QUESTIONS:
 * 1.how do i retrieve the value for the pdata file?
 * 
 */
public class Runner {
	
	static String outdir; 
	static String config; 
	static String labels;
	static String countfiles;
	
	static List<String> genes;

	static String[] methods = { "limma", "edgeR","DESeq"};
	
	public static void main(String[] args) {
		
		//Read command Line
		readCommandLine(args);

		genes = getGenes();
		createInputFile();		
		String[] input = {outdir+"/exprs.txt",outdir+"/p_data.txt",outdir+"/f_data.txt",""};

		for(String method : methods) {
			input[3]=method;	
			//execute the string 
			System.out.println(createCommand(input));
			executeR(createCommand(input));
			
			addColumnToResultFile(outdir+"/"+method+"_temp.out", outdir+"/"+method+".out");
			
			//DELETE TEMP FILE
			try {
				Files.delete( Paths.get(outdir+"/"+method+"_temp.out"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void createInputFile() {
		try (BufferedReader br = new BufferedReader(new FileReader(countfiles))) {
			
			String currentLine;
			List<String> linesPdata = new ArrayList<String>();
			List<String> labelsValues = readLabels(labels);
			List<List<Integer>> columnsEx = new ArrayList<List<Integer>>();
			String tempSampleName = null; 
			int value = 0 ; 
			
			while ((currentLine = br.readLine()) != null ) {
				
				String[] splittedLine = currentLine.split("\t");
				String sampleName= splittedLine[0];
				String condition = splittedLine[2];
				String geneCountFile = splittedLine[1];
				
				if(tempSampleName == null) {
					tempSampleName=sampleName;
				}else if(!sampleName.equals(tempSampleName)) {
					value++;
				}
				
				//READ GENECOUNT 
				columnsEx.add(readGeneCount(geneCountFile, labelsValues));
				
				String linePdata = sampleName+"."+condition+"\t"+value;
				linesPdata.add(linePdata);
				tempSampleName = sampleName;
			}
			
			writePdata(linesPdata,outdir+"/p_data.txt");
			writeFdata(labelsValues, value,outdir+"/f_data.txt" );
			writeExprdata(columnsEx, outdir+"/exprs.txt");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static List<String> readLabels(String filename) {
		
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			
			String currentLine;

			List<String> column = new ArrayList<String>();
			while ((currentLine = br.readLine()) != null ) {
				String[] splittedLine = currentLine.split("\t");
				column.add(splittedLine[0]);
			}

			return column; 
			
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	public static void writeFdata(List<String> column1, int condition,String filepath) {
		File filename = new File(filepath);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
					
			for(int i =0 ; i < column1.size(); i++) {
				String prefix = "";
				for(int j = 0; j<=condition ; j++) {
				dos.print(prefix+column1.get(i));
				prefix="\t";			
				}
				dos.println();
			}

			fos.close();
			dos.close(); 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<Integer> readGeneCount(String filename, List<String> labels) {
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
				//System.out.println("lll"+labels.size());
					
					String currentLine;
					boolean header =true;
					int numReads;
					HashMap<String, Integer> map = new HashMap<String,Integer>();
					List<String> genes = new ArrayList<String>();
					List<Integer> column = new ArrayList<Integer>();
					while ((currentLine = br.readLine()) != null ) {
						if(header) {
							header=false;
							continue;
						}
						String[] splittedLine = currentLine.split("\t");
						 numReads = Integer.parseInt(splittedLine[8]);
						 
						 if(labels.contains(splittedLine[0])) {
							 if(map.containsKey(splittedLine[0])) {
								 map.put(splittedLine[0], map.get(splittedLine[0])+numReads);
							 }else {
								 map.put(splittedLine[0],numReads);
							 }
						 }
				
			
					}
					
					for(String s : genes) {
						if(map.containsKey(s)) {
							column.add(map.get(s));
							
						}else {
							column.add(0);
						}
					}
					//System.out.println("length"+ column.size());
					return column; 
					
					
		
				} catch (IOException e) {
					e.printStackTrace();
				}
			System.out.println("Something going wrong with readGeneCount");
			return null;
	}
	
	
	public static List<String> getGenes(){
		
		List<String> genes = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(countfiles))) {
			
			String currentLine;
			String currentLineGene;
			
			while ((currentLine = br.readLine()) != null ) {
				
				String[] splittedLine = currentLine.split("\t");
				String geneCountFile = splittedLine[1];
				try (BufferedReader br2 = new BufferedReader(new FileReader(geneCountFile))){
					
					while ((currentLineGene = br2.readLine()) != null ) {
						if(!genes.contains(currentLine.split("\t")[0])) {
							genes.add(currentLine.split("\t")[0]);
						}
						
						
					}
				}
				

			}
			
		

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		return genes;
		
	}
	
	public static void writePdata(List<String> lines, String filePath) {
		File filename = new File(filePath);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			for(String line : lines) {
				dos.print(line+"\n");		
			}
			fos.close();
			dos.close(); 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeExprdata(List<List<Integer>> columns, String filePath) {
		File filename = new File(filePath);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			for(int i =0; i < columns.get(0).size() ; i++) {
				//System.out.println("s"+columns.get(0).size());
				for(List<Integer>  column: columns) {
					//System.out.println(column.size());
						dos.print(column.get(i)+"\t");		
				}
				dos.println();
			}
			fos.close();
			dos.close(); 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double calcFDR(String line) {
		
		return 0.0;
	}
	

	public static void addColumnToResultFile(String input, String output) {
		
		List<Double> pValues = new ArrayList<Double>();
		List<Double> newPValues = new ArrayList<Double>();
		List<String> linesOfInputFile = new ArrayList<String>();
		String headerLine = null; 
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			File filenameWrite = new File(output);
			FileWriter fos;
				fos = new FileWriter(filenameWrite);
				PrintWriter dos = new PrintWriter(fos);
				String currentLine;
		
				boolean header =true; 
				while ((currentLine = br.readLine()) != null ) {
					
					//CASE OF HEADER
					if(header) {
						header=false;
						headerLine = currentLine;
						//dos.println(currentLine+"\tADJ.PVAL");
						continue;
					}
					//REST OF FILE
					System.out.println(currentLine.split("\t")[1]);
					pValues.add(Double.parseDouble(currentLine.split("\t")[2]));
					linesOfInputFile.add(currentLine);
					//dos.println(currentLine+"\t"+calcFDR(currentLine));
				}
				
				
				br.close();
				
				double mFDR = Double.MAX_VALUE;
				
				int n = pValues.size();
				double FDRk;
				//sort descending
				Collections.sort(pValues);
				Collections.reverse(pValues);
				//compute FDRk
				for (int k =0; k<n; k++) {
					FDRk=pValues.get(k)*n/k;
					newPValues.add(Math.min(FDRk,mFDR));
					if(mFDR > FDRk) {
						mFDR=FDRk;
					}
				}
				
				dos.println(headerLine+"\tADJ.PVAL");
				for(int k = 0; k<n ; k++) {
					dos.println(linesOfInputFile.get(k)+"\t"+newPValues.get(k));
				}
				
				dos.close();
				
				
				
		
		}catch (IOException e) {
					e.printStackTrace();
				}
		
		
	}
	
	
	public static String createCommand(String[] input) {
		//System.out.println(config);
		String script = Config.getConfig("diffscript", config);
		String outFile = outdir+"-"+input[3]+"_temp.out";
		
		//Rscript de_rseq.R <exprs.file> <pdat.file> <fdat.file> <de.method> <out.file>"
		return "Rscript "+script+" "+input[0]+" "+input[1]+" "+input[2]+" "+input[3]+" "+outFile; 
	}
	
	
	
	public static void executeR(String command) {

		RExecutor r =new RExecutor(command.toString(),config);
		Thread t = new Thread(r);
		t.start();
		try{
			t.join();
		}
		catch(InterruptedException e){
			throw new RuntimeException("R did not exit"+" properly!");
		}
	}


	public static void printoutResult() {
		File filename = new File(outdir);
		FileWriter fos;
		try {
			fos = new FileWriter(filename);
			PrintWriter dos = new PrintWriter(fos);
			
			
			
			fos.close();
			dos.close(); 

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
	}
	// Implement a reverse-order Comparator by lambda function
	static Comparator<Double> compRev = (Double a, Double b) -> {
	    return b.compareTo(a);
	};
	
	public static void readCommandLine(String[] args) {

		try {
			Options options = new Options();
			options.addOption("countfiles", true, "countfiles");
			options.addOption("labels", true, "labels file");
			options.addOption("config", true, "config file");
			options.addOption("outdir", true, "output directory path");



			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( options, args);

			// TODO check if all param!
			if (cmd.hasOption("outdir") && cmd.hasOption("labels") && cmd.hasOption("config") && cmd.hasOption("countfiles") ){
			
				outdir = cmd.getOptionValue("outdir");
				config = cmd.getOptionValue("config");
				labels=cmd.getOptionValue("labels");
				countfiles= cmd.getOptionValue("countfiles");
				
	
			}
			else{
				System.out.println();
				System.out.println("The programm was invoked with wrong parameters");
				System.out.println();
				System.out.println("Correct Usage: ");
				System.out.println("\t \t -countfiles <path/to/your/countfile>" );
				System.out.println("\t \t -outdir <path/to/your/output directory>");
				System.out.println("\t \t -labels <path/to/your/labels file>");
				System.out.println("\t \t -config <path/to/your/config file>");

				System.exit(0);

			}

	} catch (ParseException e) {
		System.err.println("Error Reading the command line parameters!");
		e.printStackTrace();
	}
	


}


}

