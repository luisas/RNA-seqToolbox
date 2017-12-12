package augmentedTree;

import java.util.Collection;
import java.util.Vector;
import java.util.function.Function;


public class LIntervalTree<A extends Interval> extends IntervalTree {
	
	Function<A,Integer> start_getter ;
	Function<A,Integer> end_getter;
	public IntervalTree tree = new IntervalTree();

	
	
	public LIntervalTree() {
		super();
		this.start_getter= a->a.getStart();
		this.end_getter= a->a.getStop();
		// TODO Auto-generated constructor stub
	}


	public LIntervalTree(Collection coll) {
		super(coll);
		// TODO Auto-generated constructor stub
	}


	public LIntervalTree(Function<A,Integer> start_getter, Function<A,Integer> end_getter) {
		this.start_getter = start_getter;
		this.end_getter= end_getter;
	}
	

	public void add(A a) {
		tree.add(new AInterval(a, start_getter.apply(a), end_getter.apply(a)));
	}

	public Vector<A> getIntersecting(A a){
		return getIntersecting(start_getter.apply(a), end_getter.apply(a));
	}
	
	public Vector<A> getIntersecting(int start, int stop){
		Vector<AInterval<A>> is = new Vector<AInterval<A>>();
		Collection<AInterval<A>> c = tree.getIntervalsIntersecting(start, stop, is);
		Vector<A> genes = new Vector<A>();
		for(AInterval<A> a :c) {
			genes.add(a.getA());
		}
		
		return genes;
 	}






}
