package readSimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;

public class GenomeSequenceExtractor {
	
	 File fasta; 
	 File idx; 
	 RandomAccessFile raf; 
	 


	
	public GenomeSequenceExtractor(File fasta, File idx) {
		super();
		this.fasta = fasta;
		this.idx = idx;
		try {
			raf = new RandomAccessFile(fasta, "rw");

		} catch (FileNotFoundException e) {
			System.out.println("Error initializing the raf");
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		
		GenomeSequenceExtractor ge = new GenomeSequenceExtractor(new File("/Users/luisasantus/Desktop/hola"), new File(""));
	
		System.out.println(ge.getSequence("1",3,5));
		
	}



	public String getSequence(String chr, int start, int end) {
		
		HashMap<String, Double[]> mapIdx = parseIdx();
		Double[] array =  (Double[]) mapIdx.get(chr);
		
		//int entryLength = array[0].intValue();
		int entryStart =array[1].intValue();
		int lineLength = array[2].intValue();
		int lineLengthwnl= array[3].intValue();
		
		
		int s = entryStart + start -1 +  (start/lineLength); 
		int lengthSeq= end - start + 1; 
		//int length = lengthSeq + (lengthSeq/lineLength);
		
		try {
			
			int i = s-1; 
			int limit = (s+lengthSeq-1);
			while (i<limit) {
				raf.seek(i);
				
				if(((i+1)% (lineLengthwnl)) == 0) {
					limit++;

				}else {
					byte b = raf.readByte();
					System.out.print("i="+i+"---");
			        System.out.println((char) b);
					
				}
		        i++;
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		return chr;
		
	}
	
	public HashMap<String, Double[]> parseIdx(){
		HashMap<String,Double[]> map = new HashMap<String, Double[]>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(idx))) {

			String currentLine;
			
			try {
				while ((currentLine = br.readLine()) != null ) {
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
					int i = 0;
					String key =""; 
					Double[] a = new Double[4];
				    while (defaultTokenizer.hasMoreTokens())
				    {
				    		if(i==0) {
				    			key=defaultTokenizer.nextToken();
				    		}else {
				    			a[i]=Double.parseDouble(defaultTokenizer.nextToken());
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
