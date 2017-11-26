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

	public static void main(String[] args) {




		GenomeSequenceExtractor ge = new GenomeSequenceExtractor(new File("/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.dna.toplevel.fa"), new File("/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.dna.toplevel.fa.fai" ));

		String gene = "ENSG00000183091";
		String transcript = "ENST00000427231";


//152468700-152468903

//
	String sequence = Utils.getRevComplement(getSequence("2",152468700,152468903));
	System.out.println(sequence);
	System.out.println(sequence.length());

//
//		System.out.println(Utils.getRevComplement(getSequence("2",152437997,152438014))+Utils.getRevComplement(getSequence("2",152437362,152437418)));


		String gtf= "/home/proj/biosoft/praktikum/genprakt/ReadSimulator/Homo_sapiens.GRCh37.75.gtf";
		exonSkipping.parserGTF.parse(gtf);



		String tseq= ge.getTranscriptSequence(Utils.annotation.getGenes().get(gene), Utils.annotation.getGenes().get(gene).getTranscripts().get(transcript));
		System.out.println( tseq);
		Transcript t = Utils.annotation.getGenes().get(gene).getTranscripts().get(transcript);





		System.out.println("FWREAD");
		System.out.println(tseq.substring(16469,16544 ));

		System.out.println("--------");
		System.out.println("RWREAD");
		System.out.println(Utils.getRevComplement(tseq.substring( 16610,16685)));




	}


	/**
	 *
	 * GIVEN a transcript sequence gets a substring of it
	 *
	 */




	public static String getFragmentSequence(int start, int stop, String transcriptSeq){

		return transcriptSeq.substring(start, stop);

	}



	public static String getTranscriptSequenceProva(Gene gene, Transcript transcript){

		StringBuilder sequence = new StringBuilder();
		RegionVector positions = new RegionVector();
		for(Region exon : transcript.getRegionVectorExons().getVector()){

			RegionVector current = Utils.getGenomicRV(exon.getStart(), exon.getEnd(), gene.getId(), transcript.getId(), Utils.annotation);
			positions.getVector() ;
		}
		boolean NL = true;
		int lineLength = 60;
		String strand = gene.getStrand();

		if(strand.equals("+")){
			for(Region r : positions.getVector()){
				sequence.append(getSequence(gene.getChr() , r.getStart(),r.getEnd()-1));
			}

		}else{
			for(Region r : positions.getVector()){
				sequence.insert(0,Utils.getRevComplement(getSequence(gene.getChr() , r.getStart(),r.getEnd()-1)));
			}

		}

		String sequenceString = sequence.toString();

		String seq;
		if(NL){
			seq=sequenceString.replaceAll("(.{"+lineLength+"})", "$1\n");

		}else{
			seq = sequenceString;
		}


		return seq;
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
		int countNL = 0 ;

		Collections.sort(transcript.getRegionVectorExons().getVector());
		System.out.println(Utils.prettyRegionVector(transcript.getRegionVectorExons()));

		int sum = 0;
		for (Region r : transcript.getRegionVectorExons().getVector()){

			if(strand.equals("-")){



				//System.out.println(Utils.getRevComplement(getSequence(chr, r.getStart(), r.getEnd())));
//				System.out.println("EXON LENGTH: "+ r.getLength());

//
				String seq = getSequence(chr, r.getStart(), r.getEnd());
				String rev =  Utils.getRevComplement(seq);
				sb.insert(0,rev);

//				System.out.println("lll "+ seq.charAt(0));
				if(r.getLength()!=rev.length() ){
//
//					System.out.println(rev);
					System.out.println("--------------------");
			    System.out.println(r.getLength());
				System.out.println("exon");
			    System.out.println(seq);
			    System.out.println(seq.length());
			    System.out.println(rev);
			    System.out.println(rev.length());

				}

				sum += Utils.getRevComplement(getSequence(chr, r.getStart(), r.getEnd())).length() ;
			}else{
				sb.append(getSequence(chr, r.getStart(), r.getEnd()));

			}
		}


		String sequenceString = sb.toString();

		System.out.println("length "+ sum);


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


		long globalStart = entryStart + start + (start/lineLength)-1;
		long globalEnd = entryStart + end+ (end/lineLength);

		

		
		if(start%lineLength==0 ){
			
				globalStart--; 
				System.out.println("minus!");
				
		}
//		else if((end%lineLength == 1) && (start%lineLength != 1)){
//			globalEnd++; 
//			System.out.println("plus");
//		}
		long globalPosition = globalStart;
		String a ="";
		int sum = 0 ;
		while(globalPosition < globalEnd ){

			try {
				raf.seek(globalPosition);
				byte b;
				b = raf.readByte();
				if((char) b == 'A' || (char) b == 'C' || (char) b == 'T' || (char) b == 'G' || (char) b == 'N'){
					sb.append((char)b);
					a +=(char)b;
					sum ++ ;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			globalPosition ++;

		}

		return a;


	}



	public static String getSequenceA(String chr, int start, int end) {

		int c = 0 ;

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




			while (i<limit) {




				count ++;
				raf.seek(i);


				if(count == lineLengthwnl  || count==0){

					if(count == 0 ){
						c++;
					}

						limit++;
				        count = 0;

				}else {
					byte b = raf.readByte();
					sb.append((char)b);

				}
		        i++;


			}


		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println(sb.toString().substring(0,sb.toString().length()-c));
		return sb.toString().substring(0,sb.toString().length()-c);

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
