package cn.edu.sjtu.seiee.adapt.emr_tag.segmenter;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

class POSTaggerDemo {

	private static final String basedir = "/home/dong/Documents/java-lib/stanford-postagger-full-2014-10-26/";
  private POSTaggerDemo() {}

  public static void main(String[] args) throws Exception {
//    if (args.length != 2) {
//      System.err.println("usage: java TaggerDemo modelFile fileToTag");
//      return;
//    }
	  MaxentTagger tagger =  new MaxentTagger(basedir + "models/chinese-distsim.tagger");
	  Segmenter seg = new StanfordSegmenter();
	  seg.init();
	  
	  BufferedReader br = new BufferedReader(new FileReader("res/1-400_samples.txt"));
	  String line;
	  
	  BufferedWriter bw = new BufferedWriter(new FileWriter("res/1-400_samples_postagged.txt"));
	  while( (line = br.readLine()) != null ) {
		  List<String> words = seg.segment(line);
		  List<Word> sent = Sentence.toUntaggedList(words);
		  List<TaggedWord> tSent = tagger.tagSentence(sent);
		  bw.write(Sentence.listToString(tSent, false));
		  bw.newLine();
	  }
	  bw.close();
	  br.close();
  }

}
