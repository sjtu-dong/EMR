package cn.edu.sjtu.seiee.adapt.emr_tag.sign_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.edu.sjtu.seiee.adapt.emr_tag.Evaluate;
import cn.edu.sjtu.seiee.adapt.emr_tag.Loader;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.CompleteSegmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.segmenter.Segmenter;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Dictionary;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tag;
import cn.edu.sjtu.seiee.adapt.emr_tag.tagger.Tagger;

public class Main {

	public static String[] tags = { "\\sym", "\\dis", "\\med", "\\bod",
			"\\pro", "\\tes" };

	public static void main(String[] args) throws IOException {
		Dictionary dict = new Dictionary();
		dict.init("res/dict/new1/");
		Segmenter seg = new CompleteSegmenter();
		seg.init();
		Tagger tagger = new Tagger(dict);

		List<String> samples = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(
				"res/1-400_test_v3.txt"));
		String line;
		while ((line = br.readLine()) != null)
			samples.add(line);
		br.close();

		BufferedWriter bw1 = new BufferedWriter(new FileWriter("res/ours"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("res/crfs"));
		List<List<Tag>> labeled = new ArrayList<List<Tag>>();
		List<List<Tag>> tagged = new ArrayList<List<Tag>>();
		List<String> testSamples = new ArrayList<String>();
		List<Double> result = new ArrayList<Double>();
		List<Integer> crfResult = null;

		for (int j = 0; j < 100; ++j) {
			Collections.shuffle(samples);
			
			//Our method
			for (int i = 0; i < 100; ++i) {
				String s = samples.get(i);
				testSamples.add(s.replaceAll("<.>", "").replaceAll("</.>", ""));
				labeled.add(Loader.convertStrToTags(s));
			}
			for (String s : testSamples) {
				List<String> record = seg.segment(s);
				tagged.add(tagger.getTags(record));
			}
			int totalLabeled = 0, totalTagged = 0, correctTagged = 0;
			for (String tag : tags) {
				Evaluate.getFScore(labeled, tagged, tag, result);
				totalLabeled += result.get(4);
				totalTagged += result.get(3);
				correctTagged += result.get(2);
			}
			double precision = 1.0 * correctTagged / totalTagged;
			double recall = 1.0 * correctTagged / totalLabeled;
			double fscore = 2 * precision * recall / (precision + recall);

			bw1.write(String.format("%.4f\t%.4f\t%.4f", precision, recall, fscore));
			bw1.newLine();
			bw1.flush();
			
			//CRF
			CRFFileGenerate(samples);
			
			Process p = null;
        	File exeFile = new File("./res/tmp/crf.sh");
        	ProcessBuilder pb = new ProcessBuilder(exeFile.getAbsolutePath());
        	pb.directory(new File("./res/tmp/"));
        	p = pb.start();
        	BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
    
        	// read the output from the command
        	System.out.println("Here is the standard output of the command:\n");
        	while ((line = stdInput.readLine()) != null) {
        		System.out.println(line);
        	}
        	
        	crfResult = evaluation("res/tmp/output.txt");
        	precision = 1.0 * crfResult.get(0) / crfResult.get(1);
			recall = 1.0 * crfResult.get(0) / crfResult.get(2);
			fscore = 2 * precision * recall / (precision + recall);

			bw2.write(String.format("%.4f\t%.4f\t%.4f", precision, recall, fscore));
			bw2.newLine();
			bw2.flush();
			
			labeled.clear();
			tagged.clear();
			testSamples.clear();
		}
		
		bw1.flush();
		bw1.close();
		bw2.flush();
		bw2.close();
	}

	public static void CRFFileGenerate(List<String> samples) {
		String folder = "res/tmp/";

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(folder
					+ "test"));
			for (int i = 0; i < 100; ++i) {
				String s = samples.get(i).replaceAll("<.>", "")
						.replaceAll("</.>", "");
				List<Tag> tags = Loader.convertStrToTags(samples.get(i));
				bw.write(tagsToVertical(s, tags));
				bw.newLine();
			}
			bw.close();
			
