package cn.edu.sjtu.seiee.adapt.emr_tag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;

public class Loader {

	public static Pattern pattern = Pattern.compile("<(.)>(.*?)</(\\1)>");

	public static List<String> load(String file) {
		List<String> records = new ArrayList<String>();

		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
				records.add(line);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return records;
	}

	public static List<Integer> loadIndex(String file) {
		List<Integer> index = new ArrayList<Integer>();
		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
				index.add(Integer.valueOf(line));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return index;
	}

	public static List<List<Tag>> loadLabeled(String file) {
		List<List<Tag>> tags = new ArrayList<List<Tag>>();
		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
				tags.add(convertStrToTags(line));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tags;
	}

	public static List<Tag> convertStrToTags(String line) {
		List<Tag> tags = new ArrayList<Tag>();
		Matcher m = pattern.matcher(line);
		int count = 0;
		while (m.find()) {
			String word = m.group(2);
			String tagname = "";
			switch (m.group(1)) {
			case "d":
				tagname = "\\dis";
				break;
			case "s":
				tagname = "\\sym";
				break;
			case "m":
				tagname = "\\med";
				break;
			case "b":
				tagname = "\\bod";
				break;
			case "p":
				tagname = "\\pro";
				break;
			case "t":
				tagname = "\\tes";
				break;
			default:
				System.out.println("error");
				break;
			}
			int start = m.start() - 7 * count;
			tags.add(new Tag(start, tagname, word));
			count ++;
		}
		return tags;
	}
}
