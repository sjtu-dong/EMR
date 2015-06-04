package cn.edu.sjtu.seiee.adapt.emr_tag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Dictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.TimeTagger;

public class Test2 {
	
	public static void main(String[] args) {
		List<String> samples = Loader.load("res/1-400_samples.txt");
		Collections.shuffle(samples);
		for( int i = 0; i < 20; ++ i) {
			List<Tag> tags = TimeTagger.tag(samples.get(i));
			System.out.println(samples.get(i));
			for( Tag tag: tags)
				System.out.println(tag.pos + "\t" + tag.word);
		}
	}
	
	public static void run() throws IOException {
		Dictionary dict = new Dictionary();
		dict.init();
		
		BufferedReader br = new BufferedReader(new FileReader("res/stat/diseases.txt"));
		String line;
		while( (line = br.readLine()) != null ) {
			String[] strs = line.split("\t");
			if( strs.length == 2 && dict.termTag(strs[0]).equals("\\dis"))
				System.out.println(line);
		}
		br.close();
	}
}
