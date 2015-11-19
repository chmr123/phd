package tf.autoodc.pivot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class Pivot {
	static int window_size = 3;
	Set<String> pivots = new HashSet<String>();
	Map<String, Set<String>> alltextKeyWords;
	Map<String, String[]> alltextOriginal;

	public Pivot(Map<String, Set<String>> alltextKeyWords,
			Map<String, String[]> alltextOriginal) {
		this.alltextKeyWords = alltextKeyWords;
		this.alltextOriginal = alltextOriginal;
	}

	public void getPivotFeature(Map<String[], String> keywords_map) {
		for (String keys[] : keywords_map.keySet()) {
			for (String key : keys) {
				pivots.add(key); // add each key word to pivot ArrayList as base
									// feature vector
			}
		}
	}

	public Map<String, ArrayList<String>> extendFeatureSpace() {
		Map<String, ArrayList<String>> keywords_extension = new LinkedHashMap<String, ArrayList<String>>();
		for (String line : alltextKeyWords.keySet()) {
			ArrayList<String> alltuples = new ArrayList<String>();
			Set<String> keywords = alltextKeyWords.get(line);
			for (String keyword : keywords) {
				ArrayList<String> tuples = new ArrayList<String>();
				tuples = getKeyWordsDependency(line, keyword);
				alltuples.addAll(tuples);
			}
			keywords_extension.put(line, alltuples);
		}
		return keywords_extension;
	}

	public Map<String, String[]> getSentenceWithPosTags() {
		Map<String, String[]> taggedSentenceMap = new LinkedHashMap<String, String[]>();
		for (String sentence : alltextOriginal.keySet()) {
			String tagged = postag(sentence);
			String[] entry = alltextOriginal.get(sentence);
			taggedSentenceMap.put(tagged, entry);
		}
		return taggedSentenceMap;
	}

	public ArrayList<String> getKeyWordsDependency(String sentence, String keyword) {
		LexicalizedParser lp = LexicalizedParser.loadModel(
				"/home/mingrui/Desktop/englishPCFG.ser.gz", "-maxLength", "80",
				"-retainTmpSubcategories");
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		// Uncomment the following line to obtain original Stanford Dependencies
		// tlp.setGenerateOriginalDependencies(true);
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		String[] array = sentence.split("\\s+");
		Tree parse = lp.apply(Sentence.toWordList(array));
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		ArrayList<String> keywordsDependency = new ArrayList<String>();
		//String lemmatizedKeyword = lemmatize(keyword);
		for (TypedDependency t : tdl) {
			String d = t.toString();
			String dependencyType = d.substring(0, d.indexOf("("));
			String pair = d.substring(d.indexOf("(") + 1, d.indexOf("("));
			String[] terms = pair.split(",");
			String term1 = terms[0].trim();
			String term2 = terms[1].trim();

			// Match keywords with the terms in the tuples, if matched, add the
			// tuple into the arraylist
			String[] wordsplitted = keyword.split(" ");
			for (String key : wordsplitted) {
				if (term1.equals(key)) {
					keywordsDependency.add(t.toString());
				}
				if (term2.equals(key)) {
					keywordsDependency.add(t.toString());
				}
			}
		}

		return keywordsDependency;
	}

	/*
	 * This function returns the tagged sentence
	 */
	private String postag(String sentence) {
		MaxentTagger tagger = new MaxentTagger(
				"taggers/left3words-distsim-wsj-0-18.tagger");
		// The tagged string
		String tagged = tagger.tagString(sentence);
		// Output the result
		return tagged;
	}

	/*
	 * This function return the lemmatized word from the original term
	 */
	private String lemmatize(String text) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
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
}
