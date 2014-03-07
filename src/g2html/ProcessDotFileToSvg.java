package g2html;

import java.io.File;
import java.io.IOException;

public class ProcessDotFileToSvg implements Runnable {
	private File to;
	private File from;

	// allocate a new transformation task
	ProcessDotFileToSvg(File from, File to){
		super();
		this.from = from;
		this.to = to;
	}

	// test if the parameter can be found by the runtime
	static boolean testDotPath(String dot){
		try {
			Process p = Runtime.getRuntime().exec(dot + " -V");
			return p.waitFor()==0;
		} catch (IOException _) {
		} catch (InterruptedException _) {
		}
		return false;
	}

	// perform the transformation
	@Override
	public void run() {
		Log.printf("Starting:%s.\n", from.getPath());
		String myCommand = Config.conf.getDotPath()+" "+from.getAbsolutePath()+" -Tsvg -o "+to.getAbsolutePath();
		try {
			Log.printf("Executing: '%s'\n",myCommand);
			Runtime.getRuntime().exec(myCommand).waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.printf("Finished:%s.\n", from.getPath());
	}
}
