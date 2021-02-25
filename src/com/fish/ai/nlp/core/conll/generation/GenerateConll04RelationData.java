package com.fish.ai.nlp.core.conll.generation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

/*
 * @author samrat.saha
 * @email iitr.samrat@gmail.com
 */

public class GenerateConll04RelationData {
	
	public static int GenerateConllTrainingData(Annotation annotation, String outFile, int sentenceNumber){
		int sentNum = sentenceNumber;
		String trainingContent = "";

		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			int tokenNum = 1;
			int elementNum = 0;
			int entityNum = 0;
			//System.out.println(sentence.get(CoreAnnotations.MentionsAnnotation.class).size());
			if(sentence.get(CoreAnnotations.MentionsAnnotation.class).size() > 0){
				String currNeToken = sentence.get(CoreAnnotations.NamedEntityTagAnnotation.class);
				CoreMap currEntityMention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(entityNum);
				String currEntityMentionWords = currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.word()).
						collect(Collectors.joining("/"));
				String currEntityMentionTags =
						currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.tag()).
						collect(Collectors.joining("/"));
				String currEntityMentionNER = currEntityMention.get(CoreAnnotations.EntityTypeAnnotation.class);
				String normalizedNER = currEntityMention.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
				//System.out.println(currEntityMentionNER + " normal ->" + normalizedNER);
				while (tokenNum <= sentence.get(CoreAnnotations.TokensAnnotation.class).size()) {
					if (currEntityMention.get(CoreAnnotations.TokensAnnotation.class).get(0).index() == tokenNum) {
						String entityText = currEntityMention.toString();
						//System.out.println(sentNum+"\t"+currEntityMentionNER+"\t"+elementNum+"\t"+"O\t"+currEntityMentionTags+"\t"+
						//currEntityMentionWords+"\t"+"O\tO\tO");
						trainingContent += sentNum+"\t"+currEntityMentionNER+"\t"+elementNum+"\t"+"O\t"+currEntityMentionTags+"\t"+
								currEntityMentionWords+"\t"+"O\tO\tO" + "\n";
						// update tokenNum
						tokenNum += (currEntityMention.get(CoreAnnotations.TokensAnnotation.class).size());
						// update entity if there are remaining entities
						entityNum++;
						if (entityNum < sentence.get(CoreAnnotations.MentionsAnnotation.class).size()) {
							currEntityMention = sentence.get(CoreAnnotations.MentionsAnnotation.class).get(entityNum);
							currEntityMentionWords = currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.word()).
									collect(Collectors.joining("/"));
							currEntityMentionTags =
									currEntityMention.get(CoreAnnotations.TokensAnnotation.class).stream().map(token -> token.tag()).
									collect(Collectors.joining("/"));
							currEntityMentionNER = currEntityMention.get(CoreAnnotations.EntityTypeAnnotation.class);
						}
					} else {
						CoreLabel token = sentence.get(CoreAnnotations.TokensAnnotation.class).get(tokenNum-1);
						//System.out.println(sentNum+"\t"+token.ner()+"\t"+elementNum+"\tO\t"+token.tag()+"\t"+token.word()+"\t"+"O\tO\tO");
						trainingContent += sentNum+"\t"+token.ner()+"\t"+elementNum+"\tO\t"+token.tag()+"\t"+token.word()+"\t"+"O\tO\tO" + "\n";
						tokenNum += 1;
					}
					elementNum += 1;
				}
				sentNum++;
				//if(sentNum >= 500)
				//break;
				trainingContent += "\n";
				System.err.println(trainingContent);
				System.out.println("--------------------");
			}
			//System.out.println();
			//System.out.println("O\t2\tBoard_member");
		}


		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "utf-8"))) {
			writer.write(trainingContent);
			writer.flush();
			writer.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return sentNum;
	}
	
	public static void main(String[] args) {
		
		File file = new File("src/main/resources/question.txt");
		Annotation annotation = null;
		StanfordCoreNLP pipeline;
		Properties properties = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,ner,regexner");
		properties.setProperty("ssplit.eolonly", "true");
		properties.setProperty("regexner.mapping", "src/main/resources/movieregex.txt");

		
		pipeline = new StanfordCoreNLP(properties);
		try {
			annotation = new Annotation(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
			pipeline.annotate(annotation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GenerateConllTrainingData(annotation, "src/main/resources/question.corp", 0);
	}

	
}


