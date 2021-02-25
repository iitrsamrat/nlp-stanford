package com.fish.ai.nlp.sentiment;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;



/*
 * @author samrat.saha
 * @email iitr.samrat@gmail.com
 */


public class SentimentMain {

	public static void main(String[] args) {
		String line = "...Remember this. Throughout the ages some things NEVER get better and NEVER change. You have Walls and you have Wheels. It was ALWAYS that way and it will ALWAYS be that way! Please explain to the Democrats that there can NEVER be a replacement for a good old fashioned WALL!";

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		if (line != null && line.length() > 0) {
			Annotation annotation = pipeline.process(line);
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				System.out.println(sentence);
				System.out.println("  " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));

				Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				System.out.println(tree);
				System.out.println(" -------- ");
			}


		}

	}

}
