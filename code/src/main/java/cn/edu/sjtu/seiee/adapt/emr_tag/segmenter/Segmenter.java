package cn.edu.sjtu.seiee.adapt.emr_tag.segmenter;

import java.util.List;

public interface Segmenter {
	void init();
	List<String> segment(String sentence);
}
