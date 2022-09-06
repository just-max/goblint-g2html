package g2html;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

class Worker implements Runnable {
	final List<Process> processes;
	Integer exit;
	Worker(List<Process> processes) {
		this.processes = processes;
	}
	public void run() {
		processes.forEach(p -> {
			try { p.waitFor(); }
			catch (InterruptedException ignore) {}
		});
	}
	public void stop() {
		processes.forEach(Process::destroy);
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
			Worker worker;
			File[] files = from.listFiles();
			// not a directory
			if (files == null) {
				String[] myCommand = new String[]{Config.conf.getDotPath(), from.getAbsolutePath(), "-Tsvg", "-o", to.getAbsolutePath()};
				Log.printf("Executing: '%s'\n", Arrays.toString(myCommand));
				worker = new Worker(List.of(Runtime.getRuntime().exec(myCommand)));
			}
			else {
				String[] packCommand = Stream.concat(
						Stream.of(Config.conf.getGvPackPath(), "-u"),
						Arrays.stream(files).sorted(Comparator.comparing(File::getName)).map(f -> f.getAbsolutePath()))
					.toArray(String[]::new);
				String[] dotCommand = { Config.conf.getDotPath(), "-Tsvg", "-o", to.getAbsolutePath() };
				worker = new Worker(
					ProcessBuilder.startPipeline(
						List.of(new ProcessBuilder(packCommand), new ProcessBuilder(dotCommand))));
			}
			Thread t = new Thread(worker);
			t.start();
			try {
				t.join(Config.conf.getDotTimeout());
				if (!t.isAlive()) {
					Log.printf("Finished:%s.\n", from.getPath());
					return;
				}
			} catch(InterruptedException ex) {
				t.interrupt();
				Thread.currentThread().interrupt();
				throw ex;
			} finally {
				worker.stop();
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

