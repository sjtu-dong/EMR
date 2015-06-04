package cn.edu.sjtu.seiee.adapt.emr_tag.tagger;

public class Tag implements Comparable<Tag> {
	public int startIndex;
	public int endIndex;
	public int pos;
	public String tagName;
	public String word;

	public Tag(int pos, String tag, String w) {
		this.pos = pos;
		tagName = tag;
		word = w;
	}

	public Tag(int start, int end, int pos, String tag, String w) {
		startIndex = start;
		endIndex = end;
		this.pos = pos;
		tagName = tag;
		word = w;
	}

	public int compareTo(Tag tag1) {
		if ( pos > tag1.pos)
			return 1;
		else if (pos == tag1.pos)
			return 0;
		else
			return -1;
	}
}
