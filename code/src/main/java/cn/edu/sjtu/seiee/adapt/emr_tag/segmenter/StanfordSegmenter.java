package cn.edu.sjtu.seiee.adapt.emr_tag.segmenter;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class StanfordSegmenter implements Segmenter{ 
	
	private static final String basedir = "/home/dong/Documents/java-lib/stanford-segmenter-2014-10-26/data";

	private CRFClassifier<CoreLabel> segmenter;
	
	public void init() {
		try {
			System.setOut(new PrintStream(System.out, true, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	    Properties props = new Properties();
	    props.setProperty("sighanCorporaDict", basedir);
	    props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
	    props.setProperty("inputEncoding", "UTF-8");
	    props.setProperty("sighanPostProcessing", "true");

	    segmenter = new CRFClassifier<CoreLabel>(props);
	    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
	}

	public List<String> segment(String sentence) {
		return segmenter.segmentString(sentence);
	}

	
}
