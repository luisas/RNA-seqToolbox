package goSetEnrichment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import plots.Pair;

public class Parsers {
	
	
	public static Pair<HashMap<String, Pair<Double, Boolean>>,List<String>> parseEnrich(String filename) {
		
		HashMap<String, Pair<Double, Boolean>> map = new HashMap<String, Pair<Double, Boolean>>();
		List<String> simulIds = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			
			while ((currentLine = br.readLine()) != null ) {
			
				//header
				if(currentLine.startsWith("id")) {
					continue;
				}
				String id =null; 
				String fc = null ; 
				String signif=null ; 
				
				StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
				int i = 0 ;
		        while (defaultTokenizer.hasMoreTokens()){
		        	String key = defaultTokenizer.nextToken();
		        	if(currentLine.startsWith("#")) {
		        		simulIds.add(currentLine.replaceAll("#", ""));
		        	}	
		        	else {
			        	if(i==0) {
			        		id = key; 
			        	}else if(i==1) {
			        		fc=key; 
			        	}else if(i==2) {
			        		signif=key;
			        		
			  
			        		//SAVE!!
			       map.put(id, new Pair<Double,Boolean>(Double.parseDouble(fc),Boolean.valueOf(signif)));
			        		
		
			        		fc=null;
			        		signif=null;
			        		id=null;
			        		//check that they are not empy, if they are --> ignore!!
			        		break; 
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
		return new Pair<HashMap<String, Pair<Double, Boolean>>,List<String>>(map,simulIds);
		
	}
	
	
	public static HashMap<String,List<String>>  parseEnsembl(String filename) {
		HashMap<String,List<String>> mappingMap = new HashMap<String,List<String>>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null ) {
			

				String hgnc = null ; 
				List<String> gos = new ArrayList<String>();; 

		        	String[]  split = currentLine.split("\t");
		        	hgnc = split[1] ;
			        		
			    String key = split[2];
			    StringTokenizer gosTokenizer = new StringTokenizer(key,"|");
			    while (gosTokenizer.hasMoreTokens()){
			  		gos.add(gosTokenizer.nextToken());    	
			    }
			        		
			   if(!hgnc.equals("")) {
				   if(mappingMap.containsKey(hgnc)) {
					   mappingMap.get(hgnc).addAll(gos);
				   }else {
					   mappingMap.put(hgnc, gos);
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
		
		return mappingMap;

		
	}
	
	public static HashMap<String,List<String>> parseGaf(String filename) {
		
		HashMap<String,List<String>> map = new HashMap<String,List<String>>();
		String encoding="UTF-8";
		try {
			InputStream fileStream = new FileInputStream(filename);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader decoder;
			decoder = new InputStreamReader(gzipStream, encoding);
			BufferedReader br = new BufferedReader(decoder);
			String currentLine;
			
			while ((currentLine = br.readLine()) != null ) {
				if(!currentLine.startsWith("!")) {
					String geneId =null; 
					String goCat = null; 
					
					String[] split = currentLine.split("\t");
				
					if(split[3].equals("")) {
						geneId = split[2]; 
						goCat=split[4];
						
						if(map.containsKey(geneId)) {
							map.get(geneId).add(goCat);
						}else {
							List<String> list = new ArrayList<String>();
							list.add(goCat);
							map.put(geneId, list);
						}
						
					}
				}
				
			}
			br.close();

			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map ; 
	}
	
	public static Pair<DAG,HashMap<String,Node>> parseObo(String filename, String root) {
		
		DAG dag = null;
		String name=null; 
		String namespace=null; 
		String id=null; 
		List<String> isa = new ArrayList<String>();
		boolean obsolete = true;
		boolean term = false; 
		boolean rightRoot= false; 
		HashMap<String,Node> id2node = new HashMap<String, Node>();
		HashMap<String,List<String>> id2parentsids = new HashMap<String, List<String>>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null ) {
				//BEGIN OF ONE TERM 
				if(currentLine.startsWith("[Term]")) {
					term =true;
					
					if(!obsolete && rightRoot) {
						
						id2node.put(id, new Node (id,name,new ArrayList<Node>()));
						id2parentsids.put(id, isa);
						
					}
					obsolete=false; 
					name=null;
					namespace=null;
					id=null;
					isa = new ArrayList<String>();
					rightRoot=false; 
				}
				else if(term) {
					if(currentLine.startsWith("id:")) {id = currentLine.replaceAll("id:", "").replaceAll("^ ", "").replaceAll(" $", ""); }
					else if(currentLine.startsWith("name:")) {name = currentLine.replaceAll("name:", "").replaceAll("^ ", "").replaceAll(" $", "");}
					else if(currentLine.startsWith("namespace:")) {namespace = currentLine.replaceAll("namespace:", "").replaceAll("^ ", "").replaceAll(" $", "");
						if(namespace.equals(root)) {
							rightRoot = true; 
						}
					}
					else if(currentLine.startsWith("is_a")) {isa.add(currentLine.split(" ")[1]);}
					else if(currentLine.startsWith("is_obsolete")) {obsolete=true;}
					
				}
			}
			if(!obsolete && rightRoot) {
	 
				id2node.put(id, new Node (id,name,new ArrayList<Node>()));
				id2parentsids.put(id, isa);
				
			}
			
			//Here finished reading!
			//update all nodes!
			for(String nodeID : id2node.keySet()) {
				for(String parentID : id2parentsids.get(nodeID)) {
					id2node.get(nodeID).parents.add(id2node.get(parentID));		
				}
			}
			
			
			
			
			
			
			
			for(Node node : id2node.values()) {
				if(node.parents.isEmpty())
				{
					dag= new DAG(namespace, node);
					//System.out.println("roooot: "+node.id);
					return  new Pair(dag,id2node); 
				
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("PROBLEM WITH OBO2DAG parser!!!");
		return new Pair(dag,id2node); 
	}
	
	
	

}
