package tf.autoodc.tm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoODC_TM {

	public static void main(String[] args) throws IOException, InterruptedException {
		String category = "";
		String training = "";
		String testing = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-c")) {
				category = args[i + 1];
			}
			if (args[i].equals("-train")) {
				training = args[i + 1];
			}
			if (args[i].equals("-test")) {
				testing = args[i + 1];
			}

		}
		
		FileWriter fw1 = new FileWriter(category + ".train");
		FileWriter fw2 = new FileWriter(category + ".test");

		System.out.println("Reading " + training);
		writeInstance(training, category, fw1);
		System.out.println("Reading " + testing);
		writeInstance(testing, category, fw2);

		fw1.flush();
		fw2.flush();
		fw1.close();
		fw2.close();

		svm(category);
	}

	private static void writeInstance(String filename, String category, FileWriter fw) throws IOException {
		File file = new File(filename);

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;

		while ((line = br.readLine()) != null) {
	
			int label = 0;
			String[] features = line.split(",");
			int arrayLength = features.length;
			int classIndex = arrayLength - 1;
			if (features[classIndex].equals(category))
				label = 1;
			else
				label = -1;
			fw.write(label + " ");
			for (int i = 0; i < arrayLength - 1; i++) {
				int index = i + 1;
				fw.write(index + ":" + features[i] + " ");
			}
			fw.write("\n");
		}

		br.close();
	}

	private static void svm(String category) throws IOException, InterruptedException {
		// Removing temp training and testing data files

		// Train a prediction model
		ProcessBuilder pr1 = new ProcessBuilder("./svm-train", "-q", "-c", "10000", "-b", "1", category+".train", "model");
		pr1.directory(new File("."));
		Process p = pr1.start();
		p.waitFor();

		// Test a prediction model
		ProcessBuilder pr2 = new ProcessBuilder("./svm-predict", "-q", "-b", "1", category+".test", "model",
				category + ".output");
		pr2.directory(new File("."));
		p = pr2.start();
		p.waitFor();

	}

}
