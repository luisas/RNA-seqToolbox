package website;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class UtilsWebsite {


	 static final String dir = System.getProperty("user.dir");

	static File header = new File(dir+"/src/website/header.txt");
	static File suffix = new File(dir+"/src/website/suffix.txt");



	public static void generateTopGenes(Vector<String> geneids, String img1,String img2, String filename){

		File html = new File(filename);

		FileWriter fos;
		try {
			fos = new FileWriter(html);
			PrintWriter dos = new PrintWriter(fos);


			// preprend header
			try (BufferedReader br = new BufferedReader(new FileReader(header))) {
			String currentLine;
				while ((currentLine = br.readLine()) != null ) {
					dos.print(currentLine);
				}
			}


			//---------CONTENT
			
			dos.println("<img src=\""+img1+"\"height=\"242\" width=\"242\">");
			dos.println("<br>");
			dos.println("<img src=\""+img1+"\"height=\"242\" width=\"242\">");
			dos.println("<br>");
			for(String id: geneids){
				dos.println("<a href=\"http://www.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g="+id+"\">"+ id+ "</a><br>");
			}
			
			



			//append suffix
			try (BufferedReader br = new BufferedReader(new FileReader(suffix))) {
			String currentLine;
				while ((currentLine = br.readLine()) != null ) {
					dos.print(currentLine);
				}
			}

			fos.close();
			dos.close();

			System.out.println("done");





		} catch (IOException e) {
			System.out.println("Error generating the HTML file");
			e.printStackTrace();
		}








	}


	public static void main(String[] args){

		System.out.println(header.getAbsolutePath());

		String path = "/Users/luisasantus/eclipse-workspace/RNA-seqToolbox/src/website/summary.html";
		Vector ids = new Vector();
		ids.add("id1");
		ids.add("id2");
		String img1= "/Users/luisasantus/Desktop/GoBi/img.jpeg";
		generateTopGenes(ids,img1,img1 , path);



	}

}
