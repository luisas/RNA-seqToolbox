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
//		//System.out.println(ge.getSequence("19",50015536,50029590));
//		ge.getSequence("19",50015536,50029590);
//	}




	public static String getTranscriptSequence(String gene, String transcript, Annotation annotation ){
		boolean NL = true;
		int lineLength = 60;

		Transcript t = annotation.getGenes().get(gene).getTranscripts().get(transcript);
		String chr = annotation.getGenes().get(gene).getChr();
		String strand = annotation.getGenes().get(gene).getStrand();

		System.out.println(t.getRegionVectorExons());

		StringBuilder sb = new StringBuilder();
		int countNL = 0 ;
		for (Region r : t.getRegionVectorExons().getVector()){

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

		//System.out.println("START: "+ start);
		//System.out.println("STOP: "+end);
		long entryLength = array[0];
		long entryStart =array[1];
		long lineLength = array[2];
		long lineLengthwnl= array[3];

		//System.out.println("entryLength  " +entryLength);
		//System.out.println("entryStart  " +entryStart);

		long s = start + entryStart -1 + (start/lineLength);

		int lengthSeq= end - start + 1;


		long rest = (start%lineLength);

		//int length = lengthSeq + (lengthSeq/lineLength);
		try {

			long i = s;
			long limit = (s+lengthSeq);

			long count = rest-1;
			int countNL = 0 ;
			while (i<limit) {


				count ++;
				countNL++;
				//System.out.print(count+",");
				raf.seek(i);
				//System.out.println((char )raf.readByte());

				if(count == lineLengthwnl){
					//byte b = raf.readByte();
			        //System.out.print((char) b);
			        count = 0;
					limit++;



					//System.out.println();


//				}
//
//				if(((i+1)% (lineLengthwnl)) == 0) {
//					limit++;
					//byte b = raf.readByte();

			       // System.out.print((char) b);

				}else {
					byte b = raf.readByte();
					sb.append((char)b);
			        //System.out.print((char) b);

				}



		        i++;


			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}









		return sb.toString();

	}

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
