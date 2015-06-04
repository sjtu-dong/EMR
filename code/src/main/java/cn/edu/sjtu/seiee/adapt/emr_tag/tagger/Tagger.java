package cn.edu.sjtu.seiee.adapt.emr_tag.tagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tagger {

	private static final int MAX_WINDOW_SIZE = 20;
	private Dictionary dict;

	public Tagger(Dictionary dict) {
		this.dict = dict;
	}
	
	public Tagger() {
		dict = new Dictionary();
		dict.init();
	}

	public List<Tag> getTags(List<String> paragraph) {
		List<Tag> tags = new ArrayList<Tag>();
		boolean use[] = new boolean[paragraph.size()];
		Arrays.fill(use, false);
		for (int window = MAX_WINDOW_SIZE; window != 0; --window)
			tag(paragraph, window, use, tags);
		Collections.sort(tags);
		return tags;
	}
	
	private void tag(List<String> paragraph, int windowSize, boolean[] use,
			List<Tag> tags) {
		int end = paragraph.size() - windowSize + 1;
		int pos = 0;
		for (int start = 0; start < end; ++start) {
			if (use[start] || use[start + windowSize - 1]) {
				pos += paragraph.get(start).length();
				continue;
			}
			String s = "";
			for (int i = 0; i < windowSize; i++)
				s += paragraph.get(start + i);
			String tag = dict.termTag(s);
			if ( !tag.equals("")) {
				tags.add(new Tag(start, start + windowSize, pos, tag, s));
				Arrays.fill(use, start, start + windowSize, true);
			}
			pos += paragraph.get(start).length();
		}
	}

	public static void main(String[] args) {
		String s = "缘患者于10余年前开始出现反复头晕不适，到我院门诊治疗，血压最高达210/105mmHg，诊断为高血压病3级，予降压药治疗（具体用药不详)，症状缓解后门诊随诊。后一直于门诊治疗，自诉平时血压控制良好，血压多维持于140/80mmHg，但头晕、头痛症状仍有反复。2011年09月28日其再发头晕不适，无明显头痛，无胸闷心悸，无恶心呕吐，无肢体麻木、乏力，并因不慎受凉后出现咳嗽，咯白黄痰，当时无发热，在我院门诊治疗，门诊予中药治疗，治疗后症状减轻不明显。10月9日其出现发热38.8℃，咳嗽较前加重，遂到我院二沙分院急诊就诊，急查血常规：WBC:14.3×10E9/L，NEUT:11.28×10E9/L；胸片提示：胸片：1、双下肺炎症，并左侧少量胸腔积液；2、主动脉硬化；3、胸椎侧弯。诊断为高血压病、肺部感染，予赖氨匹林、可乐必妥静滴后发热已退，咳嗽咯痰较前减轻。现为求进一步专科系统诊疗，由门诊拟“高血压病、肺炎”收入我科。";
		List<String> ss = new ArrayList<String>();
		for( int i = 0; i < s.length(); ++ i)
			ss.add(String.valueOf(s.charAt(i)));
		Tagger tagger = new Tagger();
		List<Tag> tags = tagger.getTags(ss);
		for( Tag tag: tags)
			System.out.println(tag.startIndex + "\t" +
					tag.tagName + "\t" + tag.word);
	}
}
