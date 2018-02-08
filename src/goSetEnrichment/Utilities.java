package goSetEnrichment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import plots.Pair;

public class Utilities {
	
	static int LevelParent = 0 ; 
	
	public static Pair<Pair<Integer,String>,Pair<HashMap<String,String>, HashMap<String,String>>> get_sp(Node node1,Node node2, HashMap<String,Node> id2node) {
		
	
		List<String> parents2 = new ArrayList<String>();
		HashMap<String,Integer>  map2 = new HashMap<String,Integer>();
		HashMap<String, String> mapVal2 = new HashMap<String, String>();
		getParents(node2, parents2,map2,mapVal2);
		
		List<String> parents1 = new ArrayList<String>();
		HashMap<String,Integer>  map1 = new HashMap<String,Integer>();
		HashMap<String, String> mapVal1 = new HashMap<String, String>();
		getParents(node1, parents1,map1, mapVal1);
	
		
		//find LCAs
		List<String> lca  = new ArrayList<String>();
		for(int i = 0 ; i < parents1.size(); i ++ ) {
			if(parents2.contains(parents1.get(i))){
				lca.add(parents1.get(i));
						
			}
		}
		
		int min = Integer.MAX_VALUE;
		String officialLca = null ;
		
		
		boolean inLine = false; 
		if(parents1.contains(node2.id)) {
			min=map1.get(node2.id);
			officialLca = node2.id;
			inLine= true; 
		}else if(parents2.contains(node1.id)) {
			min=map2.get(node1.id);
			officialLca = node1.id;
			inLine = true; 
		}
		
		//find both SP to LCA
		if(!inLine) {
			int path1 = Integer.MAX_VALUE;
			int path2 = Integer.MAX_VALUE;

			for(String candidateLca : lca ) {
				
				path1 = map1.get(candidateLca);
				path2 = map2.get(candidateLca);
		
	//			if(path1==0||path2==0) {
	//				
	//				if(min>path1 +path2) {
	//					min=path1+path2;
	//					officialLca = candidateLca; 
	//				
	//				}	
	//			}else {
					if(min>path1 +path2-1) {
						min=path1+path2-1;
						officialLca = candidateLca; 
						
					}
	//			}				
			}
		}
		
		Pair<Integer,String> one = new Pair<Integer,String>(min,officialLca);
		Pair<HashMap<String,String>, HashMap<String,String>> two = new Pair<HashMap<String,String>, HashMap<String,String>>(mapVal1, mapVal2);
	
		
		
		return  new Pair(one,two); 
	}
	

	public static List<String> getPathLca(Node start, Node lca, Integer length){
		List<String> temp = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		rec(start,start, lca,length,length, temp,list);
		return list; 
	}
	
	
	

	
	boolean flag = false; 
	static int y = 0 ;
	public static void rec(Node start,Node s, Node stop,Integer l,Integer path,  List<String> list, List<String> t) {
		
		y++;
		at:for(Node node : start.parents) {
		
			if(l==0) {
				list=new ArrayList<String>();
				y=1;
				break at;
			}
			//System.out.println("p");
			//System.out.println("--: "+ node.id+" lev: "+ l + " "+ y );
			list.add(node.id);
			
			
			
			if(node.equals(stop)) {
				for(int i = 0 ; i < path ;i++ ) {
					t.add(list.get(list.size()-path+i));
				}
				return; 
			}
			
			rec(node,s,stop,l-1,path,list,t);
		}
		
	}
	
	
	
	public static List<String> getParentsEasy(Node node) {
		LevelParent=0;
		List<Node> parentsToVerify = new ArrayList<Node>();
		parentsToVerify.addAll(node.parents);
		
		List<String> list = new ArrayList<String>();
		list.add(node.id);
		
		
		List<Node> nextLevelParents = new ArrayList<Node>();
		while(!parentsToVerify.isEmpty()) {
				
			nextLevelParents=new ArrayList<Node>();
			
			for(Node parent : parentsToVerify) {
				
				if(!list.contains(parent.id)) {					
					
					list.add(parent.id);
					nextLevelParents.addAll(parent.parents);
					
				}
				
			}
			
				
			parentsToVerify.clear();
			parentsToVerify.addAll(nextLevelParents);
			
		}	
	
		return list;
	}
	

	
	public static void getParents(Node node, List<String> list,HashMap<String, Integer> listLevel, HashMap<String, String> map) {
		LevelParent=0;
		List<Node> parentsToVerify = new ArrayList<Node>();
		parentsToVerify.addAll(node.parents);
		
		listLevel.put(node.id, LevelParent);
		list.add(node.id);
		
		for(Node n : node.parents) {
			map.put(n.id, node.id);
		}
		
		
		List<Node> nextLevelParents = new ArrayList<Node>();
		
		
		while(!parentsToVerify.isEmpty()) {
			LevelParent++;
				
			nextLevelParents=new ArrayList<Node>();
			
			for(Node parent : parentsToVerify) {
				
				if(!list.contains(parent.id)) {					
					listLevel.put(parent.id, LevelParent);
					list.add(parent.id);
					nextLevelParents.addAll(parent.parents);
					
					for(Node n : parent.parents) {
					
						map.put(n.id, parent.id);
					}
					
				}
				
			}
			
			
			
			parentsToVerify.clear();
			parentsToVerify.addAll(nextLevelParents);
			
		}	
		
		
		
			
		
	
		
	}
	
	
	

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


public static void addGOToParents(String go,HashMap<String,Node> id2node, HashMap<String,Set<String>> mapGO2Gene, String gene) {
	
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


	

	


	

	public static void getParentsSimple(Node node, Set<String> list){
		
		if(!node.parents.isEmpty()) {
			for(Node  n : node.parents) {
				list.add(n.id);
				getParentsSimple(n,list);
			}
		}

		
	}
	

	
	

	public static Boolean is_relative(String term1, String term2 , HashMap<String,Node> id2node) {
		
		
		if(is_descendent(term1,term2,id2node) || is_descendent(term2,term1,id2node)) {
			return true; 
		}
		
		return false; 
	}
	
	
	public static Boolean is_descendent(String term1, String term2 , HashMap<String,Node> id2node) {
		

		Set<String> parent1 = new HashSet<String>();
		getParentsSimple(id2node.get(term1),parent1);
		if(parent1.contains(term2)) {
			return true; 
		}
		return false; 
		
	}
}

