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
				addGeneToParents(go, id2node,  mapGO2Gene,  gene);
				

			}	
		}
		
		return mapGO2Gene ; 
	}




//Propagation to parents
public static void addGeneToParents(String go,HashMap<String,Node> id2node, HashMap<String,Set<String>> mapGO2Gene, String gene) {
		
		if(id2node.containsKey(go) && !id2node.get(go).parents.isEmpty() ) {
		
			for(Node parent : id2node.get(go).parents) {
				
				if(mapGO2Gene.containsKey(parent.id)) {
					mapGO2Gene.get(parent.id).add(gene);
				}
				else {
					Set<String> list = new HashSet<String>();
					list.add(gene);
					mapGO2Gene.put(parent.id, list);
				}
				
				addGeneToParents(parent.id, id2node,  mapGO2Gene,  gene);
				
			}
		}
 }



	public static Integer path_length(String term1, String term2, HashMap<String,Node> id2node) {
		
		
		
		return 0; 
	}



	public static Boolean is_relative(String term1, String term2 , HashMap<String,Node> id2node) {
		
		
		if(is_descendent(term1,term2,id2node) || is_descendent(term2,term1,id2node)) {
			return true; 
		}
		
		return false; 
	}
	
	
	public static Boolean is_descendent(String term1, String term2 , HashMap<String,Node> id2node) {
		
		if(!id2node.get(term1).parents.isEmpty()) {
			for(Node parent : id2node.get(term1).parents) {
				if(parent.id.equals(term2)) {
					return true; 
				}
				is_descendent(parent.id, term2,id2node);
			}
		}
		return false; 
		
	}
}

