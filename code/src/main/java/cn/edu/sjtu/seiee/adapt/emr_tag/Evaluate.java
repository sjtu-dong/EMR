package cn.edu.sjtu.seiee.adapt.emr_tag;

import java.util.List;

import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;

public class Evaluate {

	public static double getFScore(List<List<Tag>> labeled, List<List<Tag>> tagged,
			String tagName, List<Double> result) {
		result.clear();
		int totalLabeled = 0, totalTagged = 0, correctTagged = 0;
		
		if( labeled.size() != tagged.size() ) {
			System.err.println("Error");
			return 0;
		}
		
		for( int i = 0; i < tagged.size(); ++ i) {
			List<Tag> tags1 = labeled.get(i), tags2 = tagged.get(i);
			for( Tag tag: tags1) {
				if( tag.tagName.equals(tagName))
					++ totalLabeled;
			}
			
//			if( i == 167 ) {
//				for( Tag tag: tags1)
//				System.out.println(i + "\t" + tag.tagName + 
//						"\t" + tag.word + "\t" + tag.pos);
//				for( Tag tag: tags2)
//					System.out.println(i + "\t" + tag.tagName + 
//							"\t" + tag.word + "\t" + tag.pos);
//			}
			for( Tag tag: tags2) {
				if( tag.tagName.contains(tagName)) {
					++ totalTagged;
					for( Tag tag1: tags1)
						if( tag1.tagName.equals(tagName) 
							&& tag1.pos == tag.pos
							&& tag1.word.equals(tag.word) 
							&& tag.tagName.contains(tag1.tagName)) {
							++ correctTagged;
						}
//					if( !flag )
//						System.out.println(i + "\t" + tag.tagName + 
//								"\t" + tag.word + "\t" + tag.pos);
				}
			}
		}
		
		double precision = 1.0 * correctTagged / totalTagged;
		double recall = 1.0 * correctTagged / totalLabeled;
		result.add(precision);
		result.add(recall);
		result.add((double) correctTagged);
		result.add((double) totalTagged);
		result.add((double) totalLabeled);
		
//		System.out.printf("Precision: %.3f\nRecall: %.3f\n", precision, recall);
		System.out.println(correctTagged + "\t" + totalLabeled + "\t" + totalTagged);
		return 2 * precision * recall / ( precision + recall);
	}
}
