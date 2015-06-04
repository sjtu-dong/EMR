package cn.edu.sjtu.seiee.adapt.emr_tag.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Dictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tagger;

public class PatternIteration {

	static Logger logger = Logger.getLogger(PatternIteration.class.getName());
	
	private static final int MAX_BOUND = 3;
	private static final String CHINESE_CHAR = "[\u4e00-\u9fa5]";
	private Dictionary dict;
	private Tagger tagger;
	private List<List<String>> records;
	private double PPT = 0.60;
	private int PRT = 10;
	private int MTP = 1;

	public PatternIteration(Dictionary dict, List<List<String>> records) {
		this.dict = dict;
		this.tagger = new Tagger(dict);
		this.records = records;
	}

	public PatternIteration(Dictionary dict, List<List<String>> records,
			double PPT, int PRT, int MTP) {
		this.dict = dict;
		this.tagger = new Tagger(dict);
		this.records = records;
		this.PPT = PPT;
		this.PRT = PRT;
		this.MTP = MTP;
	}
	public void iterate(String tagName) {
		System.currentTimeMillis();
		
		Set<String> terms = dict.getTerms(tagName);
		List<List<String>> taggedRecords = new ArrayList<List<String>>();
		Set<String> usedPatterns = new HashSet<String>();
		Set<String> newPatterns = new HashSet<String>();
		Set<String> newTerms = new HashSet<String>();
		List<Pattern> patternList = new ArrayList<Pattern>();
		
		do {
			System.out.println("New Terms: " + newTerms.size());
//			System.out.println("Total Terms Before Enrichment: " + terms.size());
			dict.enrichDict(tagName, newTerms);
			newTerms.clear();
			newPatterns.clear();
			taggedRecords.clear();
			
			//用字典标记records，生成candidate pattern
			Map<String, Set<String>> candPatterns = new HashMap<String, Set<String>>();
			for (List<String> words : records) {
				List<Tag> tags = tagger.getTags(words);
//				for( Tag tag: tags)
//					if( tag.tagName.contains(tagName)) {
//					}
//				List<String> replaced = replaceTags(tags, words, tagName);
//				taggedRecords.add(replaced);
				generateNewPattern(candPatterns, words, tagName, tags);
			}

//			for( String key: candPatterns.keySet())
//				System.out.println(key + "\t" + candPatterns.get(key));
			//过滤掉可信度比较低的pattern
			for( String key: candPatterns.keySet()) {
				if( !usedPatterns.contains(key)) {
					if( candPatterns.get(key).size() > PRT)
						newPatterns.add(key);
				}
			}
			String all = "";
			all = putTogether(records);

			for (String s : newPatterns) {
				Pattern p = Pattern.compile(s);
				HashSet<String> reg = new HashSet<String>();
				Matcher m = p.matcher(all);
				while (m.find()) {
					reg.add(m.group(1));
				}
				int c1 = 0;
				for( String s1: reg)
					if( terms.contains(s1))
						++ c1;
				if( 1.0 * c1 / reg.size() > PPT) {
					patternList.add(p);
					usedPatterns.add(s);
				}
			}
			removeExtraPattern(patternList, tagName);
			for( Pattern p: patternList)
				usedPatterns.add(p.pattern());
//			System.out.println("Tagged Terms: " + c);
//			System.out.println("New Patterns: " + newPatterns.size());
//			System.out.println("Useful Patterns: " + usedPatterns.size());
//			System.out.println("Pattern List Size: " + patternList.size());
			
			//获取新的term
			Map<String, HashSet<Pattern>> map = new HashMap<String, HashSet<Pattern>>();

			for (Pattern p : patternList) {
//				System.out.println(p.pattern());
				Matcher m = p.matcher(all);
				while (m.find()) {
					String key = m.group(1);
					if (!key.equals(tagName) && !terms.contains(key)) {
						if (!map.containsKey(key))
							map.put(key, new HashSet<Pattern>());
						map.get(key).add(p);
					}
				}
			}

			for (String key : map.keySet()) {
				if (key.length() > 1 && map.get(key).size() >= MTP) {
//					System.out.println(key + "\t" + map.get(key));
					newTerms.add(key);
				}
			}
//			System.out.println("Spend: " + (System.currentTimeMillis() - start));
		} while (newTerms.size() > 0);
	}

	private void generateNewPattern(Map<String, Set<String>> map,
			List<String> words, String tagName, List<Tag> tags) {
		String sequence = "(" + CHINESE_CHAR + "+?)";
		for(Tag tag: tags) {
			if (tag.tagName.equals(tagName)) {
				String start = "", end = "";
				String s1, s2;
				for (int j = 1; j <= MAX_BOUND && (tag.startIndex - j) >= 0; ++j) {
					String tmp = words.get(tag.startIndex - j);
					if( tmp.equals("，") || tmp.equals("。"))
						break;
					start = tmp + start;
					end = "";
					for (int k = 0; k < MAX_BOUND && (tag.endIndex + k) < words.size(); ++k) {
						String tmp1 = words.get(tag.endIndex + k);
						end += tmp1;
						if (start.equals(""))
							s1 = "^";
						else
							s1 = convertToRegStr(start);
						if (end.equals(""))
							s2 = "$";
						else
							s2 = convertToRegStr(end);
						String p = s1 + sequence + s2;
						if( !map.containsKey(p))
							map.put(p, new HashSet<String>());
						map.get(p).add(tag.word);
						if( tmp1.equals("，") || tmp1.equals("。")) break;
					}
				}
			}
		}
	}

	private static String putTogether(List<List<String>> records) {
		StringBuffer sb = new StringBuffer();

		for (List<String> record : records) {
			for (String w : record)
				sb.append(w);
			sb.append("\n");
		}
		return sb.toString();
	}

	// 将标记出来的word替换为标记
	public static List<String> replaceTags(List<Tag> tags, List<String> words, String tagName) {
		int start = 0;
		List<String> replaced = new ArrayList<String>();
		for (Tag tag : tags) {
			if( tag.tagName.contains(tagName)) {
				replaced.addAll(words.subList(start, tag.startIndex));
				replaced.add(tag.tagName);
				start = tag.endIndex;
			}
		}
		replaced.addAll(words.subList(start, words.size()));
		return replaced;
	}

	private static String convertToRegStr(String s) {
		return s.replace("\\", "\\\\").replace("?", "\\?").replace("(", "\\(")
				.replace(")", "\\)").replace("+", "\\+").replace("*", "\\*")
				.replace("]", "\\]").replace("[", "\\[").replace(".", "\\.");
	}
	
	private static void removeExtraPattern(List<Pattern> patterns, String tagName) {
		
		for( int i = patterns.size() - 1; i >= 0; -- i) {
			String s = patterns.get(i).pattern();
			for( Pattern p: patterns) {
				String pattern = p.pattern();
//				int pos = pattern.indexOf(tagName);
//				if( pos > 0 && pattern.indexOf(tagName, pos + 1) > 0)
//					continue;
				if( pattern.equals(s))
					continue;
				else if( pattern.contains(s) ) {
					patterns.remove(i);
					break;
				}
			}
		}
	}
}
