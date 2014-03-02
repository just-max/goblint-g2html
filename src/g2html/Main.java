package g2html;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		// Load configuration (default settings and/or from file)
		Config.load(args);
		LOGGER.setLevel(Level.SEVERE);

		// Start
		long startTime = System.currentTimeMillis();
		System.out.println("Create html files ...");
		
		// Generate files
		Result res = null;
		try {
			res = new Result(Config.resultDir);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Error: Could not generate directory structure for the result.\n");
			System.exit(255);
		}

		// Parse xml result file
		ResultStats stats = null;
		try {
			stats = XmlResult.parse(res);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Error: Could not process the xml file.\n");
			System.exit(255);
		}

		// Process found files/functions
		for(String file : stats.allFiles()){
			new ProcessCfile(stats.getStats(file).getCFile(), res.getListingFile(file)).start();
			for (String fun : stats.getStats(file).getFunctions())
				new ProcessDotFileToSvg(Config.getFunDotFile(file, fun), res.getSvgFile(file, fun)).start();
		}

		try {
			stats.printJson(res);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.printf("Error: Could not write json files.\n");
			System.exit(255);
		}

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

		// Finish (with process time)
		long finishTime = System.currentTimeMillis();
		System.out.println("Time needed: "+(finishTime-startTime)+" ms");
	}

}
