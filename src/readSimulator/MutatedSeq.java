package readSimulator;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class MutatedSeq {


	private String sequence;
	private TreeSet<Integer> positions = new TreeSet<Integer>();

	int nMut ;
	static Set<Character> na ;


	public MutatedSeq(String sequence, int nMut) {
		super();
		this.sequence = sequence;
		this.nMut = nMut;

		na = new HashSet<Character>();
		na.add('A');
		na.add('C');
		na.add('G');
		na.add('T');

		if(sequence.length()<nMut){
			System.out.println("Number of mutations higher than the length of the read itself!!!");
			System.exit(0);
		}


		while(positions.size()<nMut){

			int posMutation  = (int)(Math.random()*(sequence.length()-1) );
			sequence= mutate(sequence,posMutation,na);
			positions.add((int)(posMutation));


		}

		this.sequence = sequence;


	}


//	public static void main(String[] args){
//
//
//		MutatedSeq m = new MutatedSeq("AAAAAAAAAAAAAAAAAA",5);
//
////		for(Integer i : m.getPositions()){
////			System.out.println(i);
////		}
//		//String a = "ACTGACTG";
//		//int pos = 0;
//		//System.out.println(a);
//		//System.out.println(mutate(a,pos,na));
//
//	}


	public static String mutate(String string, int position ,Set set){

		char[] charArray = string.toCharArray();

		char mutation = string.charAt(position);
		while(mutation==string.charAt(position)){
			mutation=getMutChar(set);

		}
	    charArray[position] = mutation;
	    return new String(charArray);


	}


	public String getSequence() {
		return sequence;
	}


	public void setSequence(String sequence) {
		this.sequence = sequence;
	}


	public TreeSet<Integer> getPositions() {
		return positions;
	}


	public void setPositions(TreeSet<Integer> positions) {
		this.positions = positions;
	}

	public static Character getMutChar(Set<Character> set){
		int size = set.size();
		int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
		int i = 0;
		char result ='-';
		for(Character obj : set)
		{
		    if (i == item){
		    	result=obj;
		    }

		    i++;
		}
		return result;
	}





}
