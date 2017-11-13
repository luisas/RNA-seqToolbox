package readSimulator;

import exonSkipping.Region;
import exonSkipping.RegionVector;
import java.util.concurrent.ThreadLocalRandom;


public class Utils {
	
	
	
	
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

}
