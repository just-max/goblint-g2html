package g2html;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Main {
	public static void main(String[] args) {
		// Load configuration (default settings and/or from file)
		Config.load(args);

		// Start
		long startTime = System.currentTimeMillis();
		Log.println("Create html files ...");
		
		// Generate files
		Result res = null;
		try {
			res = new Result(Config.conf.getResultDir());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Error: Could not generate directory structure for the result.\n");
			System.exit(255);
		}

		Log.println("Done creating dir. structure.");

		// Parse xml result file
		ResultStats stats = null;
		try {
			stats = XmlResult.parse(res);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Error: Could not process the xml file.\n");
			System.exit(255);
		}

		Log.println("Done parsing xml.");

		// Thread pool for file processing tasks
		ExecutorService executorService = Executors.newFixedThreadPool(Config.conf.getNumThreads());

		// Process found files/functions
		for(String path : stats.allFiles()){
			File cFile = stats.getStats(path).getCFile();
			File listing = res.getListingFile(path);
			// check if input data is available
			if (cFile!=null && cFile.exists()){
				// process the input
				Runnable t = new ProcessCfile(cFile, listing, stats.getStats(path));
				executorService.submit(t);
			} else {
				// or replace with a placeholder
				try {
					Result.copyResource("missing.xml",listing);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(255);
				}
			}

			// process the functions for the current file
			for (String fun : stats.getStats(path).getFunctions()){
				File svgFile = res.getSvgFile(path, fun);
				File dotFile = Config.getFunDotFile(path, fun);
//				System.out.printf("dot file:%s\n",dotFile.toString());
				// check if input data is available
				if (dotFile.exists()){
					// process the input
					Runnable t1 = new ProcessDotFileToSvg(dotFile, svgFile);
					executorService.submit(t1);
				} else {
					// or replace with a placeholder
					try {
						Result.copyResource("missing.svg",svgFile);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(255);
					}
				}
			}
			Log.printf("Done parsing '%s'.\n", path);
		}

		Log.printf("Done parsing source files.\n");

		// print the report file
		try {
			stats.printReport(res);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Error: Could print report.\n");
			System.exit(255);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			System.out.printf("Error: Could write report xml file.\n");
			System.exit(255);
		}

		Log.printf("Done writing report file.\n");

		// wait till all files and functions have been processed
		try {
			executorService.shutdown();
			executorService.awaitTermination(10, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(255);
		}

		Log.printf("Done waiting for threads.\n");

		// Finish (with process time)
		long finishTime = System.currentTimeMillis();
		System.out.println("Time needed: "+(finishTime-startTime)+" ms");
	}

}
