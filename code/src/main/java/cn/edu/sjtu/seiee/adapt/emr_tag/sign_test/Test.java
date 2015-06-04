package cn.edu.sjtu.seiee.adapt.emr_tag.sign_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

	public static void main(String args[]) {
		String s = null; 
        try {
             
            // using the Runtime exec method:
        	Process p = null;
        	File exeFile = new File("./res/tmp/crf.sh");
        	ProcessBuilder pb = new ProcessBuilder(exeFile.getAbsolutePath());
        	pb.directory(new File("./res/tmp/"));
        	p = pb.start();
        	BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
    
        	// read the output from the command
        	System.out.println("Here is the standard output of the command:\n");
        	while ((s = stdInput.readLine()) != null) {
        		System.out.println(s);
        	}
                
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
