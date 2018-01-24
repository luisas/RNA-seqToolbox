package goSetEnrichment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utilities {
	
	
	
	//Do not forget the parents
	public static HashMap<String,Set<String>> getGOTerm2Gene(HashMap<String,List<String>> mapGene2GO, HashMap<String,Node> id2node){
		HashMap<String,Set<String>> mapGO2Gene = new HashMap<String,Set<String>>();
		
		for(String gene : mapGene2GO.keySet()) {
			for(String go : mapGene2GO.get(gene)) {
				//Add the one 
				if(mapGO2Gene.containsKey(go)) {
					mapGO2Gene.get(go).add(gene);
				}
				else {
					Set<String> list = new HashSet<String>();
					list.add(gene);
					mapGO2Gene.put(go, list);
				}
				//Add to all the parents 
				if(id2node.containsKey(go)) {
				if(!id2node.get(go).parents.isEmpty() ) {
				for(Node parent : id2node.get(go).parents) {
					if(mapGO2Gene.containsKey(parent.id)) {
						mapGO2Gene.get(parent.id).add(gene);
					}
					else {
						Set<String> list = new HashSet<String>();
						list.add(gene);
						mapGO2Gene.put(parent.id, list);
					}
				}
				}
				}
				else {
					//System.out.println(go);
				}
				
				
			}
			
		}
		
		
		
		return mapGO2Gene ; 
	}

}
