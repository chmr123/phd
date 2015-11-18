package tf.autoodc.pivot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.opencsv.CSVReader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;

public class Documents {

	Stemmer stemmer = new Stemmer();

	public LinkedHashMap<String, ArrayList<String>> getTokenAndPOSTag() {
		LinkedHashMap<String, ArrayList<String>> token_type = new LinkedHashMap<String, ArrayList<String>>();
		return token_type;
	}

	public String getPOSTag(String text, MaxentTagger tagger) {

		String tagged = tagger.tagString(text);
		return tagged;
	}

	public Map<String, String[]> getTextFromFile(String[] files) throws IOException {
		Map<String, String[]> alltext = new HashMap<String, String[]>();
		for (String filename : files) {
			// System.out.println("Working on dataset " + filename);
			CSVReader reader = new CSVReader(new FileReader(filename));

			String[] nextLine;

			while ((nextLine = reader.readNext()) != null) {
				String line = nextLine[0].toLowerCase() + " " + nextLine[1].toLowerCase();

				// No stem at this time
				/*
				 * String[] split = line.split("\\s+"); String
				 * line_after_stemmed = ""; for(String s : split){
				 * line_after_stemmed = line_after_stemmed + stem(s) + " "; }
				 */
				String[] category_file = new String[2];
				category_file[0] = nextLine[2];
				category_file[1] = filename;
				alltext.put(line, category_file);
			}
			reader.close();
		}

		return alltext;
	}

	public Map<String, Set<String>> getTextAndKeywordsFromFile(String[] files) throws IOException {
		Map<String, Set<String>> alltext = new HashMap<String, Set<String>>();
		for (String filename : files) {
			// System.out.println("Working on dataset " + filename);
			CSVReader reader = new CSVReader(new FileReader(filename));

			String[] nextLine;

			while ((nextLine = reader.readNext()) != null) {
				String line = nextLine[0].toLowerCase() + " " + nextLine[1].toLowerCase();

				// No stem at this time
				/*
				 * String[] split = line.split("\\s+"); String
				 * line_after_stemmed = ""; for(String s : split){
				 * line_after_stemmed = line_after_stemmed + stem(s) + " "; }
				 */
				Set<String> splittedKeywords = new HashSet<String>();
				String[] keywords = nextLine[2].split(";");
				for (String term : keywords) {
					String[] split = term.split("\\s+");
					for (String s : split) {
						splittedKeywords.add(s);
					}
				}
				alltext.put(line, splittedKeywords);
			}
			reader.close();
		}

		return alltext;
	}

	private String lemmatize(String text) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		// props.setProperty("annotators", "lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		String lemma = null;
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				lemma = token.get(LemmaAnnotation.class);	
			}
		}
		return lemma;
	}
	/*
	 * private String stem(String term){ char[] chars = term.toCharArray(); for
	 * (char c : chars) stemmer.add(c); stemmer.stem(); String stemmed =
	 * stemmer.toString(); return stemmed; }
	 */
}
