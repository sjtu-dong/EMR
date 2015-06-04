package cn.edu.sjtu.seiee.adapt.emr_tag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.CompleteSegmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.Segmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Dictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tagger;
import cn.edu.sjtu.seiee.adapt.emr_tag.util.Sort;

public class Test {

	public static void main(String[] args) {
		tagStat("res/1-400_test_v3.txt");
		check();
		checkFile("res/1-400_samples.txt", "res/1-400_test_v3.txt");
//		tagStat("res/1-100_tune_v2.txt");
//		check();
//		checkFile("res/1-100_samples.txt", "res/1-100_tune_v2.txt");
	}
	
	//考察在core dict上的情况
	public static void check() {
		Dictionary dict = new Dictionary();
		dict.init("res/dict/new1/");
		//dict.init();
		List<List<Tag>> labeled = Loader.loadLabeled("res/1-400_test_v3.txt");
		
		List<List<Tag>> tagged = new ArrayList<List<Tag>>();
		
		List<String> testSamples = Loader.load("res/1-400_samples.txt");
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
		System.out.println(String.valueOf(Evaluate.getFScore(labeled, tagged, "\\dis", result)));
		System.out.println(String.valueOf(Evaluate.getFScore(labeled, tagged, "\\med", result)));
		System.out.println(String.valueOf(Evaluate.getFScore(labeled, tagged, "\\bod", result)));
		System.out.println(String.valueOf(Evaluate.getFScore(labeled, tagged, "\\pro", result)));
		System.out.println(String.valueOf(Evaluate.getFScore(labeled, tagged, "\\sym", result)));
		System.out.println(String.valueOf(Evaluate.getFScore(labeled, tagged, "\\tes", result)));
		try {
			Test2.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Integer> get(List<List<Tag>> tagged, String tagName) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for( List<Tag> tags: tagged)
			for(Tag tag: tags)
				if( tag.tagName.equals(tagName)) {
					String w = tag.word;
					if( map.containsKey(w))
						map.put(w, map.get(w) + 1);
					else
						map.put(w, 1);
				}
		return map;
	}
	//检查添加label有没有改变源文件
	public static void checkFile(String source, String labeled) {
		List<String> samples = Loader.load(source);
		List<String> taggedSamples = Loader.load(labeled);
		
		System.out.println(samples.size() + "\t" + taggedSamples.size());
		for( int i = 0; i < samples.size(); ++ i) {
			String s = taggedSamples.get(i).replaceAll("<[bdsptm]>", "")
					.replaceAll("</[bdsptm]>", "");
			if( !s.equals(samples.get(i))) {
				System.out.println(i);
				System.out.println(s);
				System.out.println(samples.get(i));
			}
		}
	}

	//统计label的情况
	public static void tagStat(String file) {
		List<List<Tag>> tags = Loader
				.loadLabeled(file);

		HashMap<String, Integer> diseases = new HashMap<String, Integer>(), symptoms = new HashMap<String, Integer>(), bodyParts = new HashMap<String, Integer>(), procedures = new HashMap<String, Integer>(), medications = new HashMap<String, Integer>(), tests = new HashMap<String, Integer>();

		for (List<Tag> tagList : tags)
			for (Tag tag : tagList) {
				String key = tag.word;
				switch (tag.tagName) {
				case "\\dis":
					if (diseases.containsKey(key))
						diseases.put(key, diseases.get(key) + 1);
					else
						diseases.put(key, 1);
					break;
				case "\\sym":
					if (symptoms.containsKey(key))
						symptoms.put(key, symptoms.get(key) + 1);
					else
						symptoms.put(key, 1);
					break;
				case "\\bod":
					if (bodyParts.containsKey(key))
						bodyParts.put(key, bodyParts.get(key) + 1);
					else
						bodyParts.put(key, 1);
					break;
				case "\\pro":
					if (procedures.containsKey(key))
						procedures.put(key, procedures.get(key) + 1);
					else
						procedures.put(key, 1);
					break;
				case "\\med":
					if (medications.containsKey(key))
						medications.put(key, medications.get(key) + 1);
					else
						medications.put(key, 1);
					break;
				case "\\tes":
					if (tests.containsKey(key))
						tests.put(key, tests.get(key) + 1);
					else
						tests.put(key, 1);
					break;
				default:
					System.err.println("ERROR!" + tag.tagName);
				}
			}

		List<Entry<String, Integer>> disList = Sort.sort(diseases), symList = Sort
				.sort(symptoms), tesList = Sort.sort(tests), proList = Sort
				.sort(procedures), medList = Sort.sort(medications), bodList = Sort
				.sort(bodyParts);

		write("res/stat/diseases.txt", disList);
		write("res/stat/symptoms.txt", symList);
		write("res/stat/procedures.txt", proList);
		write("res/stat/bodyParts.txt", bodList);
		write("res/stat/tests.txt", tesList);
		write("res/stat/medications.txt", medList);
		
	}

	private static void write(String filename, List<Entry<String, Integer>> list) {
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(filename));

			for (Entry<String, Integer> entry : list)
				br.write(entry.getKey() + "\t" + entry.getValue() + "\n");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
