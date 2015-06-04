package cn.edu.sjtu.seiee.adapt.emr_tag.tagger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnrichDictionary {

	private static Set<String> prefix1 = null, prefix2 = null, prefix3 = null;
	
	public static void bodyPrefix(List<List<Tag>> tagList,
			Dictionary dict) {
		
		for( int i = 0; i < tagList.size(); ++ i) {
			List<Tag> tags = tagList.get(i);
			for( int j = 0; j < tags.size(); ++ j) {
				Tag tag = tags.get(j);
				String s = "";
				while( tag.tagName.equals("\\bod") && j + 1 < tags.size()
						&& tag.endIndex == tags.get(j + 1).startIndex) {
					s += tag.word;
					tag = tags.get(++ j);
				}
				if( !s.equals("") ) {
					dict.getTerms(tag.tagName).add(s + tag.word);
				}
			}
		}
	}
	
	public static void otherPrefix(List<List<Tag>> tagList, List<String> records,
			Dictionary dict) {
		if( prefix1 == null )
			init();
		
		if( tagList.size() != records.size() ) {
			System.err.println("Doesn't match");
			return;
		}
		
		for( int i = 0; i < tagList.size(); ++ i) {
			List<Tag> tags = tagList.get(i);
			String s = records.get(i);
			for( Tag tag: tags ) {
				if( !tag.tagName.equals("\\med")) {
					int pos = tag.pos;
					if( pos > 0 ) {
						String prefix = "";
						for( int j = pos - 1; j >= 0 && prefix1.contains(String.valueOf(s.charAt(j))); -- j) {
							prefix = s.charAt(j) + prefix;
						}
						if( !prefix.equals("")) {
							dict.getTerms(tag.tagName).add(prefix + tag.word);
						}
					}
					if( pos > 1 ) {
						String prefix = s.substring(pos - 2, pos);
						if( prefix2.contains(prefix)) {							
							dict.getTerms(tag.tagName).add(prefix + tag.word);
						}
						else if( prefix3.contains(prefix) && tag.tagName.equals("\\dis")) {
							dict.getTerms(tag.tagName).add(prefix + tag.word);
						}
					}
				}
			}
		}
	}
	
	private static void init() {
		prefix1 = new HashSet<String>();
		String[] strs = {"左", "右", "双", "两", "上", "下"};
		for( int i = 0; i < strs.length; ++ i)
			prefix1.add(strs[i]);
		prefix2 = new HashSet<String>();
		String[] strs1 = {"左侧", "右侧", "两侧", "双侧"};
		for( int i = 0; i < strs1.length; ++ i)
			prefix2.add(strs1[i]);
		prefix3 = new HashSet<String>();
		prefix3.add("急性");
		prefix3.add("慢性");
	}
}
