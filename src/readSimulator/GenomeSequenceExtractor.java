package readSimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;

import exonSkipping.Annotation;
import exonSkipping.Gene;
import exonSkipping.Region;
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
//		GenomeSequenceExtractor ge = new GenomeSequenceExtractor(new File("/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.dna.toplevel.fa"), new File("/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.dna.toplevel.fa.fai" ));
//
//		System.out.println(ge.getSequence("19",13783,13858));
//		//ge.getSequence("19",50015536,50029590);
//	}




	public static String getFragmentSequence(int start, int stop, Transcript transcript, Gene gene){

		StringBuilder sb = new StringBuilder();

		String transcriptSeq = getTranscriptSequence(gene,transcript);

		for(int i =start ; i<stop; i++){
			if(i>transcriptSeq.length()){
				System.out.println("last position of read is bigger than the transcript length !");
				System.out.println("Position in read: "+ i);
				System.exit(0);
			}
			sb.append(transcriptSeq.charAt(i));
		}

		return sb.toString();
	}



	public static String getTranscriptSequence(Gene gene, Transcript transcript){
		boolean NL = false;
		int lineLength = 60;


		String chr = gene.getChr();
		String strand = gene.getStrand();

		StringBuilder sb = new StringBuilder();
		int countNL = 0 ;
		for (Region r : transcript.getRegionVectorExons().getVector()){

			sb.append(getSequence(chr, r.getStart(), r.getEnd()));
		}

		String sequenceString = sb.toString();


		if(strand.equals("-")){
			sequenceString  = Utils.getComplement(sequenceString.toString());
		}

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
		long lineLengthwnl= array[3];



		long s = start + entryStart -1 + (start/lineLength);

		int lengthSeq= end - start + 1;


		long rest = (start%lineLength);

		try {

			long i = s;
			long limit = (s+lengthSeq);

			long count = rest-1;
			int countNL = 0 ;
			while (i<limit) {


				count ++;
				countNL++;
				raf.seek(i);

				if(count == lineLengthwnl){
			        count = 0;
					limit++;

				}else {
					byte b = raf.readByte();
					sb.append((char)b);
				}

		        i++;


			}


		} catch (IOException e) {
			e.printStackTrace();
		}

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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		return map;
	}





}
