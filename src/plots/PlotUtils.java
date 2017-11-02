package plots;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

public class PlotUtils {



	public static void getCumHist(Vector<Double> vector, String title, String xlab, String ylab, String outfile){

		LinePlot lp_exons = new LinePlot(title, xlab, ylab);
		Pair<Vector<Double>,Vector<Double>> p = PlotUtils.ecdf(vector);
		lp_exons.setX(p.first);
		lp_exons.setY(p.second);
		lp_exons.plot(outfile);


	}

	public static Pair<Vector<Double>,Vector<Double>> ecdf(Vector<Double> vector){

		Vector<Double> x = new Vector<Double>();
		Vector<Double> y = new Vector<Double>();


		Collections.sort(vector);

		double sum = 0;
		double tempValue = 0 ;
		boolean firstRound = true;
		int count = 0;
		for(Double d: vector){
			count ++;

			if(firstRound){
				tempValue = d;
				sum++;
				firstRound= false;
				continue;

			}else if (tempValue != d){

				x.add(tempValue);
				y.add(sum);
				tempValue = d;

			}
			sum++;
			if(count == vector.size()){
				x.add(d);
				y.add(sum);
				break;
			}


		}

		Collections.sort(x);



		Pair<Vector<Double>,Vector<Double>> result = new Pair<Vector<Double>,Vector<Double>>(x,y);
		return result;
	}


	public static void writeVector( Vector<Double> x, File file) {

		FileWriter fos;
		try {
			fos = new FileWriter(file);
			PrintWriter dos = new PrintWriter(fos);
			StringBuilder sb = new StringBuilder();

			String prefix = "";
			for(Double d: x){

				sb.append(prefix);
				sb.append(d.toString());
				prefix= "\t";
			}
			dos.print(sb);
			dos.close();


		} catch (IOException e) {
			System.out.println("Error writing the temp file");
			e.printStackTrace();
		}




	}

	public static void writeVector( Vector<Double> x, File file, Boolean bol) {

		FileWriter fos;
		try {
			fos = new FileWriter(file, bol);
			PrintWriter dos = new PrintWriter(fos);
			StringBuilder sb = new StringBuilder();

			dos.print("\n");
			String prefix = "";
			for(Double d: x){

				sb.append(prefix);
				sb.append(d.toString());
				prefix= "\t";
			}
			dos.print(sb);
			dos.close();


		} catch (IOException e) {
			System.out.println("Error writing the temp file");
			e.printStackTrace();
		}


	}





}
