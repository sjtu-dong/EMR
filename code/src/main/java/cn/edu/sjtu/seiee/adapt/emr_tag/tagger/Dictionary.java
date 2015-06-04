package cn.edu.sjtu.seiee.adapt.emr_tag.tagger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Dictionary {

	public static String defaultDictFolder = "res/dict/";
//	public static String defaultDictFolder = "/home/dong/Development/dong/dict/";
	private HashSet<String> disease;
	private HashSet<String> symptom;
	private HashSet<String> medication;
	private HashSet<String> bodyPart;
	private HashSet<String> procedure;
	private HashSet<String> test;

	public void init() {
		disease = loadDict(defaultDictFolder + "disease.txt");
		symptom = loadDict(defaultDictFolder + "symptom.txt");
		medication = loadDict(defaultDictFolder + "medication.txt");
		bodyPart = loadDict(defaultDictFolder + "bodyPart.txt");
		procedure = loadDict(defaultDictFolder + "procedure.txt");
		test = loadDict(defaultDictFolder + "test.txt");
	}

	public void init(String folder) {
		disease = loadDict(folder + "disease.txt");
		symptom = loadDict(folder + "symptom.txt");
		medication = loadDict(folder + "medication.txt");
		bodyPart = loadDict(folder + "bodyPart.txt");
		procedure = loadDict(folder + "procedure.txt");
		test = loadDict(folder + "test.txt");
	}
	
	public HashSet<String> getDisease() {
		return disease;
	}

	public void setDisease(HashSet<String> disease) {
		this.disease = disease;
	}

	public HashSet<String> getSymptom() {
		return symptom;
	}

	public void setSymptom(HashSet<String> symptom) {
		this.symptom = symptom;
	}

	public HashSet<String> getMedication() {
		return medication;
	}

	public void setMedication(HashSet<String> medication) {
		this.medication = medication;
	}

	public HashSet<String> getBodyPart() {
		return bodyPart;
	}

	public void setBodyPart(HashSet<String> bodyPart) {
		this.bodyPart = bodyPart;
	}

	public HashSet<String> getProcedure() {
		return procedure;
	}

	public void setProcedure(HashSet<String> procedure) {
		this.procedure = procedure;
	}

	public HashSet<String> getTest() {
		return test;
	}

	public void setTest(HashSet<String> test) {
		this.test = test;
	}

	public String termTag(String s) {
//		StringBuffer sb = new StringBuffer();
//		if( symptom.contains(s))
//			sb.append("\\sym");
//		if( disease.contains(s))
//			sb.append("\\dis");
//		if( medication.contains(s))
//			sb.append("\\med");
//		if( bodyPart.contains(s))
//			sb.append("\\bod");
//		if( procedure.contains(s))
//			sb.append("\\pro");
//		if( test.contains(s))
//			sb.append("\\tes");
//		return sb.toString();
		int len = s.length();
		if( symptom.contains(s))
			return "\\sym";
		if( disease.contains(s))
			return "\\dis";
		if( medication.contains(s))
			return "\\med";
		else if( len > 1 && (s.endsWith("针") || s.endsWith("片"))
				&& medication.contains(s.substring(0, len - 1)))
			return "\\med";
		else if( len > 2 && (s.endsWith("颗粒") || s.endsWith("胶囊"))
				&& medication.contains(s.substring(0, len - 2)))
			return "\\med";
		else if( len > 3 && (s.endsWith("口服液") || s.endsWith("混悬液") ||
				s.endsWith("混悬剂"))
				&& medication.contains(s.substring(0, len - 3)))
			return "\\med";
		if( bodyPart.contains(s))
			return "\\bod";
		else if( len > 1 && s.endsWith("部")
				&& bodyPart.contains(s.substring(0, len - 1)))
			return "\\bod";
		if( procedure.contains(s))
			return "\\pro";
		else if( len > 1 && s.endsWith("术")
				&& procedure.contains(s.substring(0, len - 1)))
			return "\\pro";
		else if( procedure.contains(s + "术"))
			return "\\pro";
		if( test.contains(s))
			return "\\tes";
		return "";
	}

	public Set<String> getTerms(String tagName) {
		switch(tagName) {
		case "\\dis": return disease;
		case "\\sym": return symptom;
		case "\\med": return medication;
		case "\\bod": return bodyPart;
		case "\\pro": return procedure;
		case "\\tes": return test;
		default: break;
		}
		return null;
	}

	public void enrichDict(String tagName, Set<String> newTerms) {
		switch(tagName) {
		case "\\dis": disease.addAll(newTerms); break;
		case "\\sym": symptom.addAll(newTerms); break;
		case "\\med": medication.addAll(newTerms); break;
		case "\\bod": bodyPart.addAll(newTerms); break;
		case "\\pro": procedure.addAll(newTerms); break;
		case "\\tes": test.addAll(newTerms); break;
		default: break;
		}
	}

	public static HashSet<String> loadDict(String file) {
		HashSet<String> dict = new HashSet<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while( (line = br.readLine()) != null ) {
				dict.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dict;
	}
}
