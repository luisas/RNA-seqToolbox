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
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

public class Parsers {
	
	
	public static void parseEnrich(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			List<String> simulIds = new ArrayList<String>();
			while ((currentLine = br.readLine()) != null ) {
			
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
			        		//System.out.println(currentLine);
			        		id = key; 
			        	}else if(i==1) {
			        		fc=key; 
			        	}else if(i==2) {
			        		signif=key;
			        		
			  
			        		//SAVE!!
			        		System.out.println(id+"\t"+fc+"\t"+signif);
			        		
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
		
	}
	
	public static void parseEnsembl(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null ) {
			
				String id =null; 
				String hgnc = null ; 
				List<String> gos = new ArrayList<String>();; 
				StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
				int i = 0 ;
		        while (defaultTokenizer.hasMoreTokens()){
		        	String key = defaultTokenizer.nextToken();
		        	if(!currentLine.startsWith("#")) {
			        	if(i==0) {
			        		//System.out.println(currentLine);
			        		id = key; 
			        	}else if(i==1) {
			        		hgnc=key; 
			        	}else if(i==2) {
			        		StringTokenizer gosTokenizer = new StringTokenizer(key,"|");
			        		while (gosTokenizer.hasMoreTokens()){
			  		        	gos.add(gosTokenizer.nextToken());
			  		        	
			        		}
			  
			        		//SAVE!!
			        		System.out.println(id+"\t"+hgnc+"\t"+gos.size());
			        		gos = new ArrayList<String>();
			        		hgnc=null; 
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
		
	}
	
	public static void parseGaf(String filename) {
		String encoding="UTF-8";
		try {
			InputStream fileStream = new FileInputStream(filename);
			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader decoder;
			decoder = new InputStreamReader(gzipStream, encoding);
			BufferedReader br = new BufferedReader(decoder);
			String currentLine;
			
			while ((currentLine = br.readLine()) != null ) {
				//System.out.println(currentLine);
				if(!currentLine.startsWith("!")) {
					String geneId =null; 
					String aqm = null ; 
					String goCat = null; 
					StringTokenizer defaultTokenizer = new StringTokenizer(currentLine);
					int i = 0 ;
			        while (defaultTokenizer.hasMoreTokens()){
			        	String key = defaultTokenizer.nextToken();
			        	if(i==2) {
			        		geneId = key; 
			        	}else if(i==3) {
			        		aqm=key; 
			        	}else if(i==4) {
			        		goCat=key;
			        	}
			        	i++;
			        	if(i==5) {
			        		//SAVE!!!
			        		System.out.println(geneId+"\t"+aqm+"\t"+goCat);
			        		break;
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
	}
	
	public static DAG parseObo(String filename) {
		
		DAG dag = new DAG();
		
		
		String name=null; 
		String namespace=null; 
		String id=null; 
		List<String> isa = new ArrayList<String>();
		boolean obsolete = true;
		boolean term = false; 
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String currentLine;
			while ((currentLine = br.readLine()) != null ) {
				//BEGIN OF ONE TERM 
				if(currentLine.startsWith("[Term]")) {
					term =true;
					
					if(!obsolete) {
						//SAVE
					}
					obsolete=false; 
					name=null;
					namespace=null;
					id=null;
					isa = new ArrayList<String>();
				}
				else if(term) {
					if(currentLine.startsWith("id:")) {id = currentLine.replaceAll("id:", "").replaceAll("^ ", "").replaceAll(" $", ""); }
					if(currentLine.startsWith("name:")) {name = currentLine.replaceAll("name:", "").replaceAll("^ ", "").replaceAll(" $", "");}
					if(currentLine.startsWith("namespace")) {name = currentLine.replaceAll("namespace:", "").replaceAll("^ ", "").replaceAll(" $", ""); }
					if(currentLine.startsWith("is_a")) {System.out.println(currentLine.split(" ")[1]);}
					if(currentLine.startsWith("is_obsolete")) {obsolete=true;}
					
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return dag; 
	}
	
	
	

}
