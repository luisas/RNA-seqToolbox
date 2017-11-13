package readSimulator;

import exonSkipping.Annotation;
import exonSkipping.Region;
import exonSkipping.RegionVector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;


public class Utils {






	public static HashMap<String,HashMap<String, Integer>> parseReadCount(String filename){


		HashMap<String,HashMap<String, Integer>> genes = new HashMap<String,HashMap<String, Integer>>();

		//Read the GTF file line by line
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			String currentLine;

			while ((currentLine = br.readLine()) != null ) {



				if(!currentLine.startsWith("gene")) {
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);

					int i = 0 ;
					String key = "";
					String t="";
			        while (defaultTokenizer.hasMoreTokens())
			        {
			        	switch(i){
			        		case 0:
			        			key= defaultTokenizer.nextToken();

			        		case 1:
			        			t=defaultTokenizer.nextToken();

			        		case 2:

			        			int c = Integer.parseInt(defaultTokenizer.nextToken());

			        			if(genes.containsKey(key)){
			        				genes.get(key).put(t,c);
			        			}else{
			        				HashMap<String, Integer> tandc = new HashMap<String, Integer>();
				        			tandc.put(t,c);
			        				genes.put(key, tandc);
			        			}

			        	}
			        	i++;
			        }
			}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return genes;



	}


	public double getFL(int readLength, double SD, double mean) {

		return 0;
	}



	public static int getRandomPos(int transcriptLength, int FL) {

		return ThreadLocalRandom.current().nextInt(0, (transcriptLength-FL) + 1);

	}



	public static String prettyRegionVector(RegionVector rv) {

		StringBuilder sb = new StringBuilder();
		String prefix="";
		for(Region r : rv.getVector()) {
			sb.append(prefix+r.getStart()+"-"+r.getEnd());
			prefix="|";
		}
		return sb.toString();
	}


	public static String getComplement(String sequence){

		StringBuilder sb = new StringBuilder();

		for(int i = 0 ; i<sequence.length(); i++){

			switch(sequence.charAt(i)){
				case 'A':
					sb.append("C");
				case 'C':
					sb.append("A");
				case 'T':
					sb.append("G");
				case 'G':
					sb.append("T");
			}
		}

		return sb.toString();
	}

}
