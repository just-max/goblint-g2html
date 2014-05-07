package g2html;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

class Worker extends Thread {
	final Process process;
	Integer exit;
	Worker(Process process) {
		this.process = process;
	}
	public void run() {
		try {
			exit = process.waitFor();
		} catch (InterruptedException ignore) {
		}
	}
}

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
		} catch (IOException | InterruptedException ignored) {
		}
		return false;
	}

	// perform the transformation
	@Override
	public void run() {
		Log.printf("Starting:%s.\n", from.getPath());
		try {
			String myCommand = Config.conf.getDotPath()+" "+from.getAbsolutePath()+" -Tsvg -o "+to.getAbsolutePath();
			Log.printf("Executing: '%s'\n",myCommand);
			Worker worker = new Worker(Runtime.getRuntime().exec(myCommand));
			worker.start();
			try {
				worker.join(Config.conf.getDotTimeout());
				if (!worker.isAlive()) {
					Log.printf("Finished:%s.\n", from.getPath());
					return;
				}
			} catch(InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
				throw ex;
			} finally {
				worker.process.destroy();
			}
			String myCommand2 = Config.conf.getAlternativeDotPath()+" "+from.getAbsolutePath()+" -Tsvg -o "+to.getAbsolutePath();
			Log.printf("Executing: '%s'\n",myCommand2);
			worker = new Worker(Runtime.getRuntime().exec(myCommand2));
			worker.start();
			try {
				worker.join(Config.conf.getDotTimeout());
				if (!worker.isAlive()) {
					Log.printf("Finished:%s.\n", from.getPath());
					return;
				}
			} catch(InterruptedException ex) {
				worker.interrupt();
				Thread.currentThread().interrupt();
				throw ex;
			} finally {
				worker.process.destroy();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Result.copyResource("missing.svg",to);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(255);
		}
	}
}

