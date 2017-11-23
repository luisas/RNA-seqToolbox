package readSimulator;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class MutatedSeq {


	private String sequence;
	private TreeSet<Integer> positions = new TreeSet<Integer>();
	double mutationRate;

    static Set<Character> na ;


	public MutatedSeq(String sequence, double mutationRate) {
		super();
		this.sequence = sequence;
		this.mutationRate = mutationRate;

		na = new HashSet<Character>();
		na.add('A');
		na.add('C');
		na.add('G');
		na.add('T');


		//See if there is any Mutation
		Random r ;
		for(int i = 0 ; i<sequence.length(); i++){
			r = new Random();
			//0 is inclusive, 100 is exclusive
			double myRandom = r.nextInt(100);
			if(myRandom<mutationRate){
				sequence= mutate(sequence,i);
				positions.add((int)(i));
			}
		}

//		// If there was no mutation, add -1 to the positions set
//		if(positions.size()==0 ){
//			positions.add((int)(-1));
//		}

		this.sequence = sequence;

	}


//	public static void main(String[] args){
//
//
//		MutatedSeq m = new MutatedSeq("AAAAAAAAAAAAAAAAAA",5);
//
//		for(Integer i : m.getPositions()){
//			System.out.println(i);
//		}
//		String a = "ACTGACTG";
//		int pos = 0;
//		System.out.println(a);
//		System.out.println(mutate(a,pos,na));
//
//	}


	public static String mutate(String string, int position ){

		char[] charArray = string.toCharArray();

		char mutation = string.charAt(position);

		while(mutation==string.charAt(position)){
			mutation=getMutChar();
		}
	    charArray[position] = mutation;
	    return new String(charArray);


	}

	/*
	 * Gets a random character in set na
	 */
	public static Character getMutChar(){
		int item = new Random().nextInt(na.size());
		char result ='-';
		int i = 0;
		for(Character c : na)
		{
		    if (i == item){
		    	result=c;
		    }

		    i++;
		}
		return result;
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







}