			bw = new BufferedWriter(new FileWriter(folder
					+ "train"));
			for (int i = 100; i < 400; ++i) {
				String s = samples.get(i).replaceAll("<.>", "")
						.replaceAll("</.>", "");
				List<Tag> tags = Loader.convertStrToTags(samples.get(i));
				bw.write(tagsToVertical(s, tags));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String tagsToVertical(String s, List<Tag> tags) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		Tag tag = tags.get(i);
		for (int j = 0; j < s.length(); ++j) {
			if (j == tag.pos) {
				switch (tag.tagName) {
				case "\\sym":
					sb.append(s.charAt(j) + "\tB-S\n");
					break;
				case "\\dis":
					sb.append(s.charAt(j) + "\tB-D\n");
					break;
				case "\\pro":
					sb.append(s.charAt(j) + "\tB-P\n");
					break;
				case "\\med":
					sb.append(s.charAt(j) + "\tB-M\n");
					break;
				case "\\tes":
					sb.append(s.charAt(j) + "\tB-T\n");
					break;
				case "\\bod":
					sb.append(s.charAt(j) + "\tB-B\n");
					break;
				}
				for (int k = 1; k < tag.word.length(); ++k) {
					++j;
					switch (tag.tagName) {
					case "\\sym":
						sb.append(s.charAt(j) + "\tI-S\n");
						break;
					case "\\dis":
						sb.append(s.charAt(j) + "\tI-D\n");
						break;
					case "\\pro":
						sb.append(s.charAt(j) + "\tI-P\n");
						break;
					case "\\med":
						sb.append(s.charAt(j) + "\tI-M\n");
						break;
					case "\\tes":
						sb.append(s.charAt(j) + "\tI-T\n");
						break;
					case "\\bod":
						sb.append(s.charAt(j) + "\tI-B\n");
						break;
					}
				}
				++i;
				if (i < tags.size()) {
					tag = tags.get(i);
				}
			} else
				sb.append(s.charAt(j) + "\tO\n");
		}
		return sb.toString();
	}
	
	public static List<Integer> evaluation(String resultFile) throws IOException {
		List<Integer> result = new ArrayList<Integer>();
		int[] tp = new int[]{0,0,0,0,0,0};
		int[] tp_fp = new int[]{0,0,0,0,0,0}; //检测出来的
		int[] tp_fn = new int[]{0,0,0,0,0,0}; //正确的
		
		FileInputStream fisL = new FileInputStream(resultFile);
		InputStreamReader isrL = new InputStreamReader(fisL);
		BufferedReader reader = new BufferedReader(isrL);
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = "";
		while((line = reader.readLine()) != null){
			if(!line.equals("")){
				lines.add(line);
			}
		}
		reader.close();
		
		countD(lines,tp,tp_fp,tp_fn,0,"D");
		countD(lines,tp,tp_fp,tp_fn,1,"M");
		countD(lines,tp,tp_fp,tp_fn,2,"B");
		countD(lines,tp,tp_fp,tp_fn,3,"P");
		countD(lines,tp,tp_fp,tp_fn,4,"S");
		countD(lines,tp,tp_fp,tp_fn,5,"T");
		
		int a = 0, b = 0, c = 0;
		for(int i = 0;i < 6;i++){
			a += tp[i];
			b += tp_fp[i];
			c += tp_fn[i];	
		}
		result.add(a);
		result.add(b);
		result.add(c);
		return result;
	}
	
	private static void countD(ArrayList<String> lines, int[] tp, int[] tp_fp,
			int[] tp_fn, int i, String str) {
		for(int num = 0;num < lines.size();num++){
			String line = lines.get(num);
			String[] parts = line.split("\t");
			if(parts[1].equals("B-"+str)){
				tp_fn[i]++;
				if(parts[2].equals("B-"+str)){
					tp_fp[i]++;
					num++;
					while(num<lines.size()){
							line = lines.get(num);
							String[] parts2 = line.split("\t");
							if(parts2[1].equals("I-"+str) && parts2[1].equals(parts2[2])){num++;}
							else if(parts2[1].equals("O") && parts2[1].equals(parts2[2])){tp[i]++;break;}
							else if(parts2[1].startsWith("I-"+str) && parts2[2].startsWith("O")){break;}
							else if(parts2[2].startsWith("I-"+str) && parts2[1].startsWith("O")){break;}
							else if(parts2[1].startsWith("B-") && parts2[2].startsWith("O")){tp[i]++;num--;break;}
							else if(parts2[2].startsWith("B-") && parts2[1].startsWith("O")){tp[i]++;num--;break;}
							else if(parts2[1].startsWith("B-") && parts2[2].startsWith("B-")){tp[i]++;num--;break;}
							else if(parts2[1].startsWith("B-") && parts2[2].startsWith("I-"+str)){num--;break;}
							else if(parts2[2].startsWith("B-") && parts2[1].startsWith("I-"+str)){num--;break;}
					}
				}
			}
			else if(parts[2].equals("B-"+str)){
				tp_fp[i]++;
			}
		}
	}
}
