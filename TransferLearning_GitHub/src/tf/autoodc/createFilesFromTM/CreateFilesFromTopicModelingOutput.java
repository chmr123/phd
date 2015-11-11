package tf.autoodc.createFilesFromTM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;

public class CreateFilesFromTopicModelingOutput {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String tm_output = "";
		int topicNum = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f")) {
				tm_output = args[i + 1];
			}
			if (args[i].equals("-n")) {
				topicNum = Integer.parseInt(args[i + 1]);
			}
		}
		CSVReader reader = new CSVReader(new FileReader(tm_output));

		String[] nextLine;

		ArrayList<double[]> featureSet_filezilla = new ArrayList<double[]>();
		ArrayList<double[]> featureSet_403 = new ArrayList<double[]>();
		while ((nextLine = reader.readNext()) != null) {
			double[] features = new double[topicNum];
			for (int i = 1; i <= topicNum; i++) {
				double v = Double.parseDouble(nextLine[i]);
				features[i-1] = v;
			}
			
			if (nextLine[0].contains("F")) {
				featureSet_filezilla.add(features);
			} 
			else{
				featureSet_403.add(features);
			}
		}
		
		//System.out.println(featureSet_filezilla.size());
		//System.out.println(featureSet_403.size());
		createFiles("filezilla", featureSet_filezilla,topicNum);
		createFiles("private", featureSet_403,topicNum);
	}
	
	private static void createFiles(String filename, ArrayList<double[]> featureSet, int topicNum) throws IOException{
			ArrayList<String> labels = getLabels(filename);
			FileWriter fw = new FileWriter(filename + "_tm_" + topicNum);
			for(int i = 0; i < featureSet.size(); i++){
				for(double f : featureSet.get(i)){
					fw.write(f + ",");
				}
				fw.write(labels.get(i) + "\n");
			}
			fw.flush();
			fw.close();

			
		
	}
	
	private static ArrayList<String> getLabels(String filename) throws IOException{
		ArrayList<String> labels = new ArrayList<String>();
		File file = new File(filename + ".label");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			labels.add(line);
		}
		br.close();
		return labels;
		
	}
}
