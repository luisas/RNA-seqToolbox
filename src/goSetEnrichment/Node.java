package goSetEnrichment;

import java.util.List;

public class Node{
	List<Node> parents;
	String id;
	String name;
	
	
	public Node( String id, String name,List<Node> parents) {
		super();
		this.parents = parents;
		this.id = id;
		this.name = name;
	}
	
	
	
	
}
	