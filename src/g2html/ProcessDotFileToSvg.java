package g2html;

import java.io.File;
import java.io.IOException;

public class ProcessDotFileToSvg extends Thread {
	private File to;
	private File from;

	ProcessDotFileToSvg(File from, File to){
		super();
		this.from = from;
		this.to = to;
	}

	@Override
	public void run() {
		super.run();
		String myCommand = "dot "+from.getAbsolutePath()+" -Tsvg -o "+to.getAbsolutePath();
		try {
			Runtime.getRuntime().exec(myCommand);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
