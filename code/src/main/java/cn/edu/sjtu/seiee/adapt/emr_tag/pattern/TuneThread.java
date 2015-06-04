package cn.edu.sjtu.seiee.adapt.emr_tag.pattern;

/**
 * tuning the parameter
 */
import java.util.ArrayList;
import java.util.List;

import cn.edu.sjtu.seiee.adapt.emr_tag.Evaluate;
import cn.edu.sjtu.seiee.adapt.emr_tag.Loader;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.CompleteSegmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.Segmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Dictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tagger;

public class TuneThread extends Thread {

	private static final int MAX_PRT = 10, MIN_PRT = 3, PRT_STEP = 1;
	private static final double MAX_PPT = 0.91, MIN_PPT = 0.3, PPT_STEP = 0.05;

	private List<List<Tag>> labeled = null;
	private List<List<String>> records = null;
	private List<List<String>> trainRecords = null;
	private int PRT;
	private double PPT;
	private String type;
	
	public TuneThread(int PRT, double PPT, String type, List<List<Tag>> labeled, 
			List<List<String>> records, List<List<String>> trainRecords) {
		this.PRT = PRT;
		this.PPT = PPT;
		this.type = type;
		this.labeled = labeled;
		this.records = records;
		this.trainRecords = trainRecords;
	}
	
	public void run() {
		Dictionary dict = new Dictionary();
		dict.init();
		Tagger tagger = new Tagger(dict);

		PatternIteration pi = new PatternIteration(dict, trainRecords, PPT, PRT, 2);
		pi.iterate(type);
		
		List<List<Tag>> tagged = new ArrayList<List<Tag>>();
		for( List<String> record: records) {
			List<Tag> tags = tagger.getTags(record);
			tagged.add(tags);
		}
		
		synchronized(this) {
			List<Double> result = new ArrayList<Double>();
			double fscore = Evaluate.getFScore(labeled, tagged, type, result);
			System.out.printf("PPT = %.2f\tPRT = %d\tPrecision: %.3f\t"
					+ "Recall: %.3f\tF-Score: %.3f\n", 
					PPT, PRT, result.get(0), result.get(1), fscore);
		}
	}
	
	public static void main(String[] args) {
		
		String type = "\\med";
		System.out.println(type);
		
		Dictionary dict = new Dictionary();
		dict.init();
		
		List<List<Tag>> labeled = Loader.loadLabeled("res/1-100_tune_v2.txt");
		List<List<Tag>> tagged = new ArrayList<List<Tag>>();
		
		List<String> testSamples = Loader.load("res/1-100_samples.txt");
		List<List<String>> testRecords = new ArrayList<List<String>>();
		Segmenter seg = new CompleteSegmenter();
		seg.init();
		for(String s: testSamples)
			testRecords.add(seg.segment(s));
		
		Tagger tagger = new Tagger(dict);
		for( List<String> record: testRecords) {
			List<Tag> tags = tagger.getTags(record);
			tagged.add(tags);
		}
		
		List<Double> result = new ArrayList<Double>();
		System.out.println("Symptoms");
		System.out.printf("F-Score: %.3f\n", Evaluate.getFScore(labeled, tagged, "\\sym", result));
		System.out.println("Diseases");
		System.out.printf("F-Score: %.3f\n", Evaluate.getFScore(labeled, tagged, "\\dis", result));
		System.out.println("Drugs");
		System.out.printf("F-Score: %.3f\n", Evaluate.getFScore(labeled, tagged, "\\med", result));
		System.out.println("Body Parts");
		System.out.printf("F-Score: %.3f\n", Evaluate.getFScore(labeled, tagged, "\\bod", result));
		System.out.println("Procedures");
		System.out.printf("F-Score: %.3f\n", Evaluate.getFScore(labeled, tagged, "\\pro", result));
		System.out.println("Tests");
		System.out.printf("F-Score: %.3f\n", Evaluate.getFScore(labeled, tagged, "\\tes", result));

		List<String> trainSamples = Loader.load("res/train.txt");
		List<List<String>> trainRecords = new ArrayList<List<String>>();
		for(String s: trainSamples) {
			trainRecords.add(seg.segment(s));
		}
		
		for(double i = MIN_PPT; i <= MAX_PPT; i += PPT_STEP)
			for( int j = MIN_PRT; j <= MAX_PRT; j += PRT_STEP) {
				TuneThread t = new TuneThread(j, i, type, labeled, testRecords, trainRecords);
				t.start();
			}
	}
}
