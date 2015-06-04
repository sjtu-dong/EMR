package cn.edu.sjtu.seiee.adapt.emr_tag.segmenter;

import java.util.ArrayList;
import java.util.List;

public class CompleteSegmenter implements Segmenter {

	public void init() {
	}

	public List<String> segment(String sentence) {
		int length = sentence.length();
		List<String> ret = new ArrayList<String>();
		for(int i = 0; i < length; i++){
			ret.add(String.valueOf(sentence.charAt(i)));
		}
		return ret;
	}

}
