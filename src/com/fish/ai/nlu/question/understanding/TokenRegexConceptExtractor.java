package com.fish.ai.nlu.question.understanding;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.CoreMapExpressionExtractor;
import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.ling.tokensregex.MatchedExpression;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

/**
 * @author Samrat.saha
 * fish.ai
 * @email iitr.samrat@gmail.com
 */

public class TokenRegexConceptExtractor {
	private static CoreMapExpressionExtractor<MatchedExpression> extractor;
	private static Env env;




	public static void extractConcepts(Annotation annotation) {
		boolean flag = Boolean.FALSE;
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		int id = 0;
		for (CoreMap sentence : sentences) {
			System.out.println(sentence.toString());
			List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
			id++;
			List<MatchedExpression> matchedExpressions = extractor.extractExpressions(sentence);
			/*
			for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
				// Print out words, lemma, ne, and normalized ne
				String word = token.get(CoreAnnotations.TextAnnotation.class);
				String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
				String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
				String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
				String normalized = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
				System.out.println("token: " + "word="+word + ", lemma="+lemma + ", pos=" + pos + ", ne=" + ne + ", normalized=" + normalized);
			}
			*/


			for (MatchedExpression matched:matchedExpressions) {
				// Print out matched text and value
				System.out.println("--------------------");
				//System.out.println(matched.getText());
				//System.out.println(matched.getValue().toString());

				String subj = "";
				String obj = "";

				CoreMap cm = matched.getAnnotation();
				String matchedText = matched.getValue().toString();
				String matchedTextMod = matchedText.replace("(", " ").replace(")", "").replace("STRING", "");
				//System.out.println(matchedTextMod);
				StringTokenizer st = new StringTokenizer(matchedTextMod);
				String predicate = st.nextToken("##").trim();
				subj = st.nextToken("##").trim();
				obj = st.nextToken("##").trim().replace("-LRB-", "(").replace("-RRB-", ")");
				if(obj.substring(0, obj.length()/2).replaceAll("\\s|\\W", "").equalsIgnoreCase(obj.substring(obj.length()/2, obj.length()).replaceAll("\\s|\\W", ""))){
					obj = obj.substring(0, obj.length()/2);
				}

				System.out.println(subj + "->" + predicate + "->" + obj);
				System.out.println("--------------------\n");

			}
			//			}
		}



		annotation = null;
		return;
	}


	public static void main(String[] args) throws Exception {
		String rules = "src/main/resources/tokenregexrule.txt";

		String exampleQuestions = IOUtils.stringFromFile("src/main/resources/question.txt");

		Properties properties = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,ner,regexner");
		properties.setProperty("ssplit.eolonly", "true");
		properties.setProperty("regexner.mapping", "src/main/resources/movieregex.txt");
		properties.setProperty("tokensregex.extractor.rules", rules);
		StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

		Annotation annotation = new Annotation(exampleQuestions);
		env = TokenSequencePattern.getNewEnv();
		extractor = CoreMapExpressionExtractor.createExtractorFromFiles(env, properties.getProperty("tokensregex.extractor.rules"));

		pipeline.annotate(annotation);
		extractConcepts(annotation);

	}




}