package g2html;

public class Main {

	public static void main(String[] args) {
		// Load configuration (default settings and/or from file)
		Config.load(args);
		
		// Start
		long startTime = System.currentTimeMillis();
		System.out.println("Create html files ...");
		
		// Parse xml result file
		XmlResult.parse();
		
		// Write css and js files
		Result.writeCssFile();
		Result.writeJsFile();
		
		// Write and complete html result files
		Result.writeHtmlFiles();
		
		// Write html report file
		Report.write();		

		// Finish (with process time)
		long finishTime = System.currentTimeMillis();
		System.out.println("Time needed: "+(finishTime-startTime)+" ms");
	}

}
