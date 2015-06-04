package cn.edu.sjtu.seiee.adapt.emr_tag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import cn.edu.sjtu.seiee.adapt.emr_tag.pattern.PatternIteration;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.CompleteSegmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.Segmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Dictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.EnrichDictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tagger;

public class App {

	static Logger logger = Logger.getLogger(App.class.getName());

	public static void main( String[] args ) {
		//String type = "\\med";
		Dictionary dict = new Dictionary();
		dict.init("res/dict/new1/");
		
		List<List<Tag>> labeled = Loader.loadLabeled("res/1-400_test_v3.txt");
		List<String> testSamples = Loader.load("res/1-400_samples.txt");
		List<List<String>> testRecords = new ArrayList<List<String>>();
		Segmenter seg = new CompleteSegmenter();
		seg.init();
		for(String s: testSamples)
			testRecords.add(seg.segment(s));
		
		Tagger tagger = new Tagger(dict);
		
		System.out.println("=====Core Dictionary======");
		printDictSize(dict);
		printResult(labeled, testRecords, tagger);
		
//		//load training data
//		List<String> samples = Loader.load("res/train.txt");
//		List<List<String>> records = new ArrayList<List<String>>();
//		for(String s: samples) {
//			records.add(seg.segment(s));
//		}
//		
//		System.out.println("=====Pattern Iteration=====");
//		PatternIteration pi1 = new PatternIteration(dict, records, 0.85, 6, 2);
//		pi1.iterate("\\med");
//		PatternIteration pi2 = new PatternIteration(dict, records, 0.7, 7, 2);
//		pi2.iterate("\\dis");
//		printDictSize(dict);
//		printResult(labeled, testRecords, tagger);
//		
//		System.out.println("=====BodyPart Prefix=====");
//		List<List<Tag>> tagged = new ArrayList<List<Tag>>();
//		for( List<String> record: records) {
//			List<Tag> tags = tagger.getTags(record);
//			tagged.add(tags);
//		}
//		EnrichDictionary.bodyPrefix(tagged, dict);
//		printDictSize(dict);
//		printResult(labeled, testRecords, tagger);
//		
//		System.out.println("=====Other Prefix=====");
//		tagged.clear();
//		for( List<String> record: records) {
//			List<Tag> tags = tagger.getTags(record);
//			tagged.add(tags);
//		}
//		EnrichDictionary.otherPrefix(tagged, samples, dict);
//		printDictSize(dict);
//		printResult(labeled, testRecords, tagger);
//		
//		write(dict.getSymptom(), "res/dict/new1/symptom.txt");
//		write(dict.getDisease(), "res/dict/new1/disease.txt");
//		write(dict.getProcedure(), "res/dict/new1/procedure.txt");
//		write(dict.getMedication(), "res/dict/new1/medication.txt");
//		write(dict.getTest(), "res/dict/new1/test.txt");
//		write(dict.getBodyPart(), "res/dict/new1/bodyPart.txt");
	}
	
	public static void write(Set<String> set, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for(String s: set)
				bw.write(s + "\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void printResult(List<List<Tag>> labeled, List<List<String>> records, Tagger tagger) {
		List<List<Tag>> tagged = new ArrayList<List<Tag>>();
		for( List<String> record: records) {
			List<Tag> tags = tagger.getTags(record);
			tagged.add(tags);
		}
		
		System.out.println("=====Symptoms=====");
		List<Double> result = new ArrayList<Double>();
		double fscore = Evaluate.getFScore(labeled, tagged, "\\sym", result);
		System.out.printf("Precision: %.3f\tRecall: %.3f\tF-Score: %.3f\n", result.get(0), result.get(1), fscore);

		System.out.println("=====Diseases=====");
		result = new ArrayList<Double>();
		fscore = Evaluate.getFScore(labeled, tagged, "\\dis", result);
		System.out.printf("Precision: %.3f\tRecall: %.3f\tF-Score: %.3f\n", result.get(0), result.get(1), fscore);
		
		System.out.println("=====Drugs=====");
		result = new ArrayList<Double>();
		fscore = Evaluate.getFScore(labeled, tagged, "\\med", result);
		System.out.printf("Precision: %.3f\tRecall: %.3f\tF-Score: %.3f\n", result.get(0), result.get(1), fscore);
		
		System.out.println("=====Body Parts=====");
		result = new ArrayList<Double>();
		fscore = Evaluate.getFScore(labeled, tagged, "\\bod", result);
		System.out.printf("Precision: %.3f\tRecall: %.3f\tF-Score: %.3f\n", result.get(0), result.get(1), fscore);
		
		System.out.println("=====Procedures=====");
		result = new ArrayList<Double>();
		fscore = Evaluate.getFScore(labeled, tagged, "\\pro", result);
		System.out.printf("Precision: %.3f\tRecall: %.3f\tF-Score: %.3f\n", result.get(0), result.get(1), fscore);
		
		System.out.println("=====Tests=====");
		result = new ArrayList<Double>();
		fscore = Evaluate.getFScore(labeled, tagged, "\\tes", result);
		System.out.printf("Precision: %.3f\tRecall: %.3f\tF-Score: %.3f\n", result.get(0), result.get(1), fscore);
	}
	
	public static void printDictSize(Dictionary dict) {
		System.out.println("Symptoms:\t" +  dict.getSymptom().size());
		System.out.println("Diseases:\t" +  dict.getDisease().size());
		System.out.println("Drugs:\t" +  dict.getMedication().size());
		System.out.println("Procedures:\t" +  dict.getProcedure().size());
		System.out.println("BodyParts:\t" +  dict.getBodyPart().size());
		System.out.println("Tests:\t" +  dict.getTest().size());
	}
}
