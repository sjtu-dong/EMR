package cn.edu.sjtu.seiee.adapt.emr_tag.tagger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeTagger {

	public static String timeStr = "((今日|今晨|今天|昨晚|昨日|昨天|前天)(\\d+[点时]|上午|下午)?|入院后|本月初|"
			+ "出生后(第\\d+天)?|"
			+ "入院前(\\d+个月)?|"
			+ "[约近]?[数一二两三四五六七八九十百千万半几\\d]+(个|多|个多|余|个余|个半)?"
			+ "(天|月|年|周|日|时|小时|点|礼拜|星期)(多|半|许|余|左右)?[前后来]|"
			+ "近[数一二两三四五六七八九十百千万半几\\d]+(天|月|年|周|日|时|小时|点|礼拜|星期)|"
			+ "\\d{4}[-年－]\\d{1,2}[-月－]\\d{1,2}日?|\\d{4}\\.\\d{1,2}\\.\\d{1,2}|"
			+ "\\d{2}[-年－]\\d{1,2}[-月－]\\d{1,2}日?|\\d{2}\\.\\d{1,2}\\.\\d{1,2}|"
			+ "\\d{4}\\.(1[0-2]|0?\\d)|"
			+ "\\d{2,4}年\\d{1,2}月(初)?|(今年|去年|同年)?\\d{1,2}月\\d{1,2}[号日]|"
			+ "(今年|去年|同年)\\d{1,2}月(初)?|\\d+岁时|"
			+ "(\\d{2,4}|今|去)年(初|上半年|下半年)?|\\d{1,2}月后)"
			+ "|(?<=[于，,。者])(\\d{4}[-－]\\d{1,2}[-－]\\d{1,2}|\\d{4}\\.\\d{1,2}\\.\\d{1,2}|"
			+ "\\d{2}[-－]\\d{1,2}[-－]\\d{1,2}|\\d{2}\\.\\d{1,2}\\.\\d{1,2}|"
			+ "\\d{4}-\\d{1,2}|"
			+ "(1[0-2]|0?\\d)[-.](3[01]|[012]?\\d)|"
			+ "(3[01]|[012]?\\d)\\/(1[0-2]|0?\\d)|"
			+ "\\d{1,2}月(\\d{1,2}[号日]|前|余前|后)?|"
			+ "\\d{1,2}日)(?![天次])";
	public static Pattern timePattern = Pattern.compile(timeStr);
	
	public static List<Tag> tag(String s) {
		List<Tag> tags = new ArrayList<Tag>();
		Matcher ma = timePattern.matcher(s);
		while( ma.find() ) {
			tags.add(new Tag(ma.start(), "\\tim", ma.group()));
		}
		return tags;
	}
}
