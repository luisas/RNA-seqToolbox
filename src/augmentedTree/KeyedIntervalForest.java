package augmentedTree;

import java.util.Collection;
import java.util.Vector;
import java.util.function.Function;


public class KeyedIntervalForest<A extends Interval ,B > extends IntervalTree{
	

	Function<A,B> key_getter;
	Function<A,Integer> start_getter ;
	Function<A,Integer> end_getter;
	
	public LIntervalTree<A> factory ; 
	
	
	public KeyedIntervalForest(Function<A,B> key_getter, Function<A,Integer> start_getter, Function<A,Integer> end_getter) {
		this.key_getter = key_getter;
		factory = new LIntervalTree(start_getter,end_getter);
	}
	
	
	
	public void add(A a) {
		
		factory.add(new AInterval(a, start_getter.apply(a), end_getter.apply(a)));
		
	}

	public Vector<A> getIntersecting(String chr, int start, int stop){

		Vector<AInterval<A>> is = new Vector<AInterval<A>>();
		Collection<AInterval<A>> c = factory.tree.getIntervalsIntersecting(start, stop, is);
		Vector<A> genes = new Vector<A>();
		for(AInterval<A> a :c) {
			String chr_local = (String) key_getter.apply(a.getA());
			if(chr_local.equals(chr)) {
				genes.add(a.getA());

			}
		}
		
		return genes;
		
 	}	
	
	
	
	
//	public static void main(String[] args) {
//		
//		System.out.println("");
//
//		IntervalTree it = new IntervalTree();
//        = new KeyedIntervalForest<>( (g)-> g.getChr(), (g)->g.getStart(), (g)->g.getStop());	
//        apply(getGenes(),(_g)-> lookup.add(_g));
//        
//		
//	}
	

	
	
	
	
}
