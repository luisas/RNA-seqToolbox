package readSimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import exonSkipping.Gene;
import exonSkipping.Region;
import exonSkipping.RegionVector;
import exonSkipping.Transcript;

public class GenomeSequenceExtractor {

	 File fasta;
	 File idx;
	 static RandomAccessFile raf;
	 static HashMap<String, long[]> mapIdx ;


	public GenomeSequenceExtractor(File fasta, File idx) {
		super();
		this.fasta = fasta;
		this.idx = idx;
		this.mapIdx = parseIdx();
		try {
			raf = new RandomAccessFile(fasta, "r");

		} catch (FileNotFoundException e) {
			System.out.println("Error initializing the raf");
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//
//
//
//
//		GenomeSequenceExtractor ge = new GenomeSequenceExtractor(new File("/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.dna.toplevel.fa"), new File("/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.dna.toplevel.fa.fai" ));
//
//		String gene = "ENSG00000183091";
//		String transcript = "ENST00000427231";
//
//	String sequence = Utils.getRevComplement(getSequence("2",152468700,152468903));
//	System.out.println(sequence);
//	System.out.println(sequence.length());
//
//
//		String gtf= "/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.gtf";
//		Utils.annotation = exonSkipping.parserGTF.parse(gtf);
//
//
//
//		String tseq= ge.getTranscriptSequence(Utils.annotation.getGenes().get(gene), Utils.annotation.getGenes().get(gene).getTranscripts().get(transcript));
//		System.out.println( tseq);
//		Transcript t = Utils.annotation.getGenes().get(gene).getTranscripts().get(transcript);
//
//
//
//
//
//		System.out.println("FWREAD");
//		System.out.println(tseq.substring(16469,16544 ));
//
//		System.out.println("--------");
//		System.out.println("RWREAD");
//		System.out.println(Utils.getRevComplement(tseq.substring( 16610,16685)));
//
//
//
//
//	}


	/**
	 *
	 * GIVEN a transcript sequence gets a substring of it
	 *
	 */




	public static String getFragmentSequence(int start, int stop, String transcriptSeq){

		return transcriptSeq.substring(start, stop);

	}



	/**
	 * Gets the transcript (gespliced!!) sequence
	 * @param gene
	 * @param transcript
	 * @return
	 */
	public static String getTranscriptSequence(Gene gene, Transcript transcript){
		boolean NL = false;
		int lineLength = 60;


		String chr = gene.getChr();
		String strand = gene.getStrand();

		StringBuilder sb = new StringBuilder();

		Collections.sort(transcript.getRegionVectorExons().getVector());

		for (Region r : transcript.getRegionVectorExons().getVector()){

			if(strand.equals("-")){

				String seq = getSequence(chr, r.getStart(), r.getEnd());
				String rev =  Utils.getRevComplement(seq);
				sb.insert(0,rev);

			}else{
				sb.append(getSequence(chr, r.getStart(), r.getEnd()));

			}
		}


		String sequenceString = sb.toString();

		String seq;
		if(NL){
			seq=sequenceString.replaceAll("(.{"+lineLength+"})", "$1\n");

		}else{
			seq = sequenceString;
		}


		return seq;
	}



	public static String getSequence(String chr, int start, int end) {

		long[] array =  (long[]) mapIdx.get(chr);
		StringBuilder sb = new StringBuilder();
		long entryStart =array[1];
		long lineLength = array[2];

		long globalStart = entryStart + start + (start/lineLength)-1;
		long globalEnd = entryStart + end+ (end/lineLength);


		if(start%lineLength==0 ){
				globalStart--;
		}

		long globalPosition = globalStart;
		//String a ="";
		while(globalPosition < globalEnd ){

			try {
				raf.seek(globalPosition);
				byte b;
				b = raf.readByte();
				if((char) b == 'A' || (char) b == 'C' || (char) b == 'T' || (char) b == 'G' || (char) b == 'N'){
					sb.append((char)b);
					//a +=(char)b;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			globalPosition ++;

		}

		//return a
		return sb.toString();


	}



	/*
	 * Parses the IDX file
	 */
	public HashMap<String, long[]> parseIdx(){
		HashMap<String,long[]> map = new HashMap<String, long[]>();

		try (BufferedReader br = new BufferedReader(new FileReader(idx))) {

			String currentLine;

			try {
				while ((currentLine = br.readLine()) != null ) {
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
					int i = 0;

					String key ="";
					long[] a = new long[5];
				    while (defaultTokenizer.hasMoreTokens())
				    {


				    		if(i==0) {
				    			key=defaultTokenizer.nextToken();
				    		}else {
				    			a[i-1]=Long.parseLong(defaultTokenizer.nextToken());
				    		}
				    		i++;
				    }
				    map.put(key, a);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			} catch (FileNotFoundException e1) {
 				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		return map;
	}





}
