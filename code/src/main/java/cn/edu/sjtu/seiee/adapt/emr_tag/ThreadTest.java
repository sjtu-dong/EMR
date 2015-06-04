package cn.edu.sjtu.seiee.adapt.emr_tag;

public class ThreadTest extends Thread{
	
	private int i = 1000;
	private String s;
	
	public ThreadTest(int i, String s) {
		this.i = i;
		this.s = s;
	}
	
	public void run() {
			System.out.println(s);
			try {
				Thread.sleep(i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Done");
	}
	
	public static void main(String[] args) {
		ThreadTest t1 = new ThreadTest(1000, "Sleep 1s");
		t1.start();
		ThreadTest t2 = new ThreadTest(40000, "Sleep 10s");
		t2.start();
	}
}
